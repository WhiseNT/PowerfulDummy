package com.whisent.test_dummy.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobEffectAutoCompleteEditBox extends AbstractAutoCompleteEditBox{
    public MobEffectAutoCompleteEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
    }
    @Override
    public void loadSuggestions() {
        RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
        Registry<MobEffect> mobEffectRegistry = registryAccess.registryOrThrow(Registries.MOB_EFFECT);
        suggestions.clear();
        for (ResourceLocation key : mobEffectRegistry.keySet()) {
            MobEffect effect = mobEffectRegistry.get(key);
            if (effect != null) {
                String name = Component.translatable(effect.getDescriptionId()).getString();
                suggestions.add(name);
            }
        }
        suggestions.sort(String.CASE_INSENSITIVE_ORDER); // 排序
    }


    @Override
    protected void drawSuggestions(GuiGraphics guiGraphics, List<String> list, int mouseX, int mouseY) {
        super.drawSuggestions(guiGraphics, list, mouseX, mouseY);
    }
}
