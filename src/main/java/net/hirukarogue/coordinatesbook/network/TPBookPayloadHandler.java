package net.hirukarogue.coordinatesbook.network;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.hirukarogue.coordinatesbook.data.CoordinateList;
import net.hirukarogue.coordinatesbook.data.TPDataComponents;
import net.hirukarogue.coordinatesbook.data.TPData;
import net.hirukarogue.coordinatesbook.data.placeNaming.ConfirmRegistrationPayload;
import net.hirukarogue.coordinatesbook.data.placeNaming.OpenNamingScreenPayload;
import net.hirukarogue.coordinatesbook.items.coordinatesbook.CoordinatesBookItem;
import net.hirukarogue.coordinatesbook.screens.PlaceNamingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

//aqui fica toda a parada de rede, fundamental para as funções do livro
public class TPBookPayloadHandler {
    //aqui é para abrir o livro
    public static void handleTPDataNetwork(final CoordinateList data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            //aqui fica as páginas o texto que cada pagina contém
            //cada linha de texto é um local salvo
            //tanto para teleportar quanto para apagar
            List<Component> pages = new ArrayList<>();
            MutableComponent pageText = Component.literal("");

            //num conta a quantidade de linhas que passaram
            //e index indica qual a posição do local na lista de lugares
            int num = 0;
            int index = 0;

            //aqui ele pega todos os lugares registrados e registra no livro
            //até chegar no ultimo lugar
            for (TPData place : data.registeredPlaces()) {
                //esse é o hiperlink do lugar com seu nome marcado onde se você clica
                //você é teleportado para esse lugar
                Component hyperlink = Component.literal("- [" + place.name() + "]")
                        .withStyle(style -> style
                                .withColor(0x00AAFF)
                                .withUnderlined(false)
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.RUN_COMMAND,
                                        place.command()
                                ))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Clique para teleportar!")))
                        );

