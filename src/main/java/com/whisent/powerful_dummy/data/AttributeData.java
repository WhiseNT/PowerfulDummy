package com.whisent.powerful_dummy.data;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.List;

public class AttributeData {
    public List<AttributeEntry> attributes;
    public static class AttributeEntry {
        public String key;
        public boolean usePercent;
        public String displayColor;
        public String valueColor;

        public Attribute getAttribute() {
            return BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.parse(key));
        }
        public Holder<Attribute> getAttributeHolder() {
            return BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse(key)).isPresent()
                    ? BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse(key)).get() : null;
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
