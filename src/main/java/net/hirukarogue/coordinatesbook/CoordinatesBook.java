package net.hirukarogue.coordinatesbook;

import net.hirukarogue.coordinatesbook.data.TPDataComponents;
import net.hirukarogue.coordinatesbook.events.TPBookCommandEvents;
import net.hirukarogue.coordinatesbook.events.TPBookEvents;
import net.hirukarogue.coordinatesbook.items.ItemRegistry;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CoordinatesBook.MODID)
public class CoordinatesBook {

    public static final String MODID = "coordinatesbook";

    public static final Logger LOGGER = LogUtils.getLogger();

    public CoordinatesBook(IEventBus modEventBus, ModContainer modContainer) {

        ItemRegistry.register(modEventBus);

        TPDataComponents.register(modEventBus);

        modEventBus.addListener(TPBookEvents::registerNetworkStuff);

        TPBookCommandEvents commandEvents = new TPBookCommandEvents();
        NeoForge.EVENT_BUS.addListener(commandEvents::onRegisterCommands);

        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);


        modEventBus.addListener(this::addCreative);


        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ItemRegistry.TPBOOK);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
