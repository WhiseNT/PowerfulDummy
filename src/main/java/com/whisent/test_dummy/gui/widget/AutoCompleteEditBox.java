package com.whisent.test_dummy.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.whisent.test_dummy.Test_dummy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoCompleteEditBox extends EditBox {
    private final List<String> suggestions = new ArrayList<>();
    private int selectedIndex = -1;
    private boolean showSuggestions = false;
    private int scrollOffset = 0; // 新增：用于控制滚动的偏移量
    private static final int SUGGESTIONS_VISIBLE_COUNT = 10; // 每次最多显示多少个建议

    public AutoCompleteEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
    }

    // 加载所有药水效果名称
    private void loadMobEffectNames() {
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.isFocused()) {
            loadMobEffectNames();
            List<String> filtered = getSuggestions(getValue());
            if (!filtered.isEmpty() || getValue().isEmpty()) { // 如果文本框为空或有匹配项，则显示建议
                drawSuggestions(guiGraphics, filtered.isEmpty() ? suggestions : filtered, mouseX, mouseY);
            }
        }
    }

    private List<String> getSuggestions(String input) {
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void drawSuggestions(GuiGraphics guiGraphics, List<String> list, int mouseX, int mouseY) {
        int x = this.getX();
        int y = this.getY() + this.getHeight();
        int width = this.getWidth();

        // 计算实际需要绘制的建议数量
        int visibleCount = Math.min(SUGGESTIONS_VISIBLE_COUNT, list.size());

        // 背景
        guiGraphics.fill(x, y, x + width, y + visibleCount * 12, 0x90000000);

        for (int i = 0; i < visibleCount; i++) {
            int index = i + scrollOffset;
            if (index >= list.size()) break;

            String suggestion = list.get(index);
            boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y + i * 12 && mouseY <= y + (i + 1) * 12;

            if (hovered || index == selectedIndex) {
                guiGraphics.fill(x, y + i * 12, x + width, y + (i + 1) * 12, 0xB0555555);
            }

            guiGraphics.drawString(Minecraft.getInstance().font, suggestion, x + 4, y + i * 12 + 2, 0xE0E0E0);
        }
    }

    public boolean handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) return false;

        List<String> filtered = getSuggestions(getValue());

        switch (keyCode) {
            case 265: // 上方向键
                if (selectedIndex > 0) selectedIndex--;
                else if (!filtered.isEmpty()) selectedIndex = filtered.size() - 1;
                updateTextFromSelection(filtered);
                return true;

            case 264: // 下方向键
                if (selectedIndex < filtered.size() - 1) selectedIndex++;
                else selectedIndex = 0;
                updateTextFromSelection(filtered);
                return true;

            case 257: // Enter
            case 335: // Numpad Enter
                if (selectedIndex >= 0 && selectedIndex < filtered.size()) {
                    setValue(filtered.get(selectedIndex));
                    selectedIndex = -1;
                    showSuggestions = false;
                    return true;
                }
                return false;

            default:
                // 只有当输入发生变化时才重置selectedIndex
                if (keyCode != 259) { // 不是Backspace
                    selectedIndex = -1;
                    showSuggestions = true;
                }
                return false;
        }
    }

    private void updateTextFromSelection(List<String> list) {
        if (selectedIndex >= 0 && selectedIndex < list.size()) {
            setValue(list.get(selectedIndex));
        }
    }

    public boolean handleMouseClick(double mouseX, double mouseY) {
        if (!isFocused()) return false;

        List<String> filtered = getSuggestions(getValue());
        if (filtered.isEmpty()) return false;

        int x = this.getX();
        int yStart = this.getY() + this.getHeight();

        for (int i = 0; i < SUGGESTIONS_VISIBLE_COUNT; i++) {
            int index = i + scrollOffset;
            if (index >= filtered.size()) break;

            int yMin = yStart + i * 12;
            int yMax = yMin + 12;
            if (mouseX >= x && mouseX <= x + this.getWidth() && mouseY >= yMin && mouseY <= yMax) {
                setValue(filtered.get(index));
                selectedIndex = -1;
                showSuggestions = false;
                return true;
            }
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isFocused()) return false;

        List<String> filtered = getSuggestions(getValue());
        if (!filtered.isEmpty()) {
            int maxScrollOffset = Math.max(0, filtered.size() - SUGGESTIONS_VISIBLE_COUNT);
            if (delta < 0) { // 向下滚动
                scrollOffset = Math.min(scrollOffset + 1, maxScrollOffset);
            } else if (delta > 0) { // 向上滚动
                scrollOffset = Math.max(0, scrollOffset - 1);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean handled = super.charTyped(codePoint, modifiers);
        if (handled) {
            selectedIndex = -1; // 输入变化时重置selectedIndex
            showSuggestions = true;
            scrollOffset = 0; // 重置滚动偏移量
        }
        return handled;
    }
}
