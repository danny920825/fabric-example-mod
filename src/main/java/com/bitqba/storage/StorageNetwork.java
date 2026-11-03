package com.bitqba.storage;

import com.bitqba.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.ChestType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Resolves the connector's four repeater outputs and performs safe, extraction-only item mutations. */
public final class StorageNetwork {
    /**
     * Maximum depth that is explored below a chest slot.
     * The chest slot itself is depth 0, the contents of a shulker are depth 1 and
     * the contents of a shulker inside another shulker are depth 2.
     * Stacks at the limit are listed, but their own contents are not explored.
     */
    private static final int MAX_CONTAINER_DEPTH = 2;
    private static final Direction[] SIDES = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    private StorageNetwork() {}

    public record ChestStorage(BlockPos position, Inventory inventory) {}
    public record StoredStack(ChestStorage chest, int slot, List<Integer> path, ItemStack stack) {}
    public record Listing(List<StoredStack> entries, int connectionCount, boolean depthLimited) {}

    /**
     * The screen is only authoritative while both blocks are still present and connected.
     * Every search, page change and extraction calls this before touching an inventory.
     */
    public static boolean isOperational(World world, BlockPos connector) {
        return world.getBlockState(connector).isOf(ModBlocks.STORAGE_CONNECTOR)
                && world.getBlockState(connector.up()).isOf(ModBlocks.STORAGE_DISPLAY);
    }

    public static boolean isConnectedRepeater(World world, BlockPos connector, Direction side) {
        if (!isOperational(world, connector)) return false;

        BlockPos repeaterPos = connector.offset(side);
        BlockState repeater = world.getBlockState(repeaterPos);
        if (!repeater.isOf(Blocks.REPEATER) || repeater.get(RepeaterBlock.FACING) != side) return false;

        BlockPos chestPos = repeaterPos.offset(side);
        BlockState chestState = world.getBlockState(chestPos);
        if (!chestState.isOf(Blocks.CHEST)) return false;
        return ChestBlock.getInventory((ChestBlock) chestState.getBlock(), chestState, world, chestPos, true) != null;
    }

    public static List<ChestStorage> findChests(World world, BlockPos connector) {
        if (!isOperational(world, connector)) return List.of();

        List<ChestStorage> result = new ArrayList<>(4);
        Set<Long> seen = new HashSet<>();
        for (Direction side : SIDES) {
            if (!isConnectedRepeater(world, connector, side)) continue;

            BlockPos repeaterPos = connector.offset(side);
            BlockPos chestPos = repeaterPos.offset(side);
            BlockState state = world.getBlockState(chestPos);
            if (!state.isOf(Blocks.CHEST)) continue;

            ChestBlock chestBlock = (ChestBlock) state.getBlock();
            Inventory inventory = ChestBlock.getInventory(chestBlock, state, world, chestPos, true);
            if (inventory == null) continue;

            BlockPos canonical = chestPos;
            if (state.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                canonical = chestPos.offset(ChestBlock.getFacing(state));
                if (canonical.asLong() > chestPos.asLong()) canonical = chestPos;
            }
            if (seen.add(canonical.asLong())) result.add(new ChestStorage(canonical.toImmutable(), inventory));
        }
        return result;
    }

    public static Listing list(World world, BlockPos connector, String query) {
        List<ChestStorage> chests = findChests(world, connector);
        String needle = query.toLowerCase(Locale.ROOT).trim();
        List<StoredStack> result = new ArrayList<>();
        boolean[] depthLimited = {false};

        for (ChestStorage chest : chests) {
            for (int slot = 0; slot < chest.inventory().size(); slot++) {
                ItemStack root = chest.inventory().getStack(slot);
                if (root.isEmpty()) continue;
                collect(chest, slot, root, new ArrayList<>(), needle, result, depthLimited, 0);
            }
        }
        return new Listing(List.copyOf(result), chests.size(), depthLimited[0]);
    }

    private static void collect(ChestStorage chest, int rootSlot, ItemStack stack, List<Integer> path,
            String query, List<StoredStack> result, boolean[] depthLimited, int depth) {
        if (stack.isEmpty()) return;
        if (query.isEmpty() || matches(stack, query)) {
            result.add(new StoredStack(chest, rootSlot, List.copyOf(path), stack.copy()));
        }

        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container == null) return;

