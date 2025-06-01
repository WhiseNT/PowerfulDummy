package com.whisent.powerful_dummy.entity;

import com.whisent.powerful_dummy.gui.TestDummyEntityMenu;
import com.whisent.powerful_dummy.item.ItemRegistry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosCapability;

public class TestDummyEntity extends Monster {
    public MobType type;

    private final SimpleContainer inventory = new SimpleContainer(4);
    public TestDummyEntity(EntityType<? extends TestDummyEntity> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(false);
        this.xpReward = 0;
        this.setCanPickUpLoot(false);
        this.setInvulnerable(true);
        this.type = MobType.UNDEFINED;
    }
    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16.0d)
                .add(Attributes.MOVEMENT_SPEED, 0d)
                .add(Attributes.MAX_HEALTH, Double.MAX_VALUE)
                .add(Attributes.ARMOR, 0d)
                .add(Attributes.ARMOR_TOUGHNESS,0d)
                .add(Attributes.ATTACK_DAMAGE, 0d)
                .add(Attributes.FLYING_SPEED, 0d)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0d)
                .build();
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 pos, InteractionHand hand) {
        if (player.isCrouching()) {
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
                if (player.getMainHandItem().isEmpty()) {
                    NetworkHooks.openScreen((ServerPlayer)player, new SimpleMenuProvider(
                            (id,inventory,p)->new TestDummyEntityMenu(id,inventory,this),
                            Component.translatable("gui.test.title")
                    ),friendlyByteBuf -> {
                        friendlyByteBuf.writeInt(this.getId());
                    });
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.interactAt(player, pos, hand);
    }
    public void popEquipmentSlots () {
        this.getAllSlots().forEach(itemStack -> {
             if (!itemStack.isEmpty()) {
                Block.popResource(this.level(),this.blockPosition(), itemStack);
            }
        });
        if (ModList.get().isLoaded( "curios")) {
            int amount = this.getCapability(CuriosCapability.INVENTORY).resolve().get()
                    .getEquippedCurios().getSlots();
            for (int i = 0; i < amount; i++) {
                ItemStack itemStack = this.getCapability(CuriosCapability.INVENTORY).resolve().get()
                        .getEquippedCurios().getStackInSlot(i);
                if (!itemStack.isEmpty()) {
                    Block.popResource(this.level(),this.blockPosition(),itemStack);
                }
            }

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
        //this.heal(damage);
        //System.out.println("收到伤害");

        return super.hurt(source, damage);
    }


    public SimpleContainer getInventory() {
        return this.inventory;
    }
    @Override
    public MobType getMobType() {
        return type;
    }
    public void setMobType(MobType type) {
        this.type = type;
    }
    @Override
    public HumanoidArm getMainArm() {
        return null;
    }
    public void kill() {
        this.dead = true;
        this.discard();
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }
    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05D);
        }

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
    public void setYHeadRot(float p_21306_) {
        super.setYHeadRot(p_21306_);
    }

    @Override
    public void setXRot(float p_146927_) {
        super.setXRot(p_146927_);
    }

    @Override
    public void setYRot(float p_146923_) {
        super.setYRot(p_146923_);
    }

    @Override
    public @Nullable LivingEntity getLastHurtByMob() {
        return super.getLastHurtByMob();
    }
}
