package com.whisent.powerful_dummy.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractAutoCompleteEditBox extends EditBox {
    protected final List<String> suggestions;
    protected int selectedIndex;
    protected int scrollOffset;
    protected int SUGGESTIONS_VISIBLE_COUNT;
    public boolean displaySuggestions = false;
    public AbstractAutoCompleteEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        this.suggestions = new ArrayList<>();
        this.selectedIndex = -1;
        this.scrollOffset = 0;
        this.SUGGESTIONS_VISIBLE_COUNT = 10;
    }
    public void setVisibleCount(int count) {
        this.SUGGESTIONS_VISIBLE_COUNT = count;
    };
    public abstract void loadSuggestions();

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        PoseStack poseStack = guiGraphics.pose();
        RenderSystem.enableBlend();
        poseStack.pushPose();
        poseStack.translate(0,0,1);
        if (this.isFocused()) {
            List<String> filtered = getSuggestions(getValue());
            if (!filtered.isEmpty() || getValue().isEmpty()) displaySuggestions = true;
            else {
                displaySuggestions = false;
            }
            if (displaySuggestions) { // 如果文本框为空或有匹配项，则显示建议
                drawSuggestions(guiGraphics, filtered.isEmpty() ? suggestions : filtered, mouseX, mouseY);
            }
        } else {
            displaySuggestions = false;
        }
        poseStack.popPose();
    }

    protected List<String> getSuggestions(String input) {
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }

    protected void drawSuggestions(GuiGraphics guiGraphics, List<String> list, int mouseX, int mouseY) {
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
            guiGraphics.drawString(Minecraft.getInstance().font, suggestion, x + 4, y + i * 12 + 2, 0xE0E0E0);
        }
    }

    public boolean handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) return false;
        List<String> filtered = getSuggestions(getValue());
        switch (keyCode) {
            case 265:
                if (selectedIndex > 0) selectedIndex--;
                else if (!filtered.isEmpty()) selectedIndex = filtered.size() - 1;
                updateTextFromSelection(filtered);
                return true;
            case 264:
                if (selectedIndex < filtered.size() - 1) selectedIndex++;
                else selectedIndex = 0;
                updateTextFromSelection(filtered);
                return true;
            case 257:
            case 335:
                if (selectedIndex >= 0 && selectedIndex < filtered.size()) {
                    setValue(filtered.get(selectedIndex));
                    selectedIndex = -1;
                    return true;
                }
                return false;
            default:
                if (keyCode != 259) {
                    selectedIndex = -1;
                }
                return false;
        }
    }
    protected void updateTextFromSelection(List<String> list) {
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
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (!isFocused()) return false;

        List<String> filtered = getSuggestions(getValue());
        if (!filtered.isEmpty()) {
            int maxScrollOffset = Math.max(0, filtered.size() - SUGGESTIONS_VISIBLE_COUNT);
            if (scrollY < 0) { // 向下滚动
                scrollOffset = Math.min(scrollOffset + 1, maxScrollOffset);
            } else if (scrollY > 0) { // 向上滚动
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
            selectedIndex = -1;
            scrollOffset = 0;
        }
        return handled;
    }
}