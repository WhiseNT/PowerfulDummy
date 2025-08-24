package com.whisent.powerful_dummy.gui.container;

import com.whisent.powerful_dummy.entity.TestDummyEntity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class TestDummyCuriosContainer extends SimpleContainer {
    private final TestDummyEntity entity;
    private final ICuriosItemHandler curiosItemHandler;
    private final ArrayList<String> identifierList = new ArrayList<>();
    private final HashMap<String, ArrayList<Integer>> identifierMap = new HashMap<>();
    private final HashMap<Integer, Integer> slotIndexToIdentifierIndexMap = new HashMap<>();

    public TestDummyCuriosContainer(TestDummyEntity entity) {
        super(entity.getCapability(CuriosCapability.INVENTORY).getSlots());
        this.entity = entity;
        this.curiosItemHandler = getCuriosHandler();
        init();
    }

    private void init() {
        int index = 0;
        var curiosMap = curiosItemHandler.getCurios();
        if (curiosMap == null) return;

        for (var entry : curiosMap.values()) {
            String identifier = entry.getIdentifier();
            int identifierAmount = entry.getSlots();
            identifierMap.putIfAbsent(identifier, new ArrayList<>());
            for (int i = 0; i < identifierAmount; i++) {
                identifierList.add(identifier);
                identifierMap.get(identifier).add(index);
                slotIndexToIdentifierIndexMap.put(index, i);
                index++;
            }
        }

        var inv = entity.getCapability(CuriosCapability.INVENTORY);
        if (inv == null) return;

        for (int i = 0; i < this.getContainerSize(); i++) {
            String identifier = identifierList.get(i);
            var stacks = inv.getCurios().get(identifier);
            if (stacks != null) {
                int identifierIndex = slotIndexToIdentifierIndexMap.get(i);
                setItem(i, stacks.getStacks().getStackInSlot(identifierIndex));
            }
        }
    }

    @Override
    public void setItem(int index, ItemStack itemStack) {
        super.setItem(index, itemStack);
        if (index >= identifierList.size()) return;

        String itemIdentifier = identifierList.get(index);
        var curiosMap = curiosItemHandler.getCurios();
        if (curiosMap == null) return;

        var curioStacks = curiosMap.get(itemIdentifier);
        if (curioStacks == null) return;

        int identifierIndex = slotIndexToIdentifierIndexMap.get(index);
        var stacks = curioStacks.getStacks();

        if (!itemStack.isEmpty()) {
            if (stacks.getStackInSlot(identifierIndex).isEmpty()) {
                stacks.insertItem(identifierIndex, itemStack, false);
            }
        } else {
            stacks.insertItem(identifierIndex, ItemStack.EMPTY, false);
        }
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = super.removeItem(index, count);
        return stack;
    }

    public ArrayList<String> getIdentifierList() {
        return identifierList;
    }

    public String getIdentifier(int index) {
        if (index >= identifierList.size()) {
            return null;
        }
        return identifierList.get(index);
    }

    public Integer getIdentifierIndex(String identifier, int slotIndex) {
        var list = identifierMap.get(identifier);
        if (list == null) return -1;
        int identifierIndex = list.indexOf(slotIndex);
        return identifierIndex == -1 ? -1 : identifierIndex;
    }

    private ICuriosItemHandler getCuriosHandler() {
        return entity.getCapability(CuriosCapability.INVENTORY);
    }

    public ICuriosItemHandler getCuriosHandlerRaw() {
        return curiosItemHandler;
    }
}