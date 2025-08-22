package com.whisent.powerful_dummy.entity;

import com.whisent.powerful_dummy.Powerful_dummy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class DummyEntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Powerful_dummy.MODID);

    public static final Supplier<EntityType<TestDummyEntity>> TEST_DUMMY =
            ENTITIES.register("test_dummy",
            () -> EntityType.Builder.of(TestDummyEntity::new, MobCategory.MISC)
                    .sized(0.6F, 2.7F)
                    .build("test_dummy"));
    public static final Supplier<EntityType<TestDummyEntity>> TEST_DUMMY_UNDEAD =
            ENTITIES.register("test_dummy_undead",
            () -> EntityType.Builder.of(TestDummyEntity::new, MobCategory.MISC)
                    .sized(0.6F, 2.7F)
                    .build("test_dummy_undead"));
    public static final Supplier<EntityType<TestDummyEntity>> TEST_DUMMY_ARTHROPOD =
            ENTITIES.register("test_dummy_arthropod",
            () -> EntityType.Builder.of(TestDummyEntity::new, MobCategory.MISC)
                    .sized(0.6F, 2.7F)
                    .build("test_dummy_arthropod"));
    public static final Supplier<EntityType<TestDummyEntity>> TEST_DUMMY_WATER =
            ENTITIES.register("test_dummy_water",
            () -> EntityType.Builder.of(TestDummyEntity::new, MobCategory.MISC)
                    .sized(0.6F, 2.7F)
                    .build("test_dummy_water"));
    public static final Supplier<EntityType<TestDummyEntity>> TEST_DUMMY_ILLAGER =
            ENTITIES.register("test_dummy_illager",
            () -> EntityType.Builder.of(TestDummyEntity::new, MobCategory.MISC)
                    .sized(0.6F, 2.7F)
                    .build("test_dummy_illager"));
    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}