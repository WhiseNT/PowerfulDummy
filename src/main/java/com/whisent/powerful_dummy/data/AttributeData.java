package com.whisent.powerful_dummy.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class AttributeData {
    public List<AttributeEntry> attributes;
    public static class AttributeEntry {
        public String key;
        public boolean usePercent;
        public String displayColor;
        public String valueColor;

        public Attribute getAttribute() {
            return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(key));
        }

        public int getDisplayColor() {
            if (displayColor == null || displayColor.trim().isEmpty() || displayColor.equalsIgnoreCase("default")) {
                return 0x2e2e2e;
            }
            try {
                return Integer.decode(displayColor);
            } catch (NumberFormatException e) {
                return 0x2e2e2e;
            }
        }

        public int getValueColor() {
            if (valueColor == null || valueColor.trim().isEmpty() || valueColor.equalsIgnoreCase("default")) {
                return 0x2e2e2e;
            }
            try {
                return Integer.decode(valueColor);
            } catch (NumberFormatException e) {
                return 0x2e2e2e;
            }
        }
    }
}
