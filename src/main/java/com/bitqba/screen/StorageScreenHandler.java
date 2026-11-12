package com.bitqba.screen;

import com.bitqba.ledger.LedgerService;
import com.bitqba.storage.StorageNetwork;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StorageScreenHandler extends ScreenHandler {
    public static final int STORAGE_SLOTS = 45;
    public static final int PAGE_SIZE = STORAGE_SLOTS;

    public static final int BUTTON_PREVIOUS_PAGE = 0;
    public static final int BUTTON_NEXT_PAGE = 1;
    public static final int BUTTON_SORT = 2;

    private static final int PROPERTY_PAGE = 0;
    private static final int PROPERTY_PAGE_COUNT = 1;
    private static final int PROPERTY_CONNECTIONS = 2;
    private static final int PROPERTY_ENTRIES = 3;
    private static final int PROPERTY_DEPTH_LIMITED = 4;
    private static final int PROPERTY_TRUNCATED = 5;
    private static final int PROPERTY_SORT = 6;

    /** Origin codes pack the linked branch index (1-5) plus the branch count for stacks nested in a shulker. */
    private static final int ORIGIN_NESTED = StorageNetwork.SIDES.length;

    public enum SortMode {
        NONE, NAME, COUNT;

        public SortMode next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    private final SimpleInventory view = new SimpleInventory(STORAGE_SLOTS);
    private final PlayerInventory playerInventory;
    private final ArrayPropertyDelegate pageData = new ArrayPropertyDelegate(7);
    private final ArrayPropertyDelegate originData = new ArrayPropertyDelegate(STORAGE_SLOTS);
    private final BlockPos sourcePos;
    private final boolean enderMode;
    private List<StorageNetwork.StoredStack> entries = List.of();
    private String query = "";
    private int page;
    private SortMode sortMode = SortMode.NONE;

    public StorageScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos sourcePos) {
        this(ModScreenHandlers.STORAGE_DISPLAY, syncId, playerInventory, sourcePos, false);
    }

    public StorageScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos sourcePos, boolean enderMode) {
        this(enderMode ? ModScreenHandlers.ENDER_DISPLAY : ModScreenHandlers.STORAGE_DISPLAY,
                syncId, playerInventory, sourcePos, enderMode);
    }

    private StorageScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory,
            BlockPos sourcePos, boolean enderMode) {
        super(type, syncId);
        this.playerInventory = playerInventory;
        this.enderMode = enderMode;
        addProperties(pageData);
        addProperties(originData);
        this.sourcePos = sourcePos.toImmutable();

        for (int i = 0; i < STORAGE_SLOTS; i++) {
            int x = 8 + (i % 9) * 18;
            int y = 33 + (i / 9) * 18;
            addSlot(new Slot(view, i, x, y) {
                @Override public boolean canInsert(ItemStack stack) { return false; }
            });
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 159 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 217));
        }

        if (!playerInventory.player.getEntityWorld().isClient()) refresh();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        if (enderMode) {
            return true;
        }
        World world = player.getEntityWorld();
        if (!StorageNetwork.isOperational(world, sourcePos)) return false;
        double centerY = sourcePos.getY() + 1.5;
        return player.squaredDistanceTo(sourcePos.getX() + 0.5, centerY,
                sourcePos.getZ() + 0.5) < 64.0;
    }

    private ItemStack extractEntry(PlayerEntity player, StorageNetwork.StoredStack entry, int amount) {
        if (enderMode) {
            return StorageNetwork.extractFrom(player.getEnderChestInventory(), entry, amount);
        }
        return StorageNetwork.extract(player.getEntityWorld(), sourcePos, entry, amount);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < STORAGE_SLOTS) {
            if (player.getEntityWorld().isClient()) return;
            if (!canUse(player)) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    LedgerService.onConnectionError(serverPlayer);
                }
                closeFor(player);
                return;
            }

            // Shift-click is the only transfer allowed from the catalog: it moves items to the
            // player inventory. It never deposits into the network.
            if (actionType == SlotActionType.QUICK_MOVE) {
                moveCatalogEntryToPlayer(player, slotIndex);
                return;
            }
            if (actionType != SlotActionType.PICKUP || (button != 0 && button != 1)) return;

            // A non-empty cursor means the player is trying to deposit. The catalog is read-only.
            if (!getCursorStack().isEmpty()) {
                player.sendMessage(Text.translatable("gui.arcanum_storage.cursor_busy"), true);
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    LedgerService.onConnectionError(serverPlayer);
                }
                return;
            }

            int index = page * PAGE_SIZE + slotIndex;
            if (index >= 0 && index < entries.size()) {
                StorageNetwork.StoredStack entry = entries.get(index);
                int amount = button == 1 ? 1 : entry.stack().getCount();
                ItemStack taken = extractEntry(player, entry, amount);
                if (!taken.isEmpty()) {
                    setCursorStack(taken);
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        LedgerService.onExtracted(serverPlayer, entry.stack(), !entry.path().isEmpty());
                    }
                } else {
                    player.sendMessage(Text.translatable("gui.arcanum_storage.extraction_failed"), true);
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        LedgerService.onConnectionError(serverPlayer);
                    }
                }
            }
            refresh();
            return;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        if (player.getEntityWorld().isClient() || slotIndex < 0 || slotIndex >= slots.size()) {
            return ItemStack.EMPTY;
        }
        // Items from the player inventory are never moved into the network.
        if (slotIndex >= STORAGE_SLOTS) return ItemStack.EMPTY;

        // Returning EMPTY stops vanilla's quick-move loop after a single catalog entry.
        moveCatalogEntryToPlayer(player, slotIndex);
        return ItemStack.EMPTY;
    }

    private void moveCatalogEntryToPlayer(PlayerEntity player, int slotIndex) {
        if (!canUse(player)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                LedgerService.onConnectionError(serverPlayer);
            }
            closeFor(player);
            return;
        }

        int index = page * PAGE_SIZE + slotIndex;
        if (index < 0 || index >= entries.size()) return;

        StorageNetwork.StoredStack entry = entries.get(index);
        int space = StorageNetwork.availableSpace(player.getInventory(), entry.stack());
        if (space <= 0) {
            player.sendMessage(Text.translatable("gui.arcanum_storage.inventory_full"), true);
            if (player instanceof ServerPlayerEntity serverPlayer) {
                LedgerService.onInventoryFull(serverPlayer);
            }
            return;
        }

        int amount = Math.min(space, entry.stack().getCount());
        ItemStack moved = extractEntry(player, entry, amount);
        if (moved.isEmpty()) {
            player.sendMessage(Text.translatable("gui.arcanum_storage.extraction_failed"), true);
            if (player instanceof ServerPlayerEntity serverPlayer) {
                LedgerService.onConnectionError(serverPlayer);
            }
            refresh();
            return;
        }

        player.getInventory().insertStack(moved);
        if (!moved.isEmpty()) {
            // Should not happen because the space was computed first, but never delete items.
            player.dropItem(moved, false);
        }
        if (amount < entry.stack().getCount()) {
            player.sendMessage(Text.translatable("gui.arcanum_storage.partial_move", amount), true);
        }
        if (player instanceof ServerPlayerEntity serverPlayer) {
            LedgerService.onExtracted(serverPlayer, entry.stack(), !entry.path().isEmpty());
        }
        refresh();
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (player.getEntityWorld().isClient()) return false;
        if (!canUse(player)) {
            closeFor(player);
            return false;
        }

        if (id == BUTTON_SORT) {
            sortMode = sortMode.next();
            refresh();
            return true;
        }

        int pageCount = Math.max(1, (entries.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        if (id == BUTTON_PREVIOUS_PAGE) page = Math.max(0, page - 1);
        else if (id == BUTTON_NEXT_PAGE) page = Math.min(pageCount - 1, page + 1);
        else return false;
        refresh();
        return true;
    }

    public void setSearchQuery(String query) {
        if (playerInventory.player.getEntityWorld().isClient()) return;
        this.query = query.length() > 64 ? query.substring(0, 64) : query;
        page = 0;
        refresh();
        if (!this.query.isBlank() && playerInventory.player instanceof ServerPlayerEntity serverPlayer) {
            LedgerService.onSearched(serverPlayer);
        }
    }

    public int getPage() { return pageData.get(PROPERTY_PAGE); }
    public int getPageCount() { return Math.max(1, pageData.get(PROPERTY_PAGE_COUNT)); }
    public int getConnectionCount() { return pageData.get(PROPERTY_CONNECTIONS); }
    public int getEntryCount() { return pageData.get(PROPERTY_ENTRIES); }
    public boolean isDepthLimited() { return pageData.get(PROPERTY_DEPTH_LIMITED) != 0; }
    public boolean isTruncated() { return pageData.get(PROPERTY_TRUNCATED) != 0; }
    public SortMode getSortMode() {
        int index = Math.max(0, Math.min(PROPERTY_SORT_MAX, pageData.get(PROPERTY_SORT)));
        return SortMode.values()[index];
    }

    private static final int PROPERTY_SORT_MAX = SortMode.values().length - 1;

    /**
     * Encoded origin of a visible catalog slot: 0 when empty, otherwise the linked
     * side index plus one, with {@link #ORIGIN_NESTED} added for shulker contents.
     */
    public int getOriginCode(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= STORAGE_SLOTS) return 0;
        return originData.get(slotIndex);
    }

    /** Human-readable origin for a catalog slot, or {@code null} when empty. */
    public Text originText(int code) {
        if (code <= 0) return null;
        if (enderMode) {
            return code > ORIGIN_NESTED
                    ? Text.translatable("gui.arcanum_storage.origin_shulker")
                    : Text.translatable("gui.arcanum_storage.origin_ender");
        }
        int sideIndex = (code - 1) % StorageNetwork.SIDES.length;
        Text side = Text.translatable("gui.arcanum_storage.side."
                + StorageNetwork.SIDES[sideIndex].asString());
        if (code > ORIGIN_NESTED) {
            return Text.translatable("gui.arcanum_storage.origin_nested", side);
        }
        return Text.translatable("gui.arcanum_storage.origin", side);
    }

    private static void closeFor(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) serverPlayer.closeHandledScreen();
    }

    private void refresh() {
        if (playerInventory.player.getEntityWorld().isClient()) return;

        StorageNetwork.Listing listing = enderMode
                ? StorageNetwork.listInventory(playerInventory.player.getEnderChestInventory(), sourcePos, query)
                : StorageNetwork.list(playerInventory.player.getEntityWorld(), sourcePos, query);
        entries = new ArrayList<>(listing.entries());
        sortEntries();

        if (!enderMode && playerInventory.player instanceof ServerPlayerEntity serverPlayer) {
            LedgerService.onDisplayOpened(serverPlayer, listing.connectionCount(),
                    listing.depthLimited(), listing.truncated());
        }

        int pageCount = Math.max(1, (entries.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        page = Math.min(page, pageCount - 1);

        pageData.set(PROPERTY_PAGE, page);
        pageData.set(PROPERTY_PAGE_COUNT, pageCount);
        pageData.set(PROPERTY_CONNECTIONS, listing.connectionCount());
        pageData.set(PROPERTY_ENTRIES, entries.size());
        pageData.set(PROPERTY_DEPTH_LIMITED, listing.depthLimited() ? 1 : 0);
        pageData.set(PROPERTY_TRUNCATED, listing.truncated() ? 1 : 0);
        pageData.set(PROPERTY_SORT, sortMode.ordinal());

        view.clear();
        for (int slot = 0; slot < STORAGE_SLOTS; slot++) {
            originData.set(slot, 0);
        }
        int start = page * PAGE_SIZE;
        for (int slot = 0; slot < PAGE_SIZE && start + slot < entries.size(); slot++) {
            StorageNetwork.StoredStack entry = entries.get(start + slot);
            view.setStack(slot, entry.stack().copy());
            originData.set(slot, originCode(entry));
        }
        sendContentUpdates();
    }

    private void sortEntries() {
        switch (sortMode) {
            case NAME -> entries.sort(Comparator
                    .comparing((StorageNetwork.StoredStack entry) -> entry.stack().getName().getString(),
                            String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(Comparator.comparingInt(
                            (StorageNetwork.StoredStack entry) -> entry.stack().getCount()).reversed())
                    .thenComparingInt(entry -> StorageNetwork.sideIndex(entry.chest().side())));
            case COUNT -> entries.sort(Comparator
                    .comparingInt((StorageNetwork.StoredStack entry) -> entry.stack().getCount()).reversed()
                    .thenComparing(entry -> entry.stack().getName().getString(), String.CASE_INSENSITIVE_ORDER));
            case NONE -> { }
        }
    }

    private static int originCode(StorageNetwork.StoredStack entry) {
        int code = 1 + StorageNetwork.sideIndex(entry.chest().side());
        return entry.path().isEmpty() ? code : code + ORIGIN_NESTED;
    }
}
