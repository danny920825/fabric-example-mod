package com.bitqba.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

import java.util.Map;

/**
 * A cable that carries the storage network from the Storage Connector to chests.
 *
 * <p>The cable itself is only a conduit: it does not store items and it has no
 * redstone behavior. A chest joins the network when it touches any cable that is
 * connected to the connector, and the root connector exposes five usable faces
 * (the top face always holds the Storage Display).</p>
 */
public class ArchiveCableBlock extends Block {
    private static final Map<Direction, BooleanProperty> CONNECTIONS = Map.of(
            Direction.NORTH, Properties.NORTH,
            Direction.SOUTH, Properties.SOUTH,
            Direction.EAST, Properties.EAST,
            Direction.WEST, Properties.WEST,
            Direction.UP, Properties.UP,
            Direction.DOWN, Properties.DOWN);

    private static final VoxelShape CORE = Block.createCuboidShape(6, 6, 6, 10, 10, 10);
    private static final Map<Direction, VoxelShape> ARMS = Map.of(
            Direction.NORTH, Block.createCuboidShape(6, 6, 0, 10, 10, 6),
            Direction.SOUTH, Block.createCuboidShape(6, 6, 10, 10, 10, 16),
            Direction.WEST, Block.createCuboidShape(0, 6, 6, 6, 10, 10),
            Direction.EAST, Block.createCuboidShape(10, 6, 6, 16, 10, 10),
            Direction.UP, Block.createCuboidShape(6, 10, 6, 10, 16, 10),
            Direction.DOWN, Block.createCuboidShape(6, 0, 6, 10, 6, 10));

    public ArchiveCableBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(Properties.NORTH, false)
                .with(Properties.SOUTH, false)
                .with(Properties.EAST, false)
                .with(Properties.WEST, false)
                .with(Properties.UP, false)
                .with(Properties.DOWN, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.NORTH, Properties.SOUTH, Properties.EAST,
                Properties.WEST, Properties.UP, Properties.DOWN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return withConnections(getDefaultState(), context.getWorld(), context.getBlockPos());
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world,
            ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos,
            BlockState neighborState, net.minecraft.util.math.random.Random random) {
        return state.with(CONNECTIONS.get(direction), canConnect(neighborState));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return shape(state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return shape(state);
    }

    private static BlockState withConnections(BlockState state, BlockView world, BlockPos pos) {
        BlockState result = state;
        for (Direction direction : Direction.values()) {
            result = result.with(CONNECTIONS.get(direction),
                    canConnect(world.getBlockState(pos.offset(direction))));
        }
        return result;
    }

    private static boolean canConnect(BlockState neighbor) {
        return neighbor.isOf(ModBlocks.ARCHIVE_CABLE)
                || neighbor.isOf(ModBlocks.STORAGE_CONNECTOR)
                || neighbor.isOf(ModBlocks.ARCHIVE_CONNECTOR)
                || neighbor.isOf(Blocks.CHEST);
    }

    private static VoxelShape shape(BlockState state) {
        VoxelShape shape = CORE;
        for (Direction direction : Direction.values()) {
            if (state.get(CONNECTIONS.get(direction))) {
                shape = VoxelShapes.union(shape, ARMS.get(direction));
            }
        }
        return shape;
    }
}
