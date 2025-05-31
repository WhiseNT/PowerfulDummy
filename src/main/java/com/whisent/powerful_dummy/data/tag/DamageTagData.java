package com.whisent.powerful_dummy.data.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

import java.util.List;

public class DamageTagData {
    public List<DamageTagEntry> tags;

    public static class DamageTagEntry {
        public String damageName;
        public String displayColor;

        public ResourceKey<DamageType> getTag() {
            return ResourceKey.create(Registries.DAMAGE_TYPE,new ResourceLocation(damageName));
        }

        public int getDisplayColor() {
            if (displayColor == null || displayColor.isEmpty()) {
                return 0xffffff;
            }
            try {
                return Integer.decode(displayColor);
            } catch (NumberFormatException e) {
                return 0xffffff;
            }
        }
    }
}
