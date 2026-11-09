package com.bitqba.network;

import com.bitqba.screen.StorageScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ModNetworking {
    private ModNetworking() {}

    public static void register() {
        PayloadTypeRegistry.playC2S().register(StorageSearchPayload.ID, StorageSearchPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LedgerOpenS2CPayload.ID, LedgerOpenS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LedgerNoteS2CPayload.ID, LedgerNoteS2CPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(StorageSearchPayload.ID, (payload, context) ->
                context.server().execute(() -> {
                    if (context.player().currentScreenHandler instanceof StorageScreenHandler handler
                            && handler.canUse(context.player())) {
                        handler.setSearchQuery(payload.query());
                    }
                }));
    }
}
