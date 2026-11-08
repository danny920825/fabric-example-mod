package com.bitqba.screen;

import com.bitqba.block.ModBlocks;
import com.bitqba.storage.ShulkerPress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArchivePressScreenHandler extends ScreenHandler {
    public static final int BUTTON_ANALYZE = 0;
    public static final int BUTTON_ORGANIZE = 1;

    private static final int PROPERTY_STATUS = 0;
    private static final int PROPERTY_LINKED = 1;
    private static final int PROPERTY_ELIGIBLE = 2;
    private static final int PROPERTY_IGNORED = 3;
    private static final int PROPERTY_EMPTY_BEFORE = 4;
    private static final int PROPERTY_EMPTY_AFTER = 5;
    private static final int PROPERTY_EMPTIED = 6;
    private static final int PROPERTY_STACKS_BEFORE = 7;
    private static final int PROPERTY_STACKS_AFTER = 8;
    private static final int PROPERTY_GROUPS = 9;
    private static final int PROPERTY_CAN_APPLY = 10;
    private static final int PROPERTY_CAN_ANALYZE = 11;

    private final ArrayPropertyDelegate data = new ArrayPropertyDelegate(12);
    private final BlockPos connectorPos;
    private ShulkerPress.Plan preview;

    public ArchivePressScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos connectorPos) {
        super(ModScreenHandlers.ARCHIVE_PRESS, syncId);
        this.connectorPos = connectorPos.toImmutable();
        addProperties(data);

        if (!playerInventory.player.getEntityWorld().isClient()) {
            publish(ShulkerPress.analyze(playerInventory.player.getEntityWorld(), this.connectorPos), false);
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        World world = player.getEntityWorld();
        return world.getBlockState(connectorPos).isOf(ModBlocks.ARCHIVE_CONNECTOR)
                && player.squaredDistanceTo(connectorPos.getX() + 0.5, connectorPos.getY() + 0.5,
                        connectorPos.getZ() + 0.5) < 64.0;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (player.getEntityWorld().isClient()) return false;
        if (!canUse(player)) {
            closeFor(player);
            return false;
        }

        World world = player.getEntityWorld();
        if (id == BUTTON_ANALYZE) {
            ShulkerPress.Analysis analysis = ShulkerPress.analyze(world, connectorPos);
            preview = analysis.plan();
            publish(analysis, true);
            return true;
        }
        if (id == BUTTON_ORGANIZE) {
            if (preview == null) return false;
            ShulkerPress.Analysis result = ShulkerPress.apply(world, preview);
            preview = null;
            publish(result, false);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        return ItemStack.EMPTY;
    }

    public ShulkerPress.Status getStatus() {
        ShulkerPress.Status[] values = ShulkerPress.Status.values();
        int index = Math.max(0, Math.min(values.length - 1, data.get(PROPERTY_STATUS)));
        return values[index];
    }

    public boolean isLinked() { return data.get(PROPERTY_LINKED) != 0; }
    public int getEligible() { return data.get(PROPERTY_ELIGIBLE); }
    public int getIgnored() { return data.get(PROPERTY_IGNORED); }
    public int getEmptyBefore() { return data.get(PROPERTY_EMPTY_BEFORE); }
    public int getEmptyAfter() { return data.get(PROPERTY_EMPTY_AFTER); }
    public int getEmptied() { return data.get(PROPERTY_EMPTIED); }
    public int getStacksBefore() { return data.get(PROPERTY_STACKS_BEFORE); }
    public int getStacksAfter() { return data.get(PROPERTY_STACKS_AFTER); }
    public int getGroups() { return data.get(PROPERTY_GROUPS); }
    public boolean canApply() { return data.get(PROPERTY_CAN_APPLY) != 0; }
    public boolean canAnalyze() { return data.get(PROPERTY_CAN_ANALYZE) != 0; }

    private void publish(ShulkerPress.Analysis analysis, boolean allowApply) {
        ShulkerPress.Summary summary = analysis.summary();
        data.set(PROPERTY_STATUS, analysis.status().ordinal());
        data.set(PROPERTY_LINKED, analysis.linked() ? 1 : 0);
        data.set(PROPERTY_ELIGIBLE, summary.eligible());
        data.set(PROPERTY_IGNORED, summary.ignored());
        data.set(PROPERTY_EMPTY_BEFORE, summary.emptyBefore());
        data.set(PROPERTY_EMPTY_AFTER, summary.emptyAfter());
        data.set(PROPERTY_EMPTIED, summary.emptied());
        data.set(PROPERTY_STACKS_BEFORE, summary.stacksBefore());
        data.set(PROPERTY_STACKS_AFTER, summary.stacksAfter());
        data.set(PROPERTY_GROUPS, summary.groups());
        data.set(PROPERTY_CAN_APPLY, allowApply
                && analysis.status() == ShulkerPress.Status.READY
                && analysis.plan() != null ? 1 : 0);
        data.set(PROPERTY_CAN_ANALYZE, analysis.linked() ? 1 : 0);
        sendContentUpdates();
    }

    private static void closeFor(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) serverPlayer.closeHandledScreen();
    }
}
