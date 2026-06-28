package net.hirukarogue.coordinatesbook.data;

import net.hirukarogue.coordinatesbook.CoordinatesBook;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

//registro da lista de lugares
public record CoordinateList(List<TPData> registeredPlaces) implements CustomPacketPayload {
    //tipo
    public static final CustomPacketPayload.Type<CoordinateList> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CoordinatesBook.MODID, "coordinates_list"));

    //codec
    public static final StreamCodec<RegistryFriendlyByteBuf, CoordinateList> CODEC = StreamCodec.of(
            (buffer, payload) -> buffer.writeCollection(payload.registeredPlaces, TPData.STREAM_CODEC),
            buffer -> new CoordinateList(buffer.readCollection(ArrayList::new, TPData.STREAM_CODEC))
    );

    //registro do tipo
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
