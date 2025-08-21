package com.whisent.powerful_dummy.gui.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomFocusButton extends Button {

    protected AttributeAutoCompleteEditBox editBox;

    protected CustomFocusButton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration, AttributeAutoCompleteEditBox editBox) {
        super(x, y, width, height, message, onPress, createNarration);
        this.editBox = editBox;
    }

    protected CustomFocusButton(CustomFocusButton.Builder builder, AttributeAutoCompleteEditBox editBox) {
        this(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration, editBox);
        this.setTooltip(builder.tooltip);
    }



    @Override
    public boolean isHovered() {
        if (editBox != null && editBox.displaySuggestions) {
            return false;
        } else {
            return super.isHovered();
        }
    }

    @Override
    public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
        this.isHovered = false;
        return super.mouseClicked(p_93641_, p_93642_, p_93643_);
    }

    public static CustomFocusButton.Builder builder(Component message, OnPress onPress, AttributeAutoCompleteEditBox editBox) {
        return new Builder(message, onPress, editBox);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final Component message;
        private final OnPress onPress;
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private final AttributeAutoCompleteEditBox editBox;
        private CreateNarration createNarration = Button.DEFAULT_NARRATION;

        public Builder(Component message, OnPress onPress, AttributeAutoCompleteEditBox editBox) {
            this.message = message;
            this.onPress = onPress;
            this.editBox = editBox;
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder bounds(int x, int y, int width, int height) {
            return pos(x, y).size(width, height);
        }

        public Builder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder createNarration(CreateNarration narration) {
            this.createNarration = narration;
            return this;
        }

        public CustomFocusButton build() {
            return new CustomFocusButton(this, editBox);
        }
    }
}
