package com.bitqba.network;

import com.bitqba.StorageDisplayMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StorageSearchPayload(String query) implements CustomPayload {
    private static final int MAX_QUERY_LENGTH = 64;
    public static final Id<StorageSearchPayload> ID = new Id<>(Identifier.of(StorageDisplayMod.MOD_ID, "storage_search"));
    public static final PacketCodec<RegistryByteBuf, StorageSearchPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.string(MAX_QUERY_LENGTH), StorageSearchPayload::query, StorageSearchPayload::new);
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
