package com.whisent.powerful_dummy.item;

import com.whisent.powerful_dummy.Powerful_dummy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, Powerful_dummy.MODID);

    // 使用 Supplier<Item> 而不是 RegistryObject<Item>
    public static final Supplier<Item> DUMMY_STAND = ITEMS.register("dummy_stand",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
