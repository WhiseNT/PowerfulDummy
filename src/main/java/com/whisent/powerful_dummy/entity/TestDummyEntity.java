package com.whisent.powerful_dummy.entity;

import com.whisent.powerful_dummy.gui.TestDummyEntityMenu;
import com.whisent.powerful_dummy.item.ItemRegistry;
import com.whisent.powerful_dummy.utils.Debugger;
import com.whisent.powerful_dummy.utils.MobTypeHelper;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Objects;

public class TestDummyEntity extends Mob {
    public MobTypeHelper.MobTypeEnum mobType;
    public Player lastInteractPlayer;

    private final SimpleContainer inventory = new SimpleContainer(4);
    public TestDummyEntity(EntityType<? extends TestDummyEntity> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(false);
        this.xpReward = 0;
        this.setCanPickUpLoot(false);
        this.setInvulnerable(false);
        this.setPersistenceRequired();
        this.mobType = initMobType(entityType);
        if (!this.level().isClientSide) {
            Debugger.sendDebugMessage("[TestDummyEntity] Created new dummy at position: " + blockPosition());
        }
    }
    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16.0d)
                .add(Attributes.MOVEMENT_SPEED, 0d)
                .add(Attributes.MAX_HEALTH, Long.MAX_VALUE)
                .add(Attributes.ARMOR, 0d)
                .add(Attributes.ARMOR_TOUGHNESS,0d)
                .add(Attributes.ATTACK_DAMAGE, 0d)
                .add(Attributes.FLYING_SPEED, 0d)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0d)
                .build();
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 pos, InteractionHand hand) {
        if (!this.level().isClientSide) {
            Debugger.sendDebugMessage("[TestDummyEntity] Player interaction: " + player.getName().getString() +
                    " at position: " + pos + " with hand: " + hand.name());
        }
        if (player.isCrouching() && player.getMainHandItem().isEmpty()) {
            if (!player.isCreative()){
                Block.popResource(player.level(),this.blockPosition(), new ItemStack(ItemRegistry.DUMMY_STAND.get()));
            }
            this.popEquipmentSlots();
            this.kill();
            this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);;
            this.showBreakingParticles();
            return InteractionResult.SUCCESS;
        } else {
            if (!player.level().isClientSide() && hand == InteractionHand.MAIN_HAND ) {
                Debugger.sendDebugMessage("[TestDummyEntity] Opening menu for player: " + player.getName().getString());
                lastInteractPlayer = player;
                if (player.getMainHandItem().isEmpty()) {
                    player.openMenu(new SimpleMenuProvider(
                            (id,inventory,p)->new TestDummyEntityMenu(id,inventory,this),
                            Component.translatable("gui.test.title")
                    ),friendlyByteBuf -> {
                        friendlyByteBuf.writeInt(this.getId());
                    });
                    return InteractionResult.SUCCESS;
                } else {
                    Debugger.sendDebugMessage("[TestDummyEntity] Player holding item: " +
                            player.getMainHandItem().getDisplayName().getString());
                }
            }
        }

        return super.interactAt(player, pos, hand);
    }
    private MobTypeHelper.MobTypeEnum initMobType(EntityType<? extends TestDummyEntity> entityType) {
        if (DummyEntityRegistry.TEST_DUMMY.get() == entityType) {
            return MobTypeHelper.MobTypeEnum.UNDEFINED;
        } else if (DummyEntityRegistry.TEST_DUMMY_UNDEAD.get() == entityType) {
            return MobTypeHelper.MobTypeEnum.UNDEAD;
        } else if (DummyEntityRegistry.TEST_DUMMY_WATER.get() == entityType) {
            return MobTypeHelper.MobTypeEnum.WATER;
        } else if (DummyEntityRegistry.TEST_DUMMY_ARTHROPOD.get() == entityType) {
            return MobTypeHelper.MobTypeEnum.ARTHROPOD;
        } else if (DummyEntityRegistry.TEST_DUMMY_ILLAGER.get() == entityType) {
            return MobTypeHelper.MobTypeEnum.ILLAGER;
        }
        return MobTypeHelper.MobTypeEnum.UNDEFINED;
    }
    public void popEquipmentSlots () {
        this.getAllSlots().forEach(itemStack -> {
             if (!itemStack.isEmpty()) {
                Block.popResource(this.level(),this.blockPosition(), itemStack);
                Debugger.sendDebugMessage("[TestDummyEntity] Popped item: " + itemStack.getDisplayName().getString());
            }
        });
        if (ModList.get().isLoaded( "curios")) {
            if (this.getCapability(CuriosCapability.INVENTORY) == null) return;
            int amount = Objects.requireNonNull(this.getCapability(CuriosCapability.INVENTORY))
                    .getEquippedCurios().getSlots();
            for (int i = 0; i < amount; i++) {
                ItemStack itemStack = this.getCapability(CuriosCapability.INVENTORY)
                        .getEquippedCurios().getStackInSlot(i);
                if (!itemStack.isEmpty()) {
                    Block.popResource(this.level(),this.blockPosition(),itemStack);
                    Debugger.sendDebugMessage("[TestDummyEntity] Popped curios item: " +
                            itemStack.getDisplayName().getString());
                }
            }

        }
    }

    @Override
    public void die(DamageSource p_21014_) {
        super.die(p_21014_);
        if (!this.level().isClientSide) {
            Debugger.sendDebugMessage("[TestDummyEntity] Dummy died due to: " + p_21014_.getMsgId());
        }
        if (this.level() instanceof ServerLevel && this.isRemoved()) {
            this.popEquipmentSlots();
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {

        return super.isInvulnerableTo(source);
    }
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (!this.level().isClientSide) {
            String entityName = "null";
            if (source.getEntity() != null) {
                Component nameComponent = source.getEntity().getName();
                if (nameComponent != null) {
                    entityName = nameComponent.getString();
                }
            }
            Debugger.sendDebugMessage("[TestDummyEntity] Hurt by: " + source.getMsgId() +
                    " with damage: " + damage + " - Source entity: " + entityName);
        }
        return super.hurt(source, damage);
    }

    @Override
    protected void actuallyHurt(@NotNull DamageSource source, float damage) {
        super.actuallyHurt(source, damage);


    }


    @Override
    public EntityType<?> getType() {
        return super.getType();
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }
    public MobTypeHelper.MobTypeEnum getMobType() {
        return mobType;
    }
    public void setMobType(MobTypeHelper.MobTypeEnum type) {
        if (this.mobType != type) {
            this.mobType = type;
            
            // 在服务端执行实体类型转换
            if (!this.level().isClientSide) {
                switch (type) {
                    case UNDEFINED -> switchMobType(DummyEntityRegistry.TEST_DUMMY.get());
                    case UNDEAD -> switchMobType(DummyEntityRegistry.TEST_DUMMY_UNDEAD.get());
                    case ILLAGER -> switchMobType(DummyEntityRegistry.TEST_DUMMY_ILLAGER.get());
                    case WATER -> switchMobType(DummyEntityRegistry.TEST_DUMMY_WATER.get());
                    case ARTHROPOD -> switchMobType(DummyEntityRegistry.TEST_DUMMY_ARTHROPOD.get());
                }
            }
        }
    }
    
    private void switchMobType(EntityType<? extends TestDummyEntity> type) {
        TestDummyEntity newEntity = new TestDummyEntity(type, this.level());
        newEntity.getAttributes()
                .getAttributesToSync()
                .forEach(attribute -> attribute.setBaseValue(Objects.requireNonNull(this.getAttribute(attribute.getAttribute())).getBaseValue()));
        newEntity.lastInteractPlayer = this.lastInteractPlayer;
        newEntity.lastHurt = this.lastHurt;
        newEntity.lastHurtByPlayer = this.lastHurtByPlayer;
        newEntity.setPos(this.getX(), this.getY(), this.getZ());
        newEntity.setRot(this.getYRot(), this.getXRot());
        newEntity.setYBodyRot(this.getYRot());
        newEntity.setYHeadRot(this.getYRot());
        
        // 保持mobType设置
        newEntity.mobType = this.mobType;

        CompoundTag nbt  = new CompoundTag();
        this.saveWithoutId(nbt);
        newEntity.load(nbt);
        this.discard();
        ICuriosItemHandler oldDummyCurios = CuriosApi.getCuriosInventory(this).orElse(null);
        ICuriosItemHandler newDummyCurios = CuriosApi.getCuriosInventory(newEntity).orElse(null);
        if (oldDummyCurios != null && newDummyCurios != null) {
            newDummyCurios.setCurios(oldDummyCurios.getCurios());
        }
        this.level().addFreshEntity(newEntity);
    }

    public void kill() {
        if (!this.level().isClientSide) {
            Debugger.sendDebugMessage("[TestDummyEntity] Killing dummy");
        }
        this.dead = true;
        this.discard();
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }
    private void showBreakingParticles() {
        Debugger.sendDebugMessage("[TestDummyEntity] Showing breaking particles");
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05D);
        }
    }

    int tickcounter = 0;
    @Override
    public void tick() {
        super.tick();
        if (this.getHealth() < this.getMaxHealth()) {
            this.heal(this.getMaxHealth() - this.getHealth()); // 仅在需要时恢复
        }
        tickcounter++;
        if (tickcounter % 20 == 0) {
            //System.out.println(this.getArmorSlots());
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return true;
    }
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public Player getLastInteractPlayer() {
        return lastInteractPlayer;
    }

    public void setLastInteractPlayer(Player lastInteractPlayer) {
        this.lastInteractPlayer = lastInteractPlayer;
    }

    @Override
    public AttributeMap getAttributes() {
        return super.getAttributes();
    }

    @Override
    public void setYBodyRot(float p_21309_) {
        super.setYBodyRot(p_21309_);
    }

    @Override
    public void setYHeadRot(float headRot) {
        super.setYHeadRot(headRot);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("MobType")) {
            this.mobType = MobTypeHelper.MobTypeEnum.valueOf(tag.getString("MobType"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.mobType != null) {
            tag.putString("MobType", this.mobType.name());
        }
    }
}