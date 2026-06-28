package net.hirukarogue.coordinatesbook.events;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.hirukarogue.coordinatesbook.CoordinatesBook;
import net.hirukarogue.coordinatesbook.data.CoordinateList;
import net.hirukarogue.coordinatesbook.data.placeNaming.ConfirmRegistrationPayload;
import net.hirukarogue.coordinatesbook.data.placeNaming.OpenNamingScreenPayload;
import net.hirukarogue.coordinatesbook.network.TPBookPayloadHandler;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class TPBookEvents {
    public static void registerNetworkStuff(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0");

        registrar.playToClient(
                CoordinateList.TYPE,
                CoordinateList.CODEC,
                TPBookPayloadHandler::handleTPDataNetwork
        );

        registrar.playToClient(
                OpenNamingScreenPayload.TYPE,
                OpenNamingScreenPayload.CODEC,
                TPBookPayloadHandler::handleOpenNamingScreen
        );

        registrar.playToServer(
                ConfirmRegistrationPayload.TYPE,
                ConfirmRegistrationPayload.CODEC,
                TPBookPayloadHandler::handleConfirmRegistration
        );
    }
}