        if (depth >= MAX_CONTAINER_DEPTH) {
            if (container.streamNonEmpty().findAny().isPresent()) depthLimited[0] = true;
            return;
        }
        List<ItemStack> children = container.stream().toList();
        for (int i = 0; i < children.size(); i++) {
            ItemStack child = children.get(i);
            if (child.isEmpty()) continue;
            path.add(i);
            collect(chest, rootSlot, child, path, query, result, depthLimited, depth + 1);
            path.remove(path.size() - 1);
        }
    }

    private static boolean matches(ItemStack stack, String query) {
        return stack.getName().getString().toLowerCase(Locale.ROOT).contains(query)
                || Registries.ITEM.getId(stack.getItem()).toString().toLowerCase(Locale.ROOT).contains(query);
    }

    /**
     * Extracts from the real inventory, never from the cached screen contents.
     * The chest is resolved again by position and the root stack, slot and container path
     * must still match, otherwise nothing is removed.
     */
    public static ItemStack extract(World world, BlockPos connector, StoredStack entry, int amount) {
        if (amount <= 0 || !isOperational(world, connector)) return ItemStack.EMPTY;

        Inventory inventory = inventoryFor(world, connector, entry.chest().position());
        if (inventory == null || entry.slot() < 0 || entry.slot() >= inventory.size()) return ItemStack.EMPTY;

        ItemStack root = inventory.getStack(entry.slot()).copy();
        ItemStack target = findStack(root, entry.path());
        if (target == null || target.isEmpty()
                || !ItemStack.areItemsAndComponentsEqual(target, entry.stack())) return ItemStack.EMPTY;

        int taken = Math.min(amount, target.getCount());
        ItemStack removed = target.copyWithCount(taken);
        ItemStack replacement = target.copy();
        replacement.decrement(taken);
        if (!replaceAtPath(root, entry.path(), 0, replacement)) return ItemStack.EMPTY;

        inventory.setStack(entry.slot(), root.isEmpty() ? ItemStack.EMPTY : root);
        inventory.markDirty();
        return removed;
    }

    /** Counts how much of {@code stack} would fit in the player's main inventory. */
    public static int availableSpace(PlayerInventory inventory, ItemStack stack) {
        if (stack.isEmpty()) return 0;
        int max = Math.min(stack.getMaxCount(), inventory.getMaxCountPerStack());
        boolean mergesIntoExisting = !stack.isDamaged();
        int space = 0;
        for (int i = 0; i < PlayerInventory.MAIN_SIZE; i++) {
            ItemStack existing = inventory.getStack(i);
            if (existing.isEmpty()) {
                space += max;
            } else if (mergesIntoExisting && ItemStack.areItemsAndComponentsEqual(existing, stack)) {
                int existingMax = Math.min(existing.getMaxCount(), inventory.getMaxCountPerStack());
                space += Math.max(0, existingMax - existing.getCount());
            }
            if (space >= stack.getCount()) return stack.getCount();
        }
        return space;
    }

    private static ItemStack findStack(ItemStack root, List<Integer> path) {
        ItemStack current = root;
        for (int index : path) {
            ContainerComponent container = current.get(DataComponentTypes.CONTAINER);
            if (container == null) return null;
            List<ItemStack> children = container.stream().toList();
            if (index < 0 || index >= children.size()) return null;
            current = children.get(index);
        }
        return current;
    }

    private static boolean replaceAtPath(ItemStack current, List<Integer> path, int depth, ItemStack replacement) {
        if (depth == path.size()) {
            current.setCount(replacement.getCount());
            return true;
        }
        ContainerComponent component = current.get(DataComponentTypes.CONTAINER);
        if (component == null) return false;
        List<ItemStack> contents = new ArrayList<>(component.stream().toList());
        int index = path.get(depth);
        if (index < 0 || index >= contents.size()) return false;
        if (depth == path.size() - 1) contents.set(index, replacement);
        else if (!replaceAtPath(contents.get(index), path, depth + 1, replacement)) return false;

        ContainerComponent updated = ContainerComponent.fromStacks(contents);
        if (updated.equals(ContainerComponent.DEFAULT)) current.remove(DataComponentTypes.CONTAINER);
        else current.set(DataComponentTypes.CONTAINER, updated);
        return true;
    }

    private static Inventory inventoryFor(World world, BlockPos connector, BlockPos canonical) {
        for (ChestStorage chest : findChests(world, connector)) {
            if (chest.position().equals(canonical)) return chest.inventory();
        }
        return null;
    }
}
