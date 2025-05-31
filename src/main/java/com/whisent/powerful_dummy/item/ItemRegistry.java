package com.whisent.powerful_dummy.item;

import com.whisent.powerful_dummy.Powerful_dummy;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> Items =
            DeferredRegister.create(ForgeRegistries.ITEMS, Powerful_dummy.MODID);
    public static final RegistryObject<Item> DUMMY_STAND = Items.register("dummy_stand",
            () -> new TestDummyItem(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        Items.register(eventBus);}
}
