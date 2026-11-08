package com.bitqba.storage;

import com.bitqba.enchantment.ModEnchantments;
import com.bitqba.item.ModItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The Archive Cache death mechanic of Arcanum Binding.
 *
 * <p>When a player dies with a bound End Pouch, the pouch is consumed and the
 * armor, hotbar and offhand are sealed into a named shulker box. The cache is
 * stored in the player's ender chest; if the ender chest is full, it is handed
 * to the player when they respawn. The 27 backpack slots drop normally.</p>
 *
 * <p>A placed cache removes itself once its last item is taken, so repeated
 * deaths do not leave a trail of empty labeled shulker boxes.</p>
 */
public final class ArchiveCache {
    /** Hotbar (0-8), armor (36-39) and offhand (40). */
    private static final int[] SEALED_SLOTS = {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            36, 37, 38, 39, 40
    };

    /** The last slot that can hold a pouch directly in the player inventory. */
    private static final int LAST_POUCH_SLOT = PlayerInventory.OFF_HAND_SLOT;

    private static final String CACHE_NAME = "item.arcanum_storage.archive_cache";
    private static final String CACHE_LORE = "item.arcanum_storage.archive_cache.lore";
    private static final String MESSAGE_STORED = "message.arcanum_storage.archive_cache.stored";
    private static final String MESSAGE_OVERFLOW = "message.arcanum_storage.archive_cache.overflow";
    private static final String MESSAGE_DROPPED = "message.arcanum_storage.archive_cache.dropped";

    /**
     * Caches waiting for the player to respawn. {@link #overflow} is empty when the
     * cache already reached the ender chest and only the message is pending.
     */
    private record PendingCache(ItemStack overflow) {}

    /** Result of delivering a pending cache when the player respawns. */
    public enum Delivery {
        NONE,
        STORED,
        INVENTORY,
        DROPPED
    }

    private static final Map<UUID, PendingCache> PENDING = new HashMap<>();

    private ArchiveCache() {}

    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register(ArchiveCache::afterRespawn);
    }

    /**
     * Runs at the head of {@code PlayerEntity.dropInventory}. It removes the bound
     * pouch, the armor, the hotbar and the offhand from the dying inventory and
     * builds the cache. Vanilla then drops everything that is left.
     */
    public static void handleDeath(ServerPlayerEntity player) {
        if (player.isSpectator()) return;
        if (player.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) return;

        PlayerInventory inventory = player.getInventory();
        int pouchSlot = findBoundPouch(inventory);
        if (pouchSlot < 0) return;

        // A vanishing pouch follows the vanilla rule: it is destroyed, not sealed.
        if (isVanishing(inventory.getStack(pouchSlot))) return;

        List<ItemStack> sealed = new ArrayList<>();
        List<Integer> sealedSlots = new ArrayList<>();

        for (int slot : SEALED_SLOTS) {
            if (slot == pouchSlot) continue;

            ItemStack stack = inventory.getStack(slot);
            if (stack.isEmpty()) continue;
            // Other pouches (bound or not) are never sealed; they drop normally.
            if (stack.isOf(ModItems.END_POUCH)) continue;
            // Vanishing items are left in place so vanilla destroys them.
            if (isVanishing(stack)) continue;

            sealed.add(stack.copy());
            sealedSlots.add(slot);
        }

        // With an empty loadout the pouch is not consumed; it drops normally.
        if (sealed.isEmpty()) return;

        for (int slot : sealedSlots) {
            inventory.setStack(slot, ItemStack.EMPTY);
        }
        inventory.setStack(pouchSlot, ItemStack.EMPTY);

        ItemStack cache = createCache(sealed);
        EnderChestInventory enderChest = player.getEnderChestInventory();
        ItemStack overflow = enderChest.addStack(cache);
        enderChest.markDirty();

        PENDING.put(player.getUuid(), new PendingCache(overflow));
    }

    private static int findBoundPouch(PlayerInventory inventory) {
        for (int slot = 0; slot <= LAST_POUCH_SLOT; slot++) {
            if (ModEnchantments.isBoundPouch(inventory.getStack(slot))) {
                return slot;
            }
        }
        return -1;
    }

    private static boolean isVanishing(ItemStack stack) {
        return EnchantmentHelper.hasAnyEnchantmentsWith(
                stack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP);
    }

    private static ItemStack createCache(List<ItemStack> sealed) {
        ItemStack cache = new ItemStack(Items.SHULKER_BOX);
        cache.set(DataComponentTypes.CUSTOM_NAME,
                Text.translatable(CACHE_NAME)
                        .formatted(Formatting.GOLD)
                        .styled(style -> style.withItalic(false)));
        cache.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                Text.translatable(CACHE_LORE).formatted(Formatting.GRAY))));
        cache.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(sealed));
        return cache;
    }

    /**
     * Returns whether the block entity is an emptied Archive Cache. The cache keeps
     * its name so the player can identify it, but a placed cache destroys itself
     * as soon as it is empty and nobody is looking at it. That keeps repeated
     * deaths from filling the world with empty labeled shulker boxes.
     */
    public static boolean isEmptyArchiveCache(ShulkerBoxBlockEntity blockEntity) {
        if (!blockEntity.isEmpty()) return false;

        Text customName = blockEntity.getCustomName();
        return customName != null
                && customName.getContent() instanceof TranslatableTextContent content
                && CACHE_NAME.equals(content.getKey());
    }

    /**
     * Delivers the cache that could not reach the ender chest. Called by the
     * respawn hook before the feedback message is chosen.
     */
    public static Delivery deliverPending(ServerPlayerEntity player) {
        PendingCache pending = PENDING.remove(player.getUuid());
        if (pending == null) return Delivery.NONE;
        if (pending.overflow().isEmpty()) return Delivery.STORED;

        ItemStack cache = pending.overflow();
        if (player.getInventory().insertStack(cache)) {
            return Delivery.INVENTORY;
        }

        player.dropItem(cache, false);
        return Delivery.DROPPED;
    }

    private static void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer,
            boolean alive) {
        switch (deliverPending(newPlayer)) {
            case STORED -> newPlayer.sendMessage(
                    Text.translatable(MESSAGE_STORED).formatted(Formatting.GRAY), false);
            case INVENTORY -> newPlayer.sendMessage(
                    Text.translatable(MESSAGE_OVERFLOW).formatted(Formatting.GRAY), false);
            case DROPPED -> newPlayer.sendMessage(
                    Text.translatable(MESSAGE_DROPPED).formatted(Formatting.GRAY), false);
            case NONE -> {
            }
        }
    }
}
