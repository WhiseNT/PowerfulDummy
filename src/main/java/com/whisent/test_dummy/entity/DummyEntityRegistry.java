package com.whisent.test_dummy.entity;

import com.whisent.test_dummy.Test_dummy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DummyEntityRegistry {
    // 使用 DeferredRegister 注册实体类型
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Test_dummy.MODID); // 替换为你的模组 ID
    // 注册 TestDummyEntity 实体类型
    public static final RegistryObject<EntityType<TestDummyEntity>> TEST_DUMMY =
            ENTITY_TYPES.register("test_dummy",
            () -> EntityType.Builder.of(TestDummyEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 2.7f) // 设置实体尺寸（宽度, 高度）
                    .clientTrackingRange(10)
                    .build(new ResourceLocation("test_dummy:test_dummy").toString())
                    );
    // 注册到模组事件总线
    @SuppressWarnings({"deprecation"})
    public static void register() {
        ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
