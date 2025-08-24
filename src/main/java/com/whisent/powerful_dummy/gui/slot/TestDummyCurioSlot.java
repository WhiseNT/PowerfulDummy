package com.whisent.powerful_dummy.gui.slot;

import com.whisent.powerful_dummy.entity.TestDummyEntity;
import com.whisent.powerful_dummy.gui.container.TestDummyCuriosContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public class TestDummyCurioSlot extends Slot {
    private final Container container;
    private final int slotIndex;
    private final String identifier;
    private final TestDummyEntity entity;

    public TestDummyCurioSlot(Container container, int slotIndex, int x, int y, String identifier, TestDummyEntity entity) {
        super(container, slotIndex, x, y);
        this.identifier = identifier;
        this.container = container;
        this.slotIndex = slotIndex;
        this.entity = entity;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        if (CuriosApi.getCurio(itemStack).isPresent()) {
            TestDummyCuriosContainer curiosContainer = (TestDummyCuriosContainer) container;

            //能否装入空物品槽
            boolean flag = curiosContainer.getCuriosHandlerRaw().getCurios().get(identifier).getStacks().insertItem(
                    curiosContainer.getIdentifierIndex(identifier,slotIndex), itemStack, true
            ).isEmpty();
            //能否转换物品
            boolean flag2 = curiosContainer.getCuriosHandlerRaw().getCurios().get(identifier).getStacks().insertItem(
                    curiosContainer.getIdentifierIndex(identifier,slotIndex), itemStack, true
            ).equals(itemStack);

            //System.out.println("能否放入"+identifier);
            //System.out.println("能否放入"+flag2);
            if (this.hasItem()) {
                if (flag2) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (flag) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;


    }

    @Override
    public boolean mayPickup(Player p_40228_) {
        return  super.mayPickup(p_40228_);
    }
}
