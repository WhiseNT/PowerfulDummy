package com.whisent.test_dummy;

import com.mojang.logging.LogUtils;
import com.whisent.test_dummy.data.AttributeLoader;
import com.whisent.test_dummy.entity.DummyEntityRegistry;
import com.whisent.test_dummy.entity.TestDummyEntity;
import com.whisent.test_dummy.entity.client.TestDummyModel;
import com.whisent.test_dummy.entity.client.TestDummyRenderer;
import com.whisent.test_dummy.gui.MenuRegistry;
import com.whisent.test_dummy.gui.TestDummyEntityScreen;
import com.whisent.test_dummy.item.ItemRegistry;
import com.whisent.test_dummy.network.NetWorkHandler;
import com.whisent.test_dummy.utils.EditBoxInfoHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashSet;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Test_dummy.MODID)
public class Test_dummy {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "test_dummy";
    public static final String DUMMY_ID = "test_dummy";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static EditBoxInfoHelper helper;
    @SuppressWarnings({"deprecation"})
    public Test_dummy() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        ItemRegistry.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        MenuRegistry.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        DummyEntityRegistry.register();
        modEventBus.addListener(EventPriority.NORMAL, false, EntityAttributeCreationEvent.class, event -> {
            event.put(DummyEntityRegistry.TEST_DUMMY.get(), TestDummyEntity.setAttributes());
        });
        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> DUMMY_TAB = CREATIVE_MODE_TABS.register("creativetab_dummy",
            ()->CreativeModeTab.builder()
                    .icon(()->new ItemStack(ItemRegistry.DUMMY_STAND.get()))
                    .title(Component.translatable("creativetab.test_dummy"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ItemRegistry.DUMMY_STAND.get());
                    })
                    .build());

    private void commonSetup(final FMLCommonSetupEvent event) {
        if (FMLLoader.getDist().isClient()) {
            event.enqueueWork(() -> {
                EntityRenderers
                        .register(DummyEntityRegistry.TEST_DUMMY.get(), TestDummyRenderer::new);

            });
            event.enqueueWork(NetWorkHandler::register);
        }

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(MenuRegistry.TEST_DUMMY_MENU.get(), TestDummyEntityScreen::new);

        }
        @SubscribeEvent
        public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(TestDummyModel.LAYER_LOCATION, TestDummyModel::createBodyLayer);
        }

    }
}
