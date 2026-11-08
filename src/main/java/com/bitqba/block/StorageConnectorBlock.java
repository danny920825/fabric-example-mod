package com.bitqba.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public class StorageConnectorBlock extends Block {
    public static final BooleanProperty SCREEN_ATTACHED = BooleanProperty.of("screen_attached");

    public StorageConnectorBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(SCREEN_ATTACHED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SCREEN_ATTACHED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState().with(SCREEN_ATTACHED,
                context.getWorld().getBlockState(context.getBlockPos().up()).isOf(ModBlocks.STORAGE_DISPLAY));
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world,
            net.minecraft.world.tick.ScheduledTickView tickView, BlockPos pos, Direction direction,
            BlockPos neighborPos, BlockState neighborState, net.minecraft.util.math.random.Random random) {
        if (direction == Direction.UP) {
            return state.with(SCREEN_ATTACHED, neighborState.isOf(ModBlocks.STORAGE_DISPLAY));
        }
        return state;
    }
}
