package com.bitqba.client;

import com.bitqba.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ArcanumStorageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.STORAGE_DISPLAY, StorageDisplayScreen::new);
        HandledScreens.register(ModScreenHandlers.ARCHIVE_PRESS, ArchivePressScreen::new);
    }
}
