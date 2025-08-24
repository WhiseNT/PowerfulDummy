package com.whisent.powerful_dummy;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import com.whisent.powerful_dummy.client.DpsActionBar;
import com.whisent.powerful_dummy.entity.DummyEntityRegistry;
import com.whisent.powerful_dummy.entity.TestDummyEntity;
import com.whisent.powerful_dummy.entity.client.TestDummyModel;
import com.whisent.powerful_dummy.entity.client.TestDummyRenderer;
import com.whisent.powerful_dummy.gui.MenuRegistry;
import com.whisent.powerful_dummy.gui.TestDummyEntityScreen;
import com.whisent.powerful_dummy.item.ItemRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Powerful_dummy.MODID)
public class Powerful_dummy {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "powerful_dummy";
    public static final Logger LOGGER = LogUtils.getLogger();
    public Powerful_dummy(IEventBus modEventBus) {


        modEventBus.addListener(this::commonSetup);

        ItemRegistry.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        MenuRegistry.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        DummyEntityRegistry.register(modEventBus);
        modEventBus.addListener(EventPriority.NORMAL, false, EntityAttributeCreationEvent.class, event -> {
            event.put(DummyEntityRegistry.TEST_DUMMY.get(), TestDummyEntity.setAttributes());
            /*
            event.put(DummyEntityRegistry.TEST_DUMMY_UNDEAD.get(), TestDummyEntity.setAttributes());
            event.put(DummyEntityRegistry.TEST_DUMMY_ILLAGER.get(), TestDummyEntity.setAttributes());
            event.put(DummyEntityRegistry.TEST_DUMMY_WATER.get(), TestDummyEntity.setAttributes());
            event.put(DummyEntityRegistry.TEST_DUMMY_ARTHROPOD.get(), TestDummyEntity.setAttributes());


             */
        });
        modEventBus.addListener(this::onModConfigEvent);
        new DpsActionBar();
        PowerfulDummyConfig.register();

    }
    private void onModConfigEvent(final ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            // 重新加载配置
            PowerfulDummyConfig.bake();
        }
    }
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final Supplier<CreativeModeTab> DUMMY_TAB = CREATIVE_MODE_TABS.register("creativetab_dummy",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ItemRegistry.DUMMY_STAND.get()))
                    .title(Component.translatable("creativetab.powerful_dummy"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ItemRegistry.DUMMY_STAND.get());
                    })
                    .build());

    private void commonSetup(final FMLCommonSetupEvent event) {
        if (FMLLoader.getDist().isClient()) {
            event.enqueueWork(() -> {
                EntityRenderers.register(DummyEntityRegistry.TEST_DUMMY.get(),
                                TestDummyRenderer::new);
                /*
                EntityRenderers.register(DummyEntityRegistry.TEST_DUMMY_UNDEAD.get(),
                            TestDummyRenderer::new);
                EntityRenderers.register(DummyEntityRegistry.TEST_DUMMY_ARTHROPOD.get(),
                                TestDummyRenderer::new);
                EntityRenderers.register(DummyEntityRegistry.TEST_DUMMY_WATER.get(),
                                TestDummyRenderer::new);
                EntityRenderers.register(DummyEntityRegistry.TEST_DUMMY_ILLAGER.get(),
                        TestDummyRenderer::new);

                 */


            });
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
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
            event.register(MenuRegistry.TEST_DUMMY_MENU.get(), TestDummyEntityScreen::new);
        }
        @SubscribeEvent
        public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(TestDummyModel.LAYER_LOCATION, TestDummyModel::createBodyLayer);
        }
        public static final KeyMapping CLEAR_DPS_DATA =
                new KeyMapping("key.powerful_dummy.cleardps",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_GRAVE_ACCENT,
                        "key.categories.powerful_dummy");
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(ClientModEvents.CLEAR_DPS_DATA);
        }

    }
}
