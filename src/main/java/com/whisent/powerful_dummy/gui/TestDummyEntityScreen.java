package com.whisent.powerful_dummy.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.data.AttributeData;
import com.whisent.powerful_dummy.data.AttributeLoader;
import com.whisent.powerful_dummy.entity.TestDummyEntity;
import com.whisent.powerful_dummy.gui.widget.AttributeAutoCompleteEditBox;
import com.whisent.powerful_dummy.gui.widget.CustomFocusButton;
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
import org.jetbrains.annotations.NotNull;
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
    private AttributeAutoCompleteEditBox attributeAutoCompleteBox;
    private CustomFocusButton applyAttributeButton;
    private EditBox attributeInputField;
    private final CompoundTag attributesMap = new CompoundTag();
    public TestDummyEntityScreen(TestDummyEntityMenu menu, Inventory inventory,Component title) {
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
            if (i < 4) {
                if (slots.get(i).isEmpty()) {
                    ResourceLocation icon = this.getIconByIndex(i);
                    if (icon != null) {
                        guiGraphics.blit(icon, x, startY,
                                0, 0,
                                16, 16,
                                16, 16);
                    }
                }
            } else {
                int iFix = i;
                iFix = switch (iFix) {
                    case 4 -> 5;
                    case 5 -> 4;
                    default -> iFix;
                };
                if (slots.get(iFix).isEmpty()) {
                    ResourceLocation icon = this.getIconByIndex(iFix);
                    if (icon != null) {
                        guiGraphics.blit(icon, x, startY,
                                0, 0,
                                16, 16,
                                16, 16);
                    }
                }
            }
            x += 18;

        }


    }
    private void curiosRender(GuiGraphics guiGraphics) {
        if (!ModList.get().isLoaded("curios")) return;
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
            if (CuriosApi.getSlot(identifier).isPresent()) {
                ResourceLocation icon = CuriosApi.getSlot(identifier).get().getIcon();
                if (icon != null) {
                    ResourceLocation newIcon = new ResourceLocation(icon.getNamespace(), "textures/"+icon.getPath()+".png");
                    //RenderSystem.setShaderTexture(0, newIcon);
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
                }
            }

            row++;
            index++;

        }



    }
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);


        int baseY = this.topPos + 20;
        int yOffset = 40;
        guiGraphics.drawString(this.font, Component.translatable("button.powerful_dummy.mobType"),
                this.leftPos + 20, baseY + yOffset - 3 , 0x000000, false);
        guiGraphics.drawString(this.font, Component.translatable("button.powerful_dummy.attribute"),
                this.leftPos + 20, baseY + 8 , 0x000000, false);
        guiGraphics.drawString(this.font, Component.translatable("button.powerful_dummy.value"),
                this.leftPos + 65, baseY + 37 , 0x000000, false);
        renderDisplayAttributes(guiGraphics,this.menu.getTargetEntity());


    }
    private void renderDisplayAttributes(GuiGraphics guiGraphics, TestDummyEntity entity) {
        ResourceLocation dataId = new ResourceLocation(Powerful_dummy.MODID, "attribute_display");
        AttributeData data = AttributeLoader.getData(dataId);
        if (data == null) {
            return;
        }
        if (entity == null) {
            return;
        }

        int x = this.leftPos + 95;
        int y = this.topPos + 26;
        int lineHeight = this.font.lineHeight;
        int verticalSpacing = 5; // 行间距

        for (AttributeData.AttributeEntry entry : data.attributes) {
            Attribute attribute = entry.getAttribute();
            if (attribute == null) {
                continue;
            }

            double value = entity.getAttributeValue(attribute);

            // 渲染属性名称
            String attributeText = Component.translatable(attribute.getDescriptionId()).getString();
            int maxWidth = 100;
            if (attributeText.length() > maxWidth) {
                attributeText = font.plainSubstrByWidth(attributeText, maxWidth) + "..";
            }
            guiGraphics.drawString(this.font, attributeText, x, y, entry.getDisplayColor(), false);

            // 渲染属性值（在下一行）
            String formattedValue;
            if (entry.usePercent) {
                formattedValue = String.format("%.2f%%", value * 100);
            } else {
                formattedValue = String.format("%.2f", value);
            }

            int valueColor = entry.getDisplayColor();
            guiGraphics.drawString(this.font, formattedValue, x, y + lineHeight, valueColor, false);

            // 更新Y坐标，为下一个属性预留两行空间加上间距
            y += (lineHeight * 2) + verticalSpacing;
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
        if (attributeAutoCompleteBox != null &&attributeAutoCompleteBox.handleKeyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (attributeAutoCompleteBox != null && attributeAutoCompleteBox.handleMouseClick(mouseX, mouseY)) {
            attributeAutoCompleteBox.setFocused(false);
            return false;
        }
        if (attributeAutoCompleteBox != null && attributeAutoCompleteBox.handleMouseClick(mouseX, mouseY)) {
            attributeAutoCompleteBox.setFocused(false);
        }
        if (attributeAutoCompleteBox != null) {
            attributeAutoCompleteBox.setFocused(false);
        }
        attributeInputField.setFocused(false);
        applyAttributeButton.setFocused(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
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


