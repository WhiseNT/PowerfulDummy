package com.whisent.test_dummy.data.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

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
                return 0x2e2e2e; // 默认灰色
            }
            try {
                return Integer.decode(displayColor);
            } catch (NumberFormatException e) {
                return 0x2e2e2e;
            }
        }
    }
}
