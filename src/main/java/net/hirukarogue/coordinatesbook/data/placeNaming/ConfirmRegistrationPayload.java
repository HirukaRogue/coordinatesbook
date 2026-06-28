package net.hirukarogue.coordinatesbook.data.placeNaming;

import com.mojang.datafixers.types.Type;
import net.hirukarogue.coordinatesbook.CoordinatesBook;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

//Aqui é onde fica a parte para registrar a confirmação do lugar
public record ConfirmRegistrationPayload(String placeName) implements CustomPacketPayload {
    //tipo
    public static final Type<ConfirmRegistrationPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CoordinatesBook.MODID, "confirm_registration"));
    //codec
    public static final StreamCodec<FriendlyByteBuf, ConfirmRegistrationPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ConfirmRegistrationPayload::placeName,
            ConfirmRegistrationPayload::new
    );
    //registro de tipo
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