                //aqui current index vira final de index para ser aceito no comando na hora de clicar
                final int currentIndex = index;
                //aqui é onde fica o Xzinho, se você clica nele o local salvo é removido da lista
                //ele é feito pelo comando /coordinates delete <index>
                Component delete = Component.literal("[❌]\n")
                                .withStyle(style -> style
                                        .withColor(0xFF0000)
                                        .withUnderlined(false)
                                        .withClickEvent(new ClickEvent(
                                                ClickEvent.Action.RUN_COMMAND,
                                                "/coordinates delete " + currentIndex
                                        ))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("§cExcluir este local")))
                                );

                //aqui adciona tanto o hiperlink quanto o delete em uma das linhas
                //do texto da página
                pageText.append(hyperlink).append(" ").append(delete);

                //aqui incrementa o número de linhas
                //e também incrementa o indice da posição do local na lista
                num++;
                index++;

                //se o local tiver mais de 15 letras incrementa mais 1 de
                //número de linhas
                if (place.name().length() > 15) {
                    num++;
                }
                //se a quatidade de linhas chegar a ser maior ou igual a 14
                //apenas adciona o texto da página em uma página da lista
                //de páginas e reseta o contador de linhas
                if (num >= 14) {
                    pages.add(pageText);
                    pageText = Component.literal("");
                    num = 0;
                }
            }

            //aqui é apenas para registrar a página final caso tenha sobrado lugares
            if (num > 0) {
                pages.add(pageText);
            }

            //este bloco é onde tenho que fazer toda a viadagem
            //da mojang que resolveu colocar aquela viadagem do filterable
            //para fazer a sua censura idiota
            //tudo em nome de "proteger as crianças"
            //que pra mim é só uma desculpa para uma grande viadagem
            Filterable<String> titleFilterable = Filterable.passThrough("Livro de locais");

            List<Filterable<Component>> pagesFilterable = new ArrayList<>();
            for (Component page : pages) {
                pagesFilterable.add(Filterable.passThrough(page));
            }

            //Depois de toda a viadagem anterior, abaixo fica
            //o registro do livro em um writtenbook
            //que será onde o livro abrirá
            WrittenBookContent content = new WrittenBookContent(
                    titleFilterable,
                    Minecraft.getInstance().player.getName().getString(),
                    0,
                    pagesFilterable,
                    false
            );

            //aqui é para criar a tela que abrira o livro
            BookViewScreen.BookAccess bookAccess = new BookViewScreen.BookAccess(
                    content.pages().stream().map(net.minecraft.server.network.Filterable::raw).toList()
            );

            //e aqui abre a tela do livro
            Minecraft.getInstance().setScreen(new BookViewScreen(bookAccess));
        });
    }

    //esse aqui é o algoritimo para apagar um lugar onde o jogador clicou no X
    public static void executeDeleteLogic(ServerPlayer player, int targetIndex) {
        //essa parte é para pegar o livro na mão principal
        ItemStack tpBook = player.getMainHandItem();

        //se n tiver na mão principal, ele tenta
        //pegar o livro na mão secundária do jogador
        if (!(tpBook.getItem() instanceof CoordinatesBookItem)) {
            tpBook = player.getOffhandItem();
        }

        //se o jogador n ta segurando o livro em nenhuma das mãos
        //ae apenas barra e da return
        if (!(tpBook.getItem() instanceof CoordinatesBookItem)) {
            player.sendSystemMessage(Component.literal("§cERROR: Você não tem o livro para usar esse comando"));
            return;
        }

        //aqui serve para checar se o jogador tem permissão para usar o comando /tp
        var dispatcher = player.level().getServer().getCommands().getDispatcher();
        var tpNode = dispatcher.getRoot().getChild("tp");

        //se ele tiver permissão para usar o comando /tp...
        if (tpNode != null && tpNode.getRequirement().test(player.createCommandSourceStack())) {
            //ele pega a lista de lugares salvos
            List<TPData> placeList = new ArrayList<>(tpBook.getOrDefault(
                    TPDataComponents.REGISTERED_PLACES.get(), List.of()
            ));

            //verifica se o indice é maior ou igual a 0 e se o indice
            //é menor que a quantidade de lugares
            if (targetIndex >= 0 && targetIndex < placeList.size()) {
                //e então apaga o lugar que foi clicado
                placeList.remove(targetIndex);
                tpBook.set(TPDataComponents.REGISTERED_PLACES.get(), placeList);

                PacketDistributor.sendToPlayer(player, new CoordinateList(placeList));
            }
        } else {
            //se o jogador n tiver permissão para usar /tp, ae só manda uma mensagem de erro
            player.sendSystemMessage(Component.literal("§cERROR: Você não tem poder para usar esse comando"));
        }
    }

    //aqui é um comando para abrir a tela de registrar o lugar
    public static void handleOpenNamingScreen(final OpenNamingScreenPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            net.minecraft.client.Minecraft.getInstance().setScreen(new PlaceNamingScreen());
        });
    }

    //após o jogador clicar em confirmar, o registro será feito aqui
    public static void handleConfirmRegistration(final ConfirmRegistrationPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            //aqui verifica se é server side
            if (context.player() instanceof ServerPlayer player) {
                //pega o livro na mão do jogador
                ItemStack tpBook = player.getItemInHand(player.getUsedItemHand());

                //registra o comando de teleporte para o jogador
                String tp = "/execute as " + player.getName().getString() + " run tp " + player.getName().getString() +
                        " " + player.getBlockX() + " " + player.getBlockY() + " " + player.getBlockZ();

                //pega a lista de lugares registrado no livro
                List<TPData> placeList = new ArrayList<>(tpBook.getOrDefault(
                        TPDataComponents.REGISTERED_PLACES.get(), List.of()
                ));

                //registra o novo lugar
                TPData newPlace = new TPData(data.placeName(), tp);
                placeList.add(newPlace);

                //salva ele na lista
                tpBook.set(TPDataComponents.REGISTERED_PLACES.get(), placeList);

                //manda uma mensagem dizendo que foi registrado
                player.sendSystemMessage(Component.literal("Local '" + data.placeName() + "' registrado com sucesso!"));
            }
        });
    }
}
