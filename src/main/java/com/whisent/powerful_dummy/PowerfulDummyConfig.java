package com.whisent.powerful_dummy;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class PowerfulDummyConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // 用actionbar显示数据配置项
    private static final ModConfigSpec.BooleanValue USE_ACTIONBAR_TO_SHOW_DATA = BUILDER
            .comment("是否使用ActionBar显示数据")
            .define("useActionbarToShowData", false);
    public static boolean useActionbarToShowData;
    static {
        BUILDER.push("Powerful Dummy Settings");
        BUILDER.pop();
        SPEC = BUILDER.build();
    }



    public static void register() {
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, SPEC, "powerful_dummy-common.toml");
    }

    public static void bake() {
        useActionbarToShowData = USE_ACTIONBAR_TO_SHOW_DATA.get();
    }
}

