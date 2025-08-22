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

    public TestDummyCuriosContainer(TestDummyEntity entity) {
        super(entity.getCapability(CuriosCapability.INVENTORY).getSlots());
        this.entity = entity;
        this.curiosItemHandler = getCuriosHandler();
        init ();
    }
    private void init () {
        int index = 0;
        for (var entry : curiosItemHandler.getCurios().values()) {
            int identifierAmount = curiosItemHandler.getCurios().get(entry.getIdentifier()).getSlots();
            for (int i = 0; i < identifierAmount; i++) {
                identifierList.add(entry.getIdentifier());
                identifierMap.putIfAbsent(entry.getIdentifier(), new ArrayList<>());
                identifierMap.get(entry.getIdentifier()).add(index);
                index++;
            }

        }
        //System.out.println(identifierMap);
        var inv = entity.getCapability(CuriosCapability.INVENTORY);
        for (int i = 0; i < this.curiosItemHandler.getCurios().size(); i++) {
            String identifier = identifierList.get(i);
            setItem(i,inv.getCurios().get(identifier).getStacks()
                    .getStackInSlot(getIdentifierIndex(identifier,i)));

        }

    }
    @Override
    public void setItem(int index, ItemStack itemStack) {
        super.setItem(index, itemStack);
        String itemIdentifier = identifierList.get(index);
        //System.out.println(itemStack);
        if (!itemStack.isEmpty()) {
            if (curiosItemHandler.getCurios().get(itemIdentifier).getStacks()
                    .getStackInSlot(getIdentifierIndex(itemIdentifier,index)).isEmpty()) {
                curiosItemHandler.getCurios().get(itemIdentifier).getStacks()
                        .insertItem(getIdentifierIndex(itemIdentifier,index),itemStack,false);
            }

            }else {
            curiosItemHandler.getCurios().get(itemIdentifier).getStacks()
                    .insertItem(getIdentifierIndex(itemIdentifier,index),ItemStack.EMPTY,false);
        }

        /*
        if (!itemStack.isEmpty()) {
            curiosItemHandler.getCurios().values().stream().filter(i -> i.getIdentifier().equals(itemIdentifier)).findFirst().ifPresent(i -> {
                if (i.getStacks().getStackInSlot(getIdentifierIndex(i.getIdentifier(),index)).isEmpty()) {
                    i.getStacks().insertItem(getIdentifierIndex(i.getIdentifier(),index),itemStack,false);
                }
            });
        } else {
            curiosItemHandler.getCurios().values().stream().filter(i -> i.getIdentifier().equals(itemIdentifier)).findFirst().ifPresent(i -> {
                i.getStacks().insertItem(getIdentifierIndex(i.getIdentifier(),index), ItemStack.EMPTY,false);
            });
        }
         */

    }
    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = super.removeItem(index, count);
        //this.setItem(index,getItem(index));

        return stack;
    }

    public ArrayList<String> getIdentifierList() {
        return identifierList;
    }

    public String getIdentifier (int index) {
        if (index >= identifierList.size()) {
            return null;
        }
        return identifierList.get(index);
    }
    public Integer getIdentifierIndex (String identifier,int index) {
        return identifierMap.get(identifier).indexOf(index);
    }

    private ICuriosItemHandler getCuriosHandler() {
        return entity.getCapability(CuriosCapability.INVENTORY);
    }



    // 辅助方法：获取 Curios Handler
    public ICuriosItemHandler getCuriosHandlerRaw() {
        return curiosItemHandler;
    }
}
