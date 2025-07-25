package com.whisent.powerful_dummy.data.tag;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DamageTagLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    public static final Map<ResourceLocation, DamageTagData> CONFIGS = new HashMap<>();

    public DamageTagLoader() {
        super(GSON, "damage_colors");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        System.out.println("Loading tags");
        Type listType = new TypeToken<List<DamageTagData.DamageTagEntry>>(){}.getType();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceLocationJsonElementMap.entrySet()) {
            ResourceLocation location = entry.getKey();
            DamageTagData config = new DamageTagData();
            config.tags = GSON.fromJson(entry.getValue().getAsJsonObject().get("types"), listType);
            CONFIGS.put(location, config);
        }
        System.out.println(CONFIGS);
    }

    public static int findDisplayColor(DamageSource source) {
        for (DamageTagData config : CONFIGS.values()) {
            for (DamageTagData.DamageTagEntry entry : config.tags) {
                if (source == null) return 0xffffff;
                if (source.is(entry.getTag())) {
                    return entry.getDisplayColor();
                }
            }
        }
        return 0xffffff;
    }
}
