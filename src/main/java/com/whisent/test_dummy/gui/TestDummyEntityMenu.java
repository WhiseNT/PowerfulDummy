package com.whisent.test_dummy.gui;

import com.whisent.test_dummy.Test_dummy;
import com.whisent.test_dummy.entity.TestDummyEntity;
import com.whisent.test_dummy.gui.container.TestDummyArmorContainer;
import com.whisent.test_dummy.gui.container.TestDummyCuriosContainer;
import com.whisent.test_dummy.gui.slot.TestDummyCurioSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

public class TestDummyEntityMenu extends AbstractContainerMenu {
    private final TestDummyEntity entity;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private final TestDummyArmorContainer armorContainer;
    private TestDummyCuriosContainer curiosContainer;
    public TestDummyEntityMenu(int containerId, Inventory inv, FriendlyByteBuf data) {
        this(containerId, inv, getEntityFromBuf(data, inv.player.level()));
    }

    public TestDummyEntityMenu(int containerId, Inventory inv, @Nullable TestDummyEntity entity) {
        super(MenuRegistry.TEST_DUMMY_MENU.get(), containerId);
        this.entity = entity;
        this.armorContainer = new TestDummyArmorContainer(entity);
        if (entity != null) {
            // 添加盔甲槽位（从槽位索引 0 开始）
            addArmorSlots(entity);
            addPlayerSlots(inv);
            if (ModList.get().isLoaded("curios")){
                addCurioSlots(entity);
            };

        }

    }

    private static TestDummyEntity getEntityFromBuf(FriendlyByteBuf buf, Level level) {
        int entityId = buf.readInt();
        return (TestDummyEntity) level.getEntity(entityId);
    }
    private EquipmentSlot getEquipmentSlot(int slot) {
        return switch (slot) {
            case 0 -> EquipmentSlot.HEAD;
            case 1 -> EquipmentSlot.CHEST;
            case 2 -> EquipmentSlot.LEGS;
            case 3 -> EquipmentSlot.FEET;
            case 4 -> EquipmentSlot.MAINHAND;
            case 5 -> EquipmentSlot.OFFHAND;
            default -> null;
        };
    }
    private void addArmorSlots(TestDummyEntity entity) {
        for (int i = 0; i < 6; ++i) {
            this.addSlot(new Slot(armorContainer, i, i * 18 + 21, 8) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    if (this.index < 4) {
                        return stack.getItem() instanceof ArmorItem armorItem &&
                                armorItem.getEquipmentSlot() == getEquipmentSlot(this.index);
                    } else {
                        return true;
                    }

                }
            });
        }
    }
    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        //盔甲栏→玩家物品栏
        if (index < 6) {
            Slot slot = this.slots.get(index);
            if (slot.hasItem()) {
                ItemStack itemStack = slot.getItem();
                for (int i = 6; i < 42; i++) {
                    if (!this.slots.get(i).hasItem()) {
                        System.out.println("发现空位");
                        Slot toSlot = this.slots.get(i);
                        toSlot.set(itemStack);
                        slot.set(ItemStack.EMPTY);
                        slot.setChanged();
                        toSlot.setChanged();
                        return itemStack;
                    }
                }
            }
        } else if (index<42) {
            Slot slot = this.slots.get(index);
            if (slot.hasItem()) {
                ItemStack itemStack = slot.getItem();
                for (int i = 0; i < 6; i++) {
                    if (!this.slots.get(i).hasItem()) {
                        if (itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == getEquipmentSlot(i)) {
                            moveItemStackTo(itemStack,i,index,false);
                            slot.setChanged();
                        }
                        if (i > 3) {
                            moveItemStackTo(itemStack,i,index,false);
                            slot.setChanged();
                        }
                    }
                }
                for (int i = 42;i < this.slots.size(); ++i) {
                    if (!this.slots.get(i).hasItem()) {
                        if (this.slots.get(i).mayPlace(itemStack)) {
                            this.slots.get(i).set(itemStack);
                            this.slots.get(index).set(ItemStack.EMPTY);
                            this.slots.get(i).setChanged();
                            //moveItemStackTo(itemStack,i,index,false);
                            slot.setChanged();
                            break;
                        }
                    }
                }
            }
        } else {
            if (ModList.get().isLoaded("curios")) {
                TestDummyCurioSlot curioSlot = (TestDummyCurioSlot) this.slots.get(index);
                System.out.println(curioSlot.getItem());
                if (!curioSlot.hasItem()) {
                    return ItemStack.EMPTY;
                }
                ItemStack itemStack = curioSlot.getItem();
                for (int i = 6; i < 42; i++) {
                    Slot toSlot = this.slots.get(i);
                    if (!toSlot.hasItem()) {
                        moveItemStackTo(itemStack, i, index, false);
                        curioSlot.setChanged();
                        toSlot.setChanged();
                        return itemStack;
                    }
                }
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return entity != null && !entity.isRemoved() && playerIn.distanceTo(entity) < 64.0F;
    }

    // 可选：添加 getter 方法供 Screen 使用
    public TestDummyEntity getTargetEntity() {
        return entity;
    }

    private void addPlayerSlots(Inventory playerInventory) {
        // 主背包（3x9）位置偏移
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int PLAYER_INV_START_X = 21;
        final int PLAYER_INV_START_Y = 115;

        // 玩家主背包
        for (int row = 0; row < PLAYER_INVENTORY_ROW_COUNT; row++) {
            for (int col = 0; col < PLAYER_INVENTORY_COLUMN_COUNT; col++) {
                int x = PLAYER_INV_START_X + col * SLOT_X_SPACING;
                int y = PLAYER_INV_START_Y + row * SLOT_Y_SPACING;
                int index = col + row * 9 + HOTBAR_SLOT_COUNT;
                this.addSlot(new Slot(playerInventory, index, x, y));
            }
        }

        // 快捷栏（9格）
        final int HOTBAR_Y = PLAYER_INV_START_Y + PLAYER_INVENTORY_ROW_COUNT * SLOT_Y_SPACING + 4;
        for (int col = 0; col < HOTBAR_SLOT_COUNT; col++) {
            int x = PLAYER_INV_START_X + col * SLOT_X_SPACING;
            this.addSlot(new Slot(playerInventory, col, x, HOTBAR_Y));
        }
    }
    private void addCurioSlots(TestDummyEntity entity) {

        this.curiosContainer = new TestDummyCuriosContainer(entity);
        int slotIndex = 0;
        int startX = -5;
        int startY = 8;
        for (var entry : curiosContainer.getCuriosHandlerRaw().getCurios().entrySet()) {
            String identifier = entry.getKey();
            ICurioStacksHandler handler = entry.getValue();
            if (!handler.isVisible()) continue;
            for (int i = 0; i < handler.getSlots(); i++) {
                this.addSlot(new TestDummyCurioSlot(curiosContainer,slotIndex,startX,startY,identifier,entity));
                slotIndex++;
            }
            startY += 18; // 下一行
        }
    }
    public TestDummyCuriosContainer getCuriosContainer() {
        return curiosContainer;
    }
}

