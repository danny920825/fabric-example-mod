package com.bitqba.block;

import com.bitqba.screen.StorageScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StorageDisplayBlock extends Block {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public StorageDisplayBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient()) setConnectorScreenState(world, pos.down(), true);
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        setConnectorScreenState(world, pos.down(), false);
        super.onStateReplaced(state, world, pos, moved);
    }

    private static void setConnectorScreenState(World world, BlockPos connectorPos, boolean attached) {
        BlockState connector = world.getBlockState(connectorPos);
        if (!connector.isOf(ModBlocks.STORAGE_CONNECTOR)) return;
        if (connector.get(StorageConnectorBlock.SCREEN_ATTACHED) != attached) {
            world.setBlockState(connectorPos, connector.with(StorageConnectorBlock.SCREEN_ATTACHED, attached));
        }
    }

    @Override
    protected boolean canPlaceAt(BlockState state, net.minecraft.world.WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isOf(ModBlocks.STORAGE_CONNECTOR);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return canPlaceAt(getDefaultState(), ctx.getWorld(), ctx.getBlockPos())
                ? getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                : null;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, net.minecraft.world.WorldView world,
            net.minecraft.world.tick.ScheduledTickView tickView, BlockPos pos,
            net.minecraft.util.math.Direction direction, BlockPos neighborPos, BlockState neighborState,
            net.minecraft.util.math.random.Random random) {
        if (direction == net.minecraft.util.math.Direction.DOWN && !neighborState.isOf(ModBlocks.STORAGE_CONNECTOR)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        BlockPos connectorPos = pos.down();
        if (!world.getBlockState(connectorPos).isOf(ModBlocks.STORAGE_CONNECTOR)) return ActionResult.PASS;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
                @Override
                public StorageScreenHandler createMenu(int syncId, net.minecraft.entity.player.PlayerInventory inventory, PlayerEntity playerEntity) {
                    return new StorageScreenHandler(syncId, inventory, connectorPos);
                }

                @Override public Text getDisplayName() { return Text.translatable("container.storage_display.storage_display"); }
                @Override public BlockPos getScreenOpeningData(ServerPlayerEntity ignored) { return connectorPos; }
            });
        }
        return ActionResult.SUCCESS;
    }
}
