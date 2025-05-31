package com.whisent.powerful_dummy.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.HashMap;
import java.util.List;


public class AttributeAutoCompleteEditBox extends AbstractAutoCompleteEditBox {
    public LivingEntity entity;
    public final HashMap<String,Boolean> attributesNotZero;
    public boolean hovered = false;
    public AttributeAutoCompleteEditBox(Font font, int x, int y, int width, int height, Component message,LivingEntity entity) {
        super(font, x, y, width, height, message);
        this.entity = entity;
        attributesNotZero = new HashMap<>();
        setVisibleCount(5);
    }

    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public void loadSuggestions() {
        //Test_dummy.LOGGER.debug(this.entity.toString());
        if (getEntity().isAlive()) {
            suggestions.clear();
            attributesNotZero.clear();
            RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
            Registry<Attribute> attributeRegistry = registryAccess.registryOrThrow(Registries.ATTRIBUTE);
            for (ResourceLocation key : attributeRegistry.keySet()) {
                Attribute attribute = attributeRegistry.get(key);
                if (attribute != null && getEntity().getAttribute(attribute) != null) {
                    String name = Component.translatable(attribute.getDescriptionId()).getString();
                    if (getEntity().getAttribute(attribute).getBaseValue() != 0.0D) {
                        suggestions.add(name);
                        attributesNotZero.putIfAbsent(name,true);
                    } else {
                        suggestions.add(name);
                    }
                }
            }
            suggestions.sort(String.CASE_INSENSITIVE_ORDER);
        }
    }
    @Override
    protected void drawSuggestions(GuiGraphics guiGraphics, List<String> list, int mouseX, int mouseY) {
        this.loadSuggestions();
        int x = this.getX();
        int y = this.getY() + this.getHeight();
        int width = this.getWidth();
        int visibleCount = Math.min(SUGGESTIONS_VISIBLE_COUNT, list.size());
        guiGraphics.fill(x, y, x + width, y + visibleCount * 12, 0x90000000);
        for (int i = 0; i < visibleCount; i++) {
            int index = i + scrollOffset;
            if (index >= list.size()) break;
            String suggestion = list.get(index);
            boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y + i * 12 && mouseY <= y + (i + 1) * 12;
            if (hovered || index == selectedIndex) {
                guiGraphics.fill(x, y + i * 12, x + width, y + (i + 1) * 12, 0xB0555555);
            }
            String finalSuggestion = suggestion;
            if (suggestion.length() >= 7 && Minecraft.getInstance().getLanguageManager().getSelected().equals("zh_cn")) {
                finalSuggestion = suggestion.substring(0, 7) + "..";
            } else if (suggestion.length() >= 11) {
                finalSuggestion = suggestion.substring(0, 11) + "..";
            }
            if (attributesNotZero.getOrDefault(suggestion,false)) {
                guiGraphics.drawString(Minecraft.getInstance().font, finalSuggestion, x + 4, y + i * 12 + 2, 0xfca800);
            } else {
                guiGraphics.drawString(Minecraft.getInstance().font, finalSuggestion, x + 4, y + i * 12 + 2, 0xE0E0E0);
            }

        }
    }
}
