package com.bitqba.screen;

import com.bitqba.ArcanumStorageMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class ModScreenHandlers {
    public static final ExtendedScreenHandlerType<StorageScreenHandler, BlockPos> STORAGE_DISPLAY =
            Registry.register(Registries.SCREEN_HANDLER,
                    Identifier.of(ArcanumStorageMod.MOD_ID, "storage_display"),
                    new ExtendedScreenHandlerType<>(StorageScreenHandler::new, BlockPos.PACKET_CODEC));

    public static final ExtendedScreenHandlerType<ArchivePressScreenHandler, BlockPos> ARCHIVE_PRESS =
            Registry.register(Registries.SCREEN_HANDLER,
                    Identifier.of(ArcanumStorageMod.MOD_ID, "archive_press"),
                    new ExtendedScreenHandlerType<>(ArchivePressScreenHandler::new, BlockPos.PACKET_CODEC));

    private ModScreenHandlers() {}
    public static void register() { ArcanumStorageMod.LOGGER.info("Storage screen handler registered"); }
}
