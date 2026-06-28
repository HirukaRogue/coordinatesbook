package net.hirukarogue.coordinatesbook.data;

import net.hirukarogue.coordinatesbook.CoordinatesBook;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

//record para o registro de lugar
//ele usa payload para registrar
public record TPData(String name, String command) implements CustomPacketPayload {
    //registro do tipo de payload
    public static final CustomPacketPayload.Type<TPData> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CoordinatesBook.MODID, "tp_book_packet"));

    //codec para o lugar registrado com nome e o mando para tp
    public static final StreamCodec<FriendlyByteBuf, TPData> STREAM_CODEC = StreamCodec.of(
            (buffer, payload) -> {
                buffer.writeUtf(payload.name);
                buffer.writeUtf(payload.command);
            },
            buffer -> new TPData(buffer.readUtf(), buffer.readUtf())
    );

    //registro básico do tipo
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
