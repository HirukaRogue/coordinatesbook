package net.hirukarogue.coordinatesbook.events;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.hirukarogue.coordinatesbook.network.TPBookPayloadHandler;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class TPBookCommandEvents {
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("coordinates")
                        .requires(source -> source.hasPermission(0))
                        .then(Commands.literal("delete")
                                .then(Commands.argument("index", IntegerArgumentType.integer(0))
                                        .executes(context -> {
                                            int index = IntegerArgumentType.getInteger(context, "index");
                                            if (context.getSource().getPlayer() instanceof ServerPlayer player) {
                                                TPBookPayloadHandler.executeDeleteLogic(player, index);
                                            }
                                            return 1;
                                        })
                                )
                        )
        );
    }
}
