package com.whisent.powerful_dummy;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;


// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Powerful_dummy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PowerfulDummyConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // 用actionbar显示数据配置项
    private static final ForgeConfigSpec.BooleanValue USE_ACTIONBAR_TO_SHOW_DATA = BUILDER
            .comment("是否使用ActionBar显示数据")
            .define("useActionbarToShowData", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean useActionbarToShowData;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        useActionbarToShowData = USE_ACTIONBAR_TO_SHOW_DATA.get();
    }
}

