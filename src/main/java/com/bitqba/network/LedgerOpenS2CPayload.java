package com.bitqba.network;

import com.bitqba.ArcanumStorageMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Opens the ledger screen with the resolved server-side view. */
public record LedgerOpenS2CPayload(String json) implements CustomPayload {
    private static final int MAX_JSON_LENGTH = 1_000_000;

    public static final Id<LedgerOpenS2CPayload> ID =
            new Id<>(Identifier.of(ArcanumStorageMod.MOD_ID, "ledger_open"));
    public static final PacketCodec<RegistryByteBuf, LedgerOpenS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.string(MAX_JSON_LENGTH), LedgerOpenS2CPayload::json, LedgerOpenS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
