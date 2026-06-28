package net.hirukarogue.coordinatesbook.data.placeNaming;

import net.hirukarogue.coordinatesbook.CoordinatesBook;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

//aqui é um registro básico para abrir a tela de pop up
public record OpenNamingScreenPayload() implements CustomPacketPayload {
    //tipo
    public static final Type<OpenNamingScreenPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CoordinatesBook.MODID, "open_naming_screen"));
    //codec
    public static final StreamCodec<FriendlyByteBuf, OpenNamingScreenPayload> CODEC = StreamCodec.unit(new OpenNamingScreenPayload());
    //registro de tipo
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
