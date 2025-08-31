package com.whisent.powerful_dummy.events;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.entity.TestDummyEntity;
import com.whisent.powerful_dummy.dps.DpsTracker;
import com.whisent.powerful_dummy.impl.IActionBarDisplay;
import com.whisent.powerful_dummy.utils.Debugger;
import com.whisent.powerful_dummy.utils.DummyEventUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Powerful_dummy.MODID)
public class ServerEventsHandler {
    @SubscribeEvent
    public static void onDamageEvent(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        double damage = event.getAmount();
        if (event.getEntity() instanceof TestDummyEntity dummy) {
            if (!dummy.level().isClientSide()) {
                if (source == null) return;

                String entityName = "null";
                String damageSourceMsgId = "null";

                var entity = source.getEntity();
                if (entity != null) {
                    var name = entity.getName();
                    if (name != null) {
                        entityName = name.getString();
                    }
                }

                var msgId = source.getMsgId();
                if (msgId != null) {
                    damageSourceMsgId = msgId;
                }

                Debugger.sendDebugMessage(String.format("[TestDummyEntity] Actually hurt by: %s | Damage: %f | Source: %s",
                        damageSourceMsgId, damage, entityName));

                Player player = null;
                if (entity instanceof Player) {
                    player = (Player) entity;
                    dummy.setLastInteractPlayer(player);
                    DpsTracker.onEntityDamage(source, damage);
                } else {
                    player = dummy.getLastInteractPlayer();
                    if (player != null) {
                        DpsTracker.onEntityDamage(source, player, damage);
                    }
                }

                if (player instanceof ServerPlayer serverPlayer) {
                    DummyEventUtils.sendHurtMessage(serverPlayer);
                    ((IActionBarDisplay) player).powerfulDummy$sendDamage(damage, false);
                }
            }
        }

    }
    private static final int TICK_INTERVAL = 40;
    private static int tickCounter = 0;
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;
        }
        if (tickCounter % TICK_INTERVAL == 0) {
            DummyEventUtils.updateDpsMessages(event.player.level());
            tickCounter = 0;
        }
    }
    @SubscribeEvent
    public static void onDummyDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof TestDummyEntity) {
            LivingEntity entity = event.getEntity();
            entity.setHealth(entity.getMaxHealth());
            event.setCanceled(true);

        }
    }

}
