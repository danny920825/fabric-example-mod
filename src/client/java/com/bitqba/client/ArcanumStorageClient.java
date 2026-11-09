package com.bitqba.client;

import com.bitqba.ledger.LedgerView;
import com.bitqba.network.LedgerNoteS2CPayload;
import com.bitqba.network.LedgerOpenS2CPayload;
import com.bitqba.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ArcanumStorageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.STORAGE_DISPLAY, StorageDisplayScreen::new);
        HandledScreens.register(ModScreenHandlers.ARCHIVE_PRESS, ArchivePressScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(LedgerOpenS2CPayload.ID, (payload, context) ->
                context.client().execute(() ->
                        context.client().setScreen(new LedgerScreen(LedgerView.fromJson(payload.json())))));

        ClientPlayNetworking.registerGlobalReceiver(LedgerNoteS2CPayload.ID, (payload, context) -> {
            // Entry notes are advisory; the open payload already carries the full state.
        });
    }
}
