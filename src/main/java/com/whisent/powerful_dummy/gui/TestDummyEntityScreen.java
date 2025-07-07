package com.whisent.powerful_dummy.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.data.AttributeData;
import com.whisent.powerful_dummy.data.AttributeLoader;
import com.whisent.powerful_dummy.entity.TestDummyEntity;
import com.whisent.powerful_dummy.gui.widget.AttributeAutoCompleteEditBox;
import com.whisent.powerful_dummy.gui.widget.CustomFocusButton;
import com.whisent.powerful_dummy.gui.widget.MobEffectAutoCompleteEditBox;
import com.whisent.powerful_dummy.network.DummyInfoPacket;
import com.whisent.powerful_dummy.network.NetWorkHandler;
import com.whisent.powerful_dummy.utils.EditBoxInfoHelper;
import com.whisent.powerful_dummy.utils.MobTypeHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.*;


@OnlyIn(Dist.CLIENT)
public class TestDummyEntityScreen extends AbstractContainerScreen<TestDummyEntityMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Powerful_dummy.MODID, "textures/gui/test_dummy_screen.png");
    private static final ResourceLocation CURIOS_TEX =
            new ResourceLocation(Powerful_dummy.MODID, "textures/gui/test_dummy_curios.png");
    private MobType mobType;
    private MobEffectAutoCompleteEditBox mobEffectAutoCompleteEditBox;
    private AttributeAutoCompleteEditBox attributeAutoCompleteBox;
    private CustomFocusButton applyAttributeButton;
    private EditBox attributeInputField;
    private CompoundTag attributesMap = new CompoundTag();
    public TestDummyEntityScreen(TestDummyEntityMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, Component.literal("测试"));
        this.imageWidth = 200; // GUI宽度
        this.imageHeight = 200; // GUI高度

    }
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        if (ModList.get().isLoaded("curios")) {
            ICuriosItemHandler handler = this.menu.getTargetEntity().getCapability(CuriosCapability.INVENTORY).resolve().orElse(null);
            if (handler == null) return;
            int number = handler.getSlots() / 9;
            for (int j = 0; j < number + 1; j++) {
                guiGraphics.blit(CURIOS_TEX, this.leftPos - 13 - j * 18, this.topPos,0,
                        0, 0,
                        30, 200,
                        200, 200);
            }

        }
        armorRender(guiGraphics);
        curiosRender(guiGraphics);
    }
    private ResourceLocation getIconByIndex(int index) {
        return switch (index) {
            case 0 -> new ResourceLocation("minecraft", "textures/item/empty_armor_slot_helmet.png");
            case 1 -> new ResourceLocation("minecraft", "textures/item/empty_armor_slot_chestplate.png");
            case 2 -> new ResourceLocation("minecraft", "textures/item/empty_armor_slot_leggings.png");
            case 3 -> new ResourceLocation("minecraft", "textures/item/empty_armor_slot_boots.png");
            case 5 -> new ResourceLocation("minecraft", "textures/item/empty_slot_sword.png");
            case 4 -> new ResourceLocation("minecraft", "textures/item/empty_armor_slot_shield.png");
            default -> null; // 或者抛出异常、返回默认值等
        };
    }
    private void armorRender(GuiGraphics guiGraphics) {


        int startX = this.leftPos + 21;
        int startY = this.topPos + 8;
        int x = startX;
        TestDummyEntity entity = this.menu.getTargetEntity();

        ArrayList<ItemStack> slots = new ArrayList<>();
        entity.getAllSlots().forEach(slots::add);
        Collections.reverse(slots);
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).isEmpty()) {

            }
            if (i < 4) {
                if (slots.get(i).isEmpty()) {
                    ResourceLocation icon = this.getIconByIndex(i);
                    guiGraphics.blit(icon, x, startY,
                            0, 0,
                            16, 16,
                            16, 16);
                };
            } else {
                int iFix = i;
                switch (iFix) {
                    case 4:iFix = 5;
                    break;
                    case 5:iFix = 4;
                }
                if (slots.get(iFix).isEmpty()) {
                    ResourceLocation icon = this.getIconByIndex(iFix);
                    guiGraphics.blit(icon, x, startY,
                            0, 0,
                            16, 16,
                            16, 16);
                };
            }
            x += 18;

        }


    }
    private void curiosRender(GuiGraphics guiGraphics) {
        if (!ModList.get().isLoaded("curios")) return;
        final ResourceLocation CURIO_INVENTORY = new ResourceLocation(CuriosApi.MODID,
                "textures/gui/inventory_revamp.png");
        // 假设你已经有了获取目标实体的方法，并且这个实体支持Curios能力
        ICuriosItemHandler handler = this.menu.getTargetEntity().getCapability(CuriosCapability.INVENTORY).resolve().orElse(null);
        if (handler == null) return;

        int startX = this.leftPos - 6;  // 饰品栏起始 X 坐标
        int startY = this.topPos + 7;  // 饰品栏起始 Y 坐标
        int slotSize = 18;
        int gap = 0;

        int row = 0;
        int col = 0;
        int index = 0;

        for (int i = 0; i < handler.getSlots(); i++) {
            if (i != 0 && i % 10 == 0) {
                col += 1;
                row = 0;
            }
            String identifier = this.menu.getCuriosContainer().getIdentifier(i);

            //int identifierIndex =  this.menu.getCuriosContainer().getIdentifierIndex(identifier,index);

            ItemStack item = handler.getEquippedCurios().getStackInSlot(index);
            ResourceLocation icon = CuriosApi.getSlot(identifier).get().getIcon();
            ResourceLocation newIcon = new ResourceLocation(icon.getNamespace(), "textures/"+icon.getPath()+".png");
            RenderSystem.setShaderTexture(0, newIcon);
            RenderSystem.setShaderTexture(0, CURIOS_TEX);
            int x = startX - col * slotSize;
            int y = startY + row * (slotSize + gap);
            guiGraphics.blit(CURIOS_TEX, x,y,0,32, 0, 18, 18,200,200);
            if (item.isEmpty()) {
                guiGraphics.blit(newIcon, x, y,
                        0, 0,
                        18, 18,
                        18, 18);
            }
            row++;
            index++;

        }



    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);


        int baseY = this.topPos + 20;
        int yOffset = 40;
        /*
        guiGraphics.drawString(this.font, Component.literal("护甲值"),
                this.leftPos + 30, baseY, 0x000000, false);
        guiGraphics.drawString(this.font, Component.literal("盔甲韧性"),
                this.leftPos + 80, baseY, 0x000000, false);

         */
        guiGraphics.drawString(this.font, Component.translatable("button.powerful_dummy.mobType"),
                this.leftPos + 20, baseY + yOffset - 3 , 0x000000, false);
        guiGraphics.drawString(this.font, Component.translatable("button.powerful_dummy.attribute"),
                this.leftPos + 20, baseY + 8 , 0x000000, false);
        guiGraphics.drawString(this.font, Component.translatable("button.powerful_dummy.value"),
                this.leftPos + 65, baseY + 37 , 0x000000, false);
        /*
        guiGraphics.drawString(this.font, Component.translatable("button.powerful_dummy.change_mod_tip"),
                this.leftPos + 59, baseY + yOffset - 3, 0x000000, false);
         */
        renderDisplayAttributes(guiGraphics,this.menu.getTargetEntity());


    }
    private void renderDisplayAttributes(GuiGraphics guiGraphics, TestDummyEntity entity) {
        ResourceLocation dataId =
                new ResourceLocation(Powerful_dummy.MODID,"attribute_display");
        AttributeData data = AttributeLoader.getData(dataId);
        if (data != null && entity != null) {
            int x = this.leftPos + 95;
            int y = this.topPos + 24;
            int offset = 3;
            for (AttributeData.AttributeEntry entry : data.attributes) {
                Attribute attribute = entry.getAttribute();
                int displayColor = entry.getDisplayColor();
                int valueColor = entry.getValueColor();
                if (attribute != null) {
                    double value = entity.getAttributeValue(attribute);
                    String attributeText;
                    String attributeValue;
                    attributeText = Component.translatable(attribute.getDescriptionId()).getString()+":";
                    if (entry.usePercent) {
                        attributeValue = String.format("%.2f", value * 100);
                    } else {
                        attributeValue = String.format("%.2f", value);
                    }
                    guiGraphics.drawString(this.font, attributeText,
                            x, y + offset, displayColor, false);
                    offset += 1;
                    y += this.font.lineHeight;
                    guiGraphics.drawString(this.font, attributeValue,
                            x, y + offset, valueColor, false);
                    y += this.font.lineHeight;
                    offset += 4;
                }
            }
        }
    }

    private void putEditBoxes () {
        int Xpos = this.leftPos + 20;

        TestDummyEntity entity = this.menu.getTargetEntity();
        attributeAutoCompleteBox = new AttributeAutoCompleteEditBox(this.font,
                Xpos, this.topPos + 38 , 72, 16,
                Component.literal("Attributes"),
                entity
                );
        attributeAutoCompleteBox.loadSuggestions();

        attributeAutoCompleteBox.setResponder(newText -> {
            try {
                Attribute atb = EditBoxInfoHelper.getInstance().getAttributeByDescriptionName(newText);
                attributeInputField.setValue(String.valueOf(this.menu.getTargetEntity().getAttributeBaseValue(atb)));
            } catch (Exception e) {
                System.err.println("非法属性");
            }
        });
        attributeInputField = new EditBox(this.font,
                Xpos + 44, this.topPos + 67, 28, 18,
                Component.literal("AttributeValue"));
        attributeInputField.moveCursorToStart();
        attributeInputField.setMaxLength(10);

        attributeInputField.setResponder(newText -> {
            try {

                double newValue = Double.parseDouble(newText);
                //CompoundTag attributeTag = new CompoundTag();
                String attributeName = attributeAutoCompleteBox.getValue();
                ResourceLocation attributeKey = EditBoxInfoHelper.getInstance().getAttributeResourceLocationByName(attributeName);
                if (attributeKey != null ) {
                    attributesMap.putDouble(attributeKey.toString(), newValue);
                }
            } catch (NumberFormatException e) {
                System.err.println("无法将输入转换为数字");
            }
        });
        this.addRenderableWidget(attributeInputField);
        this.addRenderableWidget(attributeAutoCompleteBox);
    }
    @Override
    protected void init() {
        super.init();
        putEditBoxes();
        int Xpos = this.leftPos + 20;
        this.inventoryLabelY = Integer.MIN_VALUE;
        this.titleLabelY = Integer.MIN_VALUE;

        TestDummyEntity entity = this.menu.getTargetEntity();
        MobType initialType = entity.getMobType();
        this.mobType = initialType;
        Button changeTypeButton = Button.builder(
                        MobTypeHelper.getDisplayName(initialType),
                        button -> {
                            TestDummyEntity target = this.menu.getTargetEntity();
                            if (target == null) return;
                            MobType next = MobTypeHelper.getNextMobType(this.mobType);
                            button.setMessage(MobTypeHelper.getDisplayName(next));
                            this.mobType = next;
                        })
                .pos(Xpos, this.topPos + 30 +36 )
                .size(40, 20)
                .build();
        addRenderableWidget(changeTypeButton);
        this.applyAttributeButton = new CustomFocusButton.Builder(
                        Component.translatable("button.powerful_dummy.apply"),
                        button -> sendData()
                        ,attributeAutoCompleteBox)
                .pos(Xpos,this.topPos + 93 )
                .size(73, 16)
                .build();
        addRenderableWidget(applyAttributeButton);

        // 切换模式按钮
        /*
        CustomFocusButton changeButton = CustomFocusButton.builder(
                        Component.translatable("button.powerful_dummy.change_mode"),
                        button -> {

                        },
                        attributeAutoCompleteBox)
                .pos(Xpos + 39, this.topPos + 30 +36 )
                .size(34, 20)
                .build();
        addRenderableWidget(changeButton);

        addRenderableWidget(Button.builder(
                        Component.translatable("button.powerful_dummy.close"),
                        button -> {
                            if (minecraft != null) {
                                minecraft.setScreen(null);
                            }
                        })
                .pos(this.leftPos+130, this.topPos + 90 )
                .size(40, 20)
                .build());

         */
    }



    @Override
    public void removed() {

        sendData();
        super.removed();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return super.shouldCloseOnEsc();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (mobEffectAutoCompleteEditBox != null && mobEffectAutoCompleteEditBox.handleKeyPressed(keyCode,scanCode,modifiers)) {
            return true;
        }
        if (attributeAutoCompleteBox != null &&attributeAutoCompleteBox.handleKeyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (mobEffectAutoCompleteEditBox != null && mobEffectAutoCompleteEditBox.handleMouseClick(mouseX, mouseY)) {
            return false;
        }
        if (attributeAutoCompleteBox != null && attributeAutoCompleteBox.handleMouseClick(mouseX, mouseY)) {
            attributeAutoCompleteBox.setFocused(false);
            return false;
        }
        if (attributeAutoCompleteBox != null && attributeAutoCompleteBox.handleMouseClick(mouseX, mouseY)) {
            attributeAutoCompleteBox.setFocused(false);
        }
        attributeAutoCompleteBox.setFocused(false);
        attributeInputField.setFocused(false);
        applyAttributeButton.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mobEffectAutoCompleteEditBox != null && mobEffectAutoCompleteEditBox.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        if (attributeAutoCompleteBox != null && attributeAutoCompleteBox.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    private void sendData () {

        NetWorkHandler.CHANNEL.sendToServer(new DummyInfoPacket(
                this.menu.getTargetEntity().getId(),
                MobTypeHelper.toId(this.mobType),
                attributesMap
        ));
    }

}


