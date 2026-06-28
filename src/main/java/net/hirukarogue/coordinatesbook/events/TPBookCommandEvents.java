package net.hirukarogue.coordinatesbook.events;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.hirukarogue.coordinatesbook.network.TPBookPayloadHandler;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class TPBookCommandEvents {
    public void onRegisterCommands(RegisterCommandsEvent event) {
        //aqui é o comando para deletar um lugar salvo no livro
        //vocẽ também pode o executar manualmente
        //mas não é tão nescessário
        event.getDispatcher().register(
                //inicial do comando
                Commands.literal("coordinates")
                        .requires(source -> source.hasPermission(0))
                        //comando
                        .then(Commands.literal("delete")
                                //indice do local
                                .then(Commands.argument("index", IntegerArgumentType.integer(0))
                                        .executes(context -> {
                                            //Aqui é onde executa a lógica do comando

                                            //recebe o indice indicado
                                            int index = IntegerArgumentType.getInteger(context, "index");

                                            //manda o pacote para executar a eliminação do local no livro
                                            //com base no indice
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
