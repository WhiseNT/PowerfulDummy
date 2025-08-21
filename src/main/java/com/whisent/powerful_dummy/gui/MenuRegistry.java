package com.whisent.powerful_dummy.gui;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.entity.TestDummyEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Powerful_dummy.MODID);

    // 使用 register 方法而不是 DataComponent
    public static final DeferredHolder<MenuType<?>, MenuType<TestDummyEntityMenu>> TEST_DUMMY_MENU =
            MENUS.register("test_menu", TestDummyEntityMenu::createMenuType);

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
