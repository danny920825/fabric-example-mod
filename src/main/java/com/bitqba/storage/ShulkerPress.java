package com.bitqba.storage;

import com.bitqba.ArcanumStorageMod;
import com.bitqba.block.ModBlocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plans and applies the Shulker Press for one Archive Connector.
 *
 * <p>The connector joins the cable network and the press only works when the
 * network holds exactly one chest (a double chest counts as one). It only reads
 * and writes unlabeled shulker-box items stored in that chest, never opens nested
 * shulkers, never touches labeled shulkers or loose items, and validates a full
 * chest snapshot before writing anything.</p>
 */
public final class ShulkerPress {
    public enum Status {
        NO_NETWORK,
        NO_CHEST,
        MULTIPLE_CHESTS,
        NO_SHULKERS,
        ALL_EMPTY,
        ALREADY_ORGANIZED,
        READY,
        APPLIED,
        CHANGED,
        FAILED
    }

    public record Summary(
            int eligible,
            int ignored,
            int emptyBefore,
            int emptyAfter,
            int emptied,
            int stacksBefore,
            int stacksAfter,
            int groups) {

        public static final Summary EMPTY = new Summary(0, 0, 0, 0, 0, 0, 0, 0);
    }

    public record SlotChange(int slot, ItemStack stack) {
        public SlotChange {
            stack = stack.copy();
        }
    }

    public record Plan(
            BlockPos connector,
            BlockPos chest,
            List<ItemStack> baseline,
            List<SlotChange> changes,
            Summary summary) {

        public Plan {
            connector = connector.toImmutable();
            chest = chest.toImmutable();
            baseline = List.copyOf(baseline);
            changes = List.copyOf(changes);
        }
    }

    public record Analysis(Status status, boolean linked, Summary summary, Plan plan) {}

    private record NetworkChest(Status status, BlockPos position, Inventory inventory) {}

    private static final class Group {
        private final ItemStack representative;
        private final int maxCount;
        private int total;

        private Group(ItemStack stack) {
            this.representative = stack.copyWithCount(1);
            this.maxCount = Math.max(1, stack.getMaxCount());
            this.total = stack.getCount();
        }
    }

    private ShulkerPress() {}

    /**
     * Builds a preview. The returned {@link Plan} is only present when the chest can be
     * organized, and it contains the exact baseline snapshot used by {@link #apply}.
     */
    public static Analysis analyze(World world, BlockPos connector) {
        NetworkChest chest = findChest(world, connector);
        if (chest.status() != Status.READY) {
            return new Analysis(chest.status(), false, Summary.EMPTY, null);
        }

        Inventory inventory = chest.inventory();
        List<ItemStack> baseline = new ArrayList<>(inventory.size());
        for (int slot = 0; slot < inventory.size(); slot++) {
            baseline.add(inventory.getStack(slot).copy());
        }

        int ignored = 0;
        List<Integer> eligibleSlots = new ArrayList<>();
        for (int slot = 0; slot < baseline.size(); slot++) {
            ItemStack stack = baseline.get(slot);
            if (stack.isEmpty() || !isShulkerBox(stack)) continue;
            if (hasLabel(stack)) {
                ignored++;
                continue;
            }
            eligibleSlots.add(slot);
        }

        if (eligibleSlots.isEmpty()) {
            return new Analysis(Status.NO_SHULKERS, true,
                    new Summary(0, ignored, 0, 0, 0, 0, 0, 0), null);
        }

        List<Group> groups = new ArrayList<>();
        Map<Integer, List<Group>> groupsByHash = new HashMap<>();
        int stacksBefore = 0;
        int emptyBefore = 0;

        for (int slot : eligibleSlots) {
            ItemStack shulker = baseline.get(slot);
            ContainerComponent container = shulker.get(DataComponentTypes.CONTAINER);
            if (container == null) {
                emptyBefore++;
                continue;
            }
            List<ItemStack> contents = container.streamNonEmpty().map(ItemStack::copy).toList();
            if (contents.isEmpty()) {
                emptyBefore++;
                continue;
            }
            for (ItemStack item : contents) {
                stacksBefore++;
                addToGroup(groups, groupsByHash, item);
            }
        }

        if (groups.isEmpty()) {
            Summary summary = new Summary(eligibleSlots.size(), ignored, emptyBefore,
                    eligibleSlots.size(), 0, 0, 0, 0);
            return new Analysis(Status.ALL_EMPTY, true, summary, null);
        }

        groups.sort(java.util.Comparator
                .comparingInt((Group group) -> group.total)
                .reversed()
                .thenComparing(group -> Registries.ITEM.getId(group.representative.getItem()).toString()));

        List<ItemStack> output = new ArrayList<>(stacksBefore);
        for (Group group : groups) {
            int start = output.size();
            int remaining = group.total;
            while (remaining > 0) {
                int count = Math.min(group.maxCount, remaining);
                output.add(group.representative.copyWithCount(count));
                remaining -= count;
            }

            int placed = 0;
            for (int i = start; i < output.size(); i++) {
                placed += output.get(i).getCount();
            }
            if (placed != group.total) {
                return failed(eligibleSlots.size(), ignored, emptyBefore, stacksBefore,
                        output.size(), groups.size());
            }
        }

        int cursor = 0;
        int emptyAfter = 0;
        int emptied = 0;
        List<SlotChange> changes = new ArrayList<>(eligibleSlots.size());

        for (int slot : eligibleSlots) {
            List<ItemStack> contents = new ArrayList<>(ShulkerBoxBlockEntity.INVENTORY_SIZE);
            for (int i = 0; i < ShulkerBoxBlockEntity.INVENTORY_SIZE && cursor < output.size(); i++) {
                contents.add(output.get(cursor++));
            }

            ItemStack original = baseline.get(slot);
            ItemStack replacement = original.copy();
            boolean originalEmpty = isEmptyContainer(original);

            if (contents.isEmpty()) {
                replacement.remove(DataComponentTypes.CONTAINER);
                emptyAfter++;
                if (!originalEmpty) emptied++;
            } else {
                replacement.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(contents));
            }
            changes.add(new SlotChange(slot, replacement));
        }

