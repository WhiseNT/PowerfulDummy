package com.whisent.powerful_dummy.gui.container;

import com.whisent.powerful_dummy.entity.TestDummyEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TestDummyArmorContainer extends SimpleContainer {
    private final TestDummyEntity entity;
    private final EquipmentSlot[] slots = new EquipmentSlot[]{
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
    };
    private final InteractionHand[] hands = new InteractionHand[]{
            InteractionHand.MAIN_HAND,
            InteractionHand.OFF_HAND
    };
    public TestDummyArmorContainer(TestDummyEntity entity) {
        super(6);
        this.entity = entity;

        for (int i = 0; i < 4; i++) {
            setItem(i, entity.getItemBySlot(slots[i]));
        }
        for (int i = 4; i < 6; i++) {
            setItem(i, entity.getItemInHand(hands[i-4]));
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        super.setItem(index, stack);
        if (index < slots.length) {
            if (!stack.isEmpty()) {
                entity.setItemSlot(slots[index], stack);
            } else {
                entity.setItemSlot(slots[index], ItemStack.EMPTY);
            }
        } else if (index < slots.length + hands.length) {
            if (!stack.isEmpty()) {
                entity.setItemInHand(hands[index-4], stack);
            } else {
                entity.setItemInHand(hands[index-4], ItemStack.EMPTY);
            }
        }

    }


    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        ItemStack stack = super.removeItem(index, count);
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = super.removeItemNoUpdate(index);
        return stack;
    }


}
