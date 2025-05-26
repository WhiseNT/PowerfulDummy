package com.whisent.test_dummy.data;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    public static Map<ResourceLocation, AttributeData> configs = new HashMap<>();

    public AttributeLoader() {
        super(GSON, "attributes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        System.out.println("Loading attributes");
        Type listType = new TypeToken<List<AttributeData.AttributeEntry>>(){}.getType();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceLocationJsonElementMap.entrySet()) {
            ResourceLocation location = entry.getKey();
            AttributeData config = new AttributeData();
            config.attributes = GSON.fromJson(entry.getValue().getAsJsonObject().get("attributes"), listType);
            configs.put(location, config);
        }
        System.out.println(configs);
    }

    public static AttributeData getData(ResourceLocation id) {

        return configs.getOrDefault(id, null);
    }
}
