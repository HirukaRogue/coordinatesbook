package net.hirukarogue.coordinatesbook.items;

import net.hirukarogue.coordinatesbook.CoordinatesBook;
import net.hirukarogue.coordinatesbook.items.coordinatesbook.CoordinatesBookItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CoordinatesBook.MODID);

    public static final DeferredItem<Item> TPBOOK = ITEMS.register("tp_book", () -> new CoordinatesBookItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
