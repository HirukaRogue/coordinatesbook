package net.hirukarogue.coordinatesbook.items.coordinatesbook;

import net.hirukarogue.coordinatesbook.data.CoordinateList;
import net.hirukarogue.coordinatesbook.data.TPDataComponents;
import net.hirukarogue.coordinatesbook.data.TPData;
import net.hirukarogue.coordinatesbook.data.placeNaming.OpenNamingScreenPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class CoordinatesBookItem extends Item {
    public CoordinatesBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide()) {
            var dispatcher = player.level().getServer().getCommands().getDispatcher();
            var tpNode = dispatcher.getRoot().getChild("tp");

            if (tpNode != null && tpNode.getRequirement().test(player.createCommandSourceStack())) {
                if (player.isCrouching()) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        PacketDistributor.sendToPlayer(serverPlayer, new OpenNamingScreenPayload());
                    }

                    return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
                } else {
                    ItemStack itemStack = player.getItemInHand(usedHand);

                    List<TPData> locaisSalvos = itemStack.getOrDefault(
                            TPDataComponents.REGISTERED_PLACES.get(),
                            List.of()
                    );

                    if (player instanceof ServerPlayer serverPlayer) {
                        PacketDistributor.sendToPlayer(serverPlayer, new CoordinateList(locaisSalvos));
                    }

                    return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
                }
            } else {
                player.sendSystemMessage(Component.literal("Você não tem poder para usar esse livro"));
                return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
            }
        }

        return super.use(level, player, usedHand);
    }
}
