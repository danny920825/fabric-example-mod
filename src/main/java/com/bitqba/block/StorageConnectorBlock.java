package com.bitqba.block;

import com.bitqba.storage.StorageNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
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

    /**
     * The connector is the power source of the link, not the chest. Each wired side
     * gets a level 15 signal so the repeater lights up; the repeater's arrow points
     * at the chest, which does not respond to redstone. Sides without a wired
     * repeater stay silent, so the connector does not feed unrelated circuits.
     */
    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return state.get(SCREEN_ATTACHED);
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!state.get(SCREEN_ATTACHED) || direction.getAxis().isVertical()) return 0;
        // Repeaters query their source with the direction pointing from the receiver
        // to the connector, so the linked side is the opposite one.
        return StorageNetwork.isWiredSide(world, pos, direction.getOpposite()) ? 15 : 0;
    }
}
