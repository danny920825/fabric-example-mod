package com.bitqba.screen;

import com.bitqba.storage.StorageNetwork;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class StorageScreenHandler extends ScreenHandler {
    public static final int STORAGE_SLOTS = 54;
    public static final int PAGE_SIZE = STORAGE_SLOTS;

    private static final int PROPERTY_PAGE = 0;
    private static final int PROPERTY_PAGE_COUNT = 1;
    private static final int PROPERTY_CONNECTIONS = 2;
    private static final int PROPERTY_ENTRIES = 3;
    private static final int PROPERTY_DEPTH_LIMITED = 4;

    private final SimpleInventory view = new SimpleInventory(STORAGE_SLOTS);
    private final PlayerInventory playerInventory;
    private final ArrayPropertyDelegate pageData = new ArrayPropertyDelegate(5);
    private final BlockPos connectorPos;
    private List<StorageNetwork.StoredStack> entries = List.of();
    private String query = "";
    private int page;

    public StorageScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos connectorPos) {
        super(ModScreenHandlers.STORAGE_DISPLAY, syncId);
        this.playerInventory = playerInventory;
        addProperties(pageData);
        this.connectorPos = connectorPos.toImmutable();

        for (int i = 0; i < STORAGE_SLOTS; i++) {
            int x = 8 + (i % 9) * 18;
            int y = 36 + (i / 9) * 18;
            addSlot(new Slot(view, i, x, y) {
                @Override public boolean canInsert(ItemStack stack) { return false; }
            });
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 190 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 244));
        }

        if (!playerInventory.player.getEntityWorld().isClient()) refresh();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return StorageNetwork.isOperational(player.getEntityWorld(), connectorPos)
                && player.squaredDistanceTo(connectorPos.getX() + 0.5, connectorPos.getY() + 1.5,
                        connectorPos.getZ() + 0.5) < 64.0;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < STORAGE_SLOTS) {
            if (player.getEntityWorld().isClient()) return;
            if (!canUse(player)) {
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
            if (getCursorStack().isEmpty()) {
                int index = page * PAGE_SIZE + slotIndex;
                if (index >= 0 && index < entries.size()) {
                    StorageNetwork.StoredStack entry = entries.get(index);
                    int amount = button == 1 ? 1 : entry.stack().getCount();
                    ItemStack taken = StorageNetwork.extract(player.getEntityWorld(), connectorPos, entry, amount);
                    if (!taken.isEmpty()) {
                        setCursorStack(taken);
                    } else {
                        player.sendMessage(Text.translatable("gui.storage_display.extraction_failed"), true);
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
            closeFor(player);
            return;
        }

        int index = page * PAGE_SIZE + slotIndex;
        if (index < 0 || index >= entries.size()) return;

        StorageNetwork.StoredStack entry = entries.get(index);
        int space = StorageNetwork.availableSpace(player.getInventory(), entry.stack());
        if (space <= 0) {
            player.sendMessage(Text.translatable("gui.storage_display.inventory_full"), true);
            return;
        }

        int amount = Math.min(space, entry.stack().getCount());
        ItemStack moved = StorageNetwork.extract(player.getEntityWorld(), connectorPos, entry, amount);
        if (moved.isEmpty()) {
            player.sendMessage(Text.translatable("gui.storage_display.extraction_failed"), true);
            refresh();
            return;
        }

        player.getInventory().insertStack(moved);
        if (!moved.isEmpty()) {
            // Should not happen because the space was computed first, but never delete items.
            player.dropItem(moved, false);
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

        int pageCount = Math.max(1, (entries.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        if (id == 0) page = Math.max(0, page - 1);
        else if (id == 1) page = Math.min(pageCount - 1, page + 1);
        else return false;
        refresh();
        return true;
    }

    public void setSearchQuery(String query) {
        if (playerInventory.player.getEntityWorld().isClient()) return;
        this.query = query.length() > 64 ? query.substring(0, 64) : query;
        page = 0;
        refresh();
    }

    public int getPage() { return pageData.get(PROPERTY_PAGE); }
    public int getPageCount() { return Math.max(1, pageData.get(PROPERTY_PAGE_COUNT)); }
    public int getConnectionCount() { return pageData.get(PROPERTY_CONNECTIONS); }
    public int getEntryCount() { return pageData.get(PROPERTY_ENTRIES); }
    public boolean isDepthLimited() { return pageData.get(PROPERTY_DEPTH_LIMITED) != 0; }

    private static void closeFor(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) serverPlayer.closeHandledScreen();
    }

    private void refresh() {
        if (playerInventory.player.getEntityWorld().isClient()) return;

        StorageNetwork.Listing listing = StorageNetwork.list(playerInventory.player.getEntityWorld(), connectorPos, query);
        entries = listing.entries();

        int pageCount = Math.max(1, (entries.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        page = Math.min(page, pageCount - 1);

        pageData.set(PROPERTY_PAGE, page);
        pageData.set(PROPERTY_PAGE_COUNT, pageCount);
        pageData.set(PROPERTY_CONNECTIONS, listing.connectionCount());
        pageData.set(PROPERTY_ENTRIES, entries.size());
        pageData.set(PROPERTY_DEPTH_LIMITED, listing.depthLimited() ? 1 : 0);

        view.clear();
        int start = page * PAGE_SIZE;
        for (int slot = 0; slot < PAGE_SIZE && start + slot < entries.size(); slot++) {
            view.setStack(slot, entries.get(start + slot).stack().copy());
        }
        sendContentUpdates();
    }
}
