package net.hirukarogue.coordinatesbook.data;

import net.hirukarogue.coordinatesbook.CoordinatesBook;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TPData(String name, String command) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TPData> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CoordinatesBook.MODID, "tp_book_packet"));

    public static final StreamCodec<FriendlyByteBuf, TPData> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> {
                buffer.writeUtf(payload.name);
                buffer.writeUtf(payload.command);
            },
            buffer -> new TPData(buffer.readUtf(), buffer.readUtf())
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
