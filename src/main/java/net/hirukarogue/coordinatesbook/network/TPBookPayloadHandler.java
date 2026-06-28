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

public class TPBookPayloadHandler {
    public static void handleTPDataNetwork(final CoordinateList data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            List<Component> pages = new ArrayList<>();
            MutableComponent pageText = Component.literal("");

            int num = 0;
            int index = 0;

            for (TPData place : data.registeredPlaces()) {
                Component hyperlink = Component.literal("- [" + place.name() + "]\n")
                        .withStyle(style -> style
                                .withColor(0x00AAFF)
                                .withUnderlined(false)
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.RUN_COMMAND,
                                        place.command()
                                ))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Clique para teleportar!")))
                        );

                final int currentIndex = index;
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

                pageText.append(hyperlink).append(" ").append(delete);

                num++;
                index++;
                if (place.name().length() > 15) {
                    num++;
                }
                if (num > 14) {
                    pages.add(pageText);
                    pageText = Component.literal("");
                    num = 0;
                }
            }

            if (num > 0) {
                pages.add(pageText);
            }

            Filterable<String> titleFilterable = Filterable.passThrough("Livro de locais");

            List<Filterable<Component>> pagesFilterable = new ArrayList<>();
            for (Component page : pages) {
                pagesFilterable.add(Filterable.passThrough(page));
            }

            WrittenBookContent content = new WrittenBookContent(
                    titleFilterable,
                    Minecraft.getInstance().player.getName().getString(),
                    0,
                    pagesFilterable,
                    false
            );

            BookViewScreen.BookAccess bookAccess = new BookViewScreen.BookAccess(
                    content.pages().stream().map(net.minecraft.server.network.Filterable::raw).toList()
            );

            Minecraft.getInstance().setScreen(new BookViewScreen(bookAccess));
        });
    }

    public static void executeDeleteLogic(ServerPlayer player, int targetIndex) {
        ItemStack tpBook = player.getMainHandItem();

        if (!(tpBook.getItem() instanceof CoordinatesBookItem)) {
            tpBook = player.getOffhandItem();
        }

        if (!(tpBook.getItem() instanceof CoordinatesBookItem)) {
            player.sendSystemMessage(Component.literal("§cERROR: Você não tem o livro para usar esse comando"));
            return;
        }

        var dispatcher = player.level().getServer().getCommands().getDispatcher();
        var tpNode = dispatcher.getRoot().getChild("tp");

        if (tpNode != null && tpNode.getRequirement().test(player.createCommandSourceStack())) {
            List<TPData> placeList = new ArrayList<>(tpBook.getOrDefault(
                    TPDataComponents.REGISTERED_PLACES.get(), List.of()
            ));

            if (targetIndex >= 0 && targetIndex < placeList.size()) {
                placeList.remove(targetIndex);
                tpBook.set(TPDataComponents.REGISTERED_PLACES.get(), placeList);

                PacketDistributor.sendToPlayer(player, new CoordinateList(placeList));
            }
        } else {
            player.sendSystemMessage(Component.literal("§cERROR: Você não tem poder para usar esse comando"));
        }
    }

    public static void handleOpenNamingScreen(final OpenNamingScreenPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            net.minecraft.client.Minecraft.getInstance().setScreen(new PlaceNamingScreen());
        });
    }

    public static void handleConfirmRegistration(final ConfirmRegistrationPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack tpBook = player.getItemInHand(player.getUsedItemHand());

                String tp = "/execute as " + player.getName().getString() + " run tp " + player.getName().getString() +
                        " " + player.getBlockX() + " " + player.getBlockY() + " " + player.getBlockZ();

                List<TPData> placeList = new ArrayList<>(tpBook.getOrDefault(
                        TPDataComponents.REGISTERED_PLACES.get(), List.of()
                ));

                TPData newPlace = new TPData(data.placeName(), tp);
                placeList.add(newPlace);

                tpBook.set(TPDataComponents.REGISTERED_PLACES.get(), placeList);

                player.sendSystemMessage(Component.literal("Local '" + data.placeName() + "' registrado com sucesso!"));
            }
        });
    }
}
