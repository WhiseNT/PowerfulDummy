package com.whisent.powerful_dummy.kjs;

import com.whisent.powerful_dummy.data.AttributeData;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.HashMap;
import java.util.Map;

public class DummyCustomizer {
    private static int curiosSlots = 8;
    private static final Map<Attribute, Integer> attributeColorMap = new HashMap<>();
    private static final Map<ResourceLocation, Integer> damageTypeColorMap = new HashMap<>();
    public static int getCuriosSlots() {
        return curiosSlots;
    }
    public static void setCuriosSlots(int curiosSlots) {
        DummyCustomizer.curiosSlots = curiosSlots;
    }
    public static Map<Attribute, Integer> getAttributeColorMap() {
        return attributeColorMap;
    }
    public static void addAttributeColor(Attribute attribute, int color) {
        attributeColorMap.put(attribute, color);
    }

    public static int getAttributeColor(Attribute attribute) {
        return attributeColorMap.getOrDefault(attribute, null);
    }
    public static Map<ResourceLocation, Integer> getDamageTypeColorMap() {
        return damageTypeColorMap;
    }
    public static void addDamageTypeColor(Holder<DamageType> damageType, int color) {
        damageTypeColorMap.put(damageType.getKey().location(), color);
    }
    public static int getDamageTypeColor(Holder<DamageType> damageType) {
        return damageTypeColorMap.getOrDefault(damageType.getKey().location(), 0);
    }
}
