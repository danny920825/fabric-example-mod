package com.bitqba.mixin;

import com.bitqba.storage.ArchiveCache;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Removes a placed Archive Cache once it has been emptied.
 *
 * <p>The cache keeps its name and lore so the player can recognize it, but it is
 * not meant to become a permanent shulker box. Waiting for the closing animation
 * avoids destroying the block while a player still has its screen open.</p>
 */
@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private static void arcanum$discardEmptyCache(World world, BlockPos pos, BlockState state,
            ShulkerBoxBlockEntity blockEntity, CallbackInfo ci) {
        if (world.isClient()) return;
        if (blockEntity.getAnimationStage() != ShulkerBoxBlockEntity.AnimationStage.CLOSED) return;
        if (!ArchiveCache.isEmptyArchiveCache(blockEntity)) return;

        world.removeBlock(pos, false);
    }
}