        if (cursor < output.size()) {
            return failed(eligibleSlots.size(), ignored, emptyBefore, stacksBefore,
                    output.size(), groups.size());
        }

        boolean unchanged = true;
        for (SlotChange change : changes) {
            if (!ItemStack.areEqual(change.stack(), baseline.get(change.slot()))) {
                unchanged = false;
                break;
            }
        }

        Summary summary = new Summary(eligibleSlots.size(), ignored, emptyBefore, emptyAfter,
                emptied, stacksBefore, output.size(), groups.size());
        if (unchanged) {
            return new Analysis(Status.ALREADY_ORGANIZED, true, summary, null);
        }

        Plan plan = new Plan(connector, chest.position(), baseline, changes, summary);
        return new Analysis(Status.READY, true, summary, plan);
    }

    /**
     * Revalidates the connector and the complete chest snapshot before writing. Any
     * foreign change aborts the operation without touching the inventory.
     */
    public static Analysis apply(World world, Plan plan) {
        NetworkChest chest = findChest(world, plan.connector());
        if (chest.status() != Status.READY) {
            return new Analysis(chest.status(), false, plan.summary(), null);
        }
        if (!chest.position().equals(plan.chest())) {
            return new Analysis(Status.CHANGED, false, plan.summary(), null);
        }

        Inventory inventory = chest.inventory();
        if (inventory.size() != plan.baseline().size()) {
            return new Analysis(Status.CHANGED, true, plan.summary(), null);
        }
        for (int slot = 0; slot < inventory.size(); slot++) {
            if (!ItemStack.areEqual(inventory.getStack(slot), plan.baseline().get(slot))) {
                return new Analysis(Status.CHANGED, true, plan.summary(), null);
            }
        }

        List<ItemStack> backup = new ArrayList<>(inventory.size());
        for (int slot = 0; slot < inventory.size(); slot++) {
            backup.add(inventory.getStack(slot).copy());
        }

        try {
            for (SlotChange change : plan.changes()) {
                inventory.setStack(change.slot(), change.stack().copy());
            }
            inventory.markDirty();

            for (SlotChange change : plan.changes()) {
                if (!ItemStack.areEqual(inventory.getStack(change.slot()), change.stack())) {
                    restore(inventory, backup);
                    return new Analysis(Status.FAILED, true, plan.summary(), null);
                }
            }
        } catch (RuntimeException exception) {
            ArcanumStorageMod.LOGGER.error("Shulker press failed while writing the chest", exception);
            restore(inventory, backup);
            return new Analysis(Status.FAILED, true, plan.summary(), null);
        }

        return new Analysis(Status.APPLIED, true, plan.summary(), null);
    }

    private static void restore(Inventory inventory, List<ItemStack> backup) {
        for (int slot = 0; slot < backup.size(); slot++) {
            inventory.setStack(slot, backup.get(slot).copy());
        }
        inventory.markDirty();
    }

    private static NetworkChest findChest(World world, BlockPos connector) {
        if (!world.getBlockState(connector).isOf(ModBlocks.ARCHIVE_CONNECTOR)) {
            return new NetworkChest(Status.NO_NETWORK, null, null);
        }

        BlockPos root = StorageNetwork.findRoot(world, connector);
        if (root == null) {
            return new NetworkChest(Status.NO_NETWORK, null, null);
        }

        List<StorageNetwork.ChestStorage> chests = StorageNetwork.findChests(world, root);
        if (chests.isEmpty()) {
            return new NetworkChest(Status.NO_CHEST, null, null);
        }
        if (chests.size() > 1) {
            return new NetworkChest(Status.MULTIPLE_CHESTS, null, null);
        }

        StorageNetwork.ChestStorage chest = chests.get(0);
        return new NetworkChest(Status.READY, chest.position(), chest.inventory());
    }

    private static void addToGroup(List<Group> groups, Map<Integer, List<Group>> groupsByHash,
            ItemStack stack) {
        int hash = ItemStack.hashCode(stack);
        List<Group> bucket = groupsByHash.computeIfAbsent(hash, ignored -> new ArrayList<>());
        for (Group group : bucket) {
            if (ItemStack.areItemsAndComponentsEqual(group.representative, stack)) {
                group.total += stack.getCount();
                return;
            }
        }
        Group group = new Group(stack);
        bucket.add(group);
        groups.add(group);
    }

    private static boolean isShulkerBox(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }

    private static boolean hasLabel(ItemStack stack) {
        if (stack.get(DataComponentTypes.CUSTOM_NAME) != null) return true;
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        return lore != null && !lore.lines().isEmpty();
    }

    private static boolean isEmptyContainer(ItemStack stack) {
        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        return container == null || container.streamNonEmpty().findAny().isEmpty();
    }

    private static Analysis failed(int eligible, int ignored, int emptyBefore, int stacksBefore,
            int stacksAfter, int groups) {
        Summary summary = new Summary(eligible, ignored, emptyBefore, emptyBefore, 0,
                stacksBefore, stacksAfter, groups);
        return new Analysis(Status.FAILED, true, summary, null);
    }
}
