package com.bitqba.network;

import com.bitqba.ArcanumStorageMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

/** Notifies the client that one or more entries gained a state. */
public record LedgerNoteS2CPayload(List<String> entryIds) implements CustomPayload {
    private static final int MAX_ENTRIES = 64;

    public static final Id<LedgerNoteS2CPayload> ID =
            new Id<>(Identifier.of(ArcanumStorageMod.MOD_ID, "ledger_note"));
    public static final PacketCodec<RegistryByteBuf, LedgerNoteS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.string(64).collect(PacketCodecs.toList(MAX_ENTRIES)),
            LedgerNoteS2CPayload::entryIds, LedgerNoteS2CPayload::new);

    public LedgerNoteS2CPayload {
        entryIds = List.copyOf(entryIds);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
