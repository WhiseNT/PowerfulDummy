package com.whisent.powerful_dummy.item;

import com.whisent.powerful_dummy.entity.DummyEntityRegistry;
import com.whisent.powerful_dummy.entity.TestDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Map;

public class TestDummyItem extends Item {
    public TestDummyItem(Properties p_41383_) {
        super(p_41383_);
    }
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            BlockPos pos = context.getClickedPos();
            Vec3 spawnPos = Vec3.atBottomCenterOf(pos).add(0, 1, 0);

            AABB aabb = DummyEntityRegistry.TEST_DUMMY.get().getDimensions().makeBoundingBox(spawnPos);
            if (level.noCollision(aabb) && level.getEntities(null, aabb).isEmpty()) {

                context.getItemInHand().shrink(1);

                TestDummyEntity dummy = new TestDummyEntity(DummyEntityRegistry.TEST_DUMMY.get(), level);
                dummy.setPos(spawnPos.x(), spawnPos.y(), spawnPos.z());
                float playerYaw = context.getPlayer().getYRot();
                float alignedYaw = (float) (Math.floor((Mth.wrapDegrees(playerYaw) - 180D + 22.5D) / 45.0D) * 45.0D); // 对齐到 45° 倍数
                dummy.moveTo(dummy.getX(), dummy.getY(), dummy.getZ(), alignedYaw, 0.0F);
                dummy.setYHeadRot(alignedYaw);
                dummy.setYBodyRot(alignedYaw);

                level.addFreshEntity(dummy);
                level.playSound(null, dummy.getX(), dummy.getY(), dummy.getZ(),
                        SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
                dummy.gameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }
}
