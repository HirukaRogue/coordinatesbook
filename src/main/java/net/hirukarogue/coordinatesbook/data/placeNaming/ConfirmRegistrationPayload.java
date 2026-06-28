package net.hirukarogue.coordinatesbook.data.placeNaming;

import com.mojang.datafixers.types.Type;
import net.hirukarogue.coordinatesbook.CoordinatesBook;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ConfirmRegistrationPayload(String placeName) implements CustomPacketPayload {
    public static final Type<ConfirmRegistrationPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CoordinatesBook.MODID, "confirm_registration"));
    public static final StreamCodec<FriendlyByteBuf, ConfirmRegistrationPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ConfirmRegistrationPayload::placeName,
            ConfirmRegistrationPayload::new
    );
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
