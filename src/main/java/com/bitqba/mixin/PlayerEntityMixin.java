package com.bitqba.mixin;

import com.bitqba.storage.ArchiveCache;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks the player death into the Archive Cache mechanic.
 *
 * <p>Runs before vanilla drops the inventory. {@link ArchiveCache} removes the
 * bound pouch and the equipment that is sealed into the cache, so the normal
 * drop only handles the 27 backpack slots and every item that must still fall
 * (unbound pouches, vanishing items, and everything when there is no bound
 * pouch).</p>
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void arcanum$sealArchiveCache(ServerWorld world, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity serverPlayer) {
            ArchiveCache.handleDeath(serverPlayer);
        }
    }
}
