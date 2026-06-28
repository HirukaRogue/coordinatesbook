package net.hirukarogue.coordinatesbook.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hirukarogue.coordinatesbook.CoordinatesBook;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

//aqui fica o registro NBT dos lugares no livro
public class TPDataComponents {
    //registro básico
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(
            BuiltInRegistries.DATA_COMPONENT_TYPE, CoordinatesBook.MODID
    );

    //codec para cada lugar
    private static final Codec<TPData> TP_DATA_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(TPData::name),
            Codec.STRING.fieldOf("command").forGetter(TPData::command)
    ).apply(instance, TPData::new));

    //codec de todos os lugares registrados
    public static final Supplier<DataComponentType<List<TPData>>> REGISTERED_PLACES =
            COMPONENTS.register("registered_places", () -> DataComponentType.<List<TPData>>builder()
                    .persistent(Codec.list(TP_DATA_CODEC))
                    .build()
            );

    //registro de evento para o NBT
    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}
