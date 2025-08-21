package com.whisent.powerful_dummy.entity;

import com.whisent.powerful_dummy.Powerful_dummy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class DummyEntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Powerful_dummy.MODID);

    // 使用 Supplier<EntityType<?>> 而不是 RegistryObject<EntityType<?>>
    public static final Supplier<EntityType<TestDummyEntity>> TEST_DUMMY = ENTITIES.register("test_dummy",
            () -> EntityType.Builder.of(TestDummyEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .build("test_dummy"));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}