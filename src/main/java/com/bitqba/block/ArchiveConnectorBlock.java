package com.bitqba.block;

import com.bitqba.screen.ArchivePressScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/**
 * Terminal for the Shulker Press.
 *
 * <p>It no longer attaches to a chest: it joins the cable network, and the press
 * works on the network's chest only when exactly one chest (single or double) is
 * connected. {@link #LINKED} means the block touches an Archive Cable.</p>
 */
public class ArchiveConnectorBlock extends Block {
    /** Cosmetic orientation of the front face. */
    public static final EnumProperty<Direction> FACING = Properties.FACING;
    public static final BooleanProperty LINKED = BooleanProperty.of("linked");

    public ArchiveConnectorBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LINKED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LINKED);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState,
            boolean notify) {
        if (world.isClient()) return;
        boolean linked = touchesCable(world, pos);
        if (state.get(LINKED) != linked) {
            world.setBlockState(pos, state.with(LINKED, linked));
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction facing = context.getSide().getOpposite();
        if (facing.getAxis().isVertical()) {
            facing = context.getHorizontalPlayerFacing().getOpposite();
        }
        return getDefaultState()
                .with(FACING, facing)
                .with(LINKED, touchesCable(context.getWorld(), context.getBlockPos()));
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world,
            ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos,
            BlockState neighborState, net.minecraft.util.math.random.Random random) {
        return state.with(LINKED, touchesCable(world, pos));
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
            BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
                @Override
                public ArchivePressScreenHandler createMenu(int syncId, PlayerInventory inventory,
                        PlayerEntity playerEntity) {
                    return new ArchivePressScreenHandler(syncId, inventory, pos);
                }

                @Override
                public Text getDisplayName() {
                    return Text.translatable("container.arcanum_storage.shulker_press");
                }

                @Override
                public BlockPos getScreenOpeningData(ServerPlayerEntity ignored) {
                    return pos;
                }
            });
        }
        return ActionResult.SUCCESS;
    }

    private static boolean touchesCable(WorldView world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (world.getBlockState(pos.offset(direction)).isOf(ModBlocks.ARCHIVE_CABLE)) {
                return true;
            }
        }
        return false;
    }
}
