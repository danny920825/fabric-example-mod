package com.bitqba.screen;

import com.bitqba.StorageDisplayMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class ModScreenHandlers {
    public static final ExtendedScreenHandlerType<StorageScreenHandler, BlockPos> STORAGE_DISPLAY =
            Registry.register(Registries.SCREEN_HANDLER,
                    Identifier.of(StorageDisplayMod.MOD_ID, "storage_display"),
                    new ExtendedScreenHandlerType<>(StorageScreenHandler::new, BlockPos.PACKET_CODEC));

    private ModScreenHandlers() {}
    public static void register() { StorageDisplayMod.LOGGER.info("Storage screen handler registered"); }
}
