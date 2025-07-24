package com.whisent.powerful_dummy.gui;

import com.whisent.powerful_dummy.entity.TestDummyEntity;
import com.whisent.powerful_dummy.gui.container.TestDummyArmorContainer;
import com.whisent.powerful_dummy.gui.container.TestDummyCuriosContainer;
import com.whisent.powerful_dummy.gui.slot.TestDummyCurioSlot;
import com.whisent.powerful_dummy.utils.Debugger;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

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
        Debugger.sendDebugMessage("[TestDummyEntityMenu] Creating menu for entity: " +
                (entity != null ? entity.getId() : "null"));

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
        Debugger.sendDebugMessage("[TestDummyEntityMenu] Reading entity ID from buffer: " + entityId);
        Entity entity = level.getEntity(entityId);
        if (entity instanceof TestDummyEntity) {
            Debugger.sendDebugMessage("[TestDummyEntityMenu] Found TestDummyEntity: " + entityId);
            return (TestDummyEntity) entity;
        }
        Debugger.sendDebugMessage("[TestDummyEntityMenu] Entity not found or not a TestDummyEntity: " + entityId);
        return null;
    }
    private EquipmentSlot getEquipmentSlot(int slot) {
        EquipmentSlot result = switch (slot) {
            case 0 -> EquipmentSlot.HEAD;
            case 1 -> EquipmentSlot.CHEST;
            case 2 -> EquipmentSlot.LEGS;
            case 3 -> EquipmentSlot.FEET;
            case 4 -> EquipmentSlot.MAINHAND;
            case 5 -> EquipmentSlot.OFFHAND;
            default -> null;
        };
        Debugger.sendDebugMessage("[TestDummyEntityMenu] Mapping slot " + slot + " to equipment slot: " + result);
        return result;
    }
    private void addArmorSlots(TestDummyEntity entity) {
        Debugger.sendDebugMessage("[TestDummyEntityMenu] Adding armor slots for entity: " + entity.getId());

        for (int i = 0; i < 6; ++i) {
            this.addSlot(new Slot(armorContainer, i, i * 18 + 21, 8) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    boolean canPlace;
                    if (this.index < 4) {
                        canPlace = stack.getItem() instanceof ArmorItem armorItem &&
                                armorItem.getEquipmentSlot() == getEquipmentSlot(this.index);
                    } else {
                        canPlace = true;
                    }

                    Debugger.sendDebugMessage("[TestDummyEntityMenu] Slot " + this.index +
                            " can place item '" + stack.getDisplayName().getString() +
                            "'? " + canPlace);
                    return canPlace;
                }
            });
        }
        Debugger.sendDebugMessage("[TestDummyEntityMenu] Add armor slots successfully for entity: " + entity.getId());
    }
    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        Debugger.sendDebugMessage("[TestDummyEntityMenu] Quick move stack from slot: " + index);

        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemStack = slot.getItem();
            originalStack = itemStack.copy();
            Debugger.sendDebugMessage("[TestDummyEntityMenu] Moving item: " +
                    itemStack.getDisplayName().getString() +
                    " (count: " + itemStack.getCount() + ")");
            // 1. 从盔甲栏/手持栏转移 (0-5)
            if (index < 6) {
                Debugger.sendDebugMessage("[TestDummyEntityMenu] Moving from armor/hand slot");

                // 先尝试饰品栏 (42+)
                if (!moveItemStackTo(itemStack, 42, slots.size(), false)) {
                    // 再尝试玩家背包 (6-42)
                    if (!moveItemStackTo(itemStack, 6, 42, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            // 2. 从玩家背包转移 (6-41)
            else if (index < 42) {
                Debugger.sendDebugMessage("[TestDummyEntityMenu] Moving from player inventory");

                // 优先级1: 饰品栏 (42+)
                if (!moveItemStackTo(itemStack, 42, slots.size(), false)) {
                    // 优先级2: 匹配的盔甲栏 (0-3)
                    boolean movedToArmor = false;
                    if (itemStack.getItem() instanceof ArmorItem armorItem) {
                        for (int i = 0; i < 4; i++) {
                            if (armorItem.getEquipmentSlot() == getEquipmentSlot(i)) {
                                if (moveItemStackTo(itemStack, i, i+1, false)) {
                                    movedToArmor = true;
                                    break;
                                }
                            }
                        }
                    }
                    // 优先级3: 手持栏 (4-5) - 最低优先级
                    if (!movedToArmor && !itemStack.isEmpty()) {
                        if (!moveItemStackTo(itemStack, 4, 6, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
            // 3. 从饰品栏转移 (42+)
            else {
                Debugger.sendDebugMessage("[TestDummyEntityMenu] Moving from curios slot");

                // 直接尝试玩家背包 (6-42)
                if (!moveItemStackTo(itemStack, 6, 42, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack.getCount() == originalStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack);
        }

        return originalStack;
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
        Debugger.sendDebugMessage("[TestDummyEntityMenu] Adding player inventory slots");

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
        Debugger.sendDebugMessage("[TestDummyEntityMenu] Add player slots successfully for entity: " + entity.getId());
    }
    private void addCurioSlots(TestDummyEntity entity) {
        Debugger.sendDebugMessage("[TestDummyEntityMenu] Adding curios slots for entity: " + entity.getId());

        this.curiosContainer = new TestDummyCuriosContainer(entity);
        int slotIndex = 0;
        int startX = -5;
        int startY = 8;
        for (int i = 0; i < curiosContainer.getCuriosHandlerRaw().getSlots(); i++) {
            if (i != 0 && i % 10 == 0) {
                startX -= 18;
                startY = 8;
            }
            String identifier = curiosContainer.getIdentifier(i);
            ICurioStacksHandler handler =
                    curiosContainer.getCuriosHandlerRaw().getCurios().get(identifier);
            int amount = handler.getSlots();
            if (!handler.isVisible()) continue;
            for (int j = 0; j < amount; j++) {
                this.addSlot(new TestDummyCurioSlot(curiosContainer,slotIndex,startX,startY,identifier,entity));
                ;
            }
            slotIndex++;
            startY += 18;
        }
        for (var entry : curiosContainer.getCuriosHandlerRaw().getCurios().entrySet()) {
            String identifier = entry.getKey();

        }
        Debugger.sendDebugMessage("[TestDummyEntityMenu] Add curios slots successfully for entity: " + entity.getId());
    }
    public TestDummyCuriosContainer getCuriosContainer() {
        return curiosContainer;
    }
}

