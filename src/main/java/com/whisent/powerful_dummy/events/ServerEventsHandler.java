package com.whisent.powerful_dummy.events;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.dps.DpsTracker;
import com.whisent.powerful_dummy.entity.TestDummyEntity;
import com.whisent.powerful_dummy.impl.IActionBarDisplay;
import com.whisent.powerful_dummy.utils.Debugger;
import com.whisent.powerful_dummy.utils.DummyEventUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;


@EventBusSubscriber(modid = Powerful_dummy.MODID)
public class ServerEventsHandler {

    private static final int TICK_INTERVAL = 40;
    private static int tickCounter = 0;
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        tickCounter++;
        if (tickCounter % TICK_INTERVAL == 0) {
            DummyEventUtils.updateDpsMessages(event.getEntity().level());
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
    @SubscribeEvent
    public static void onDummyDamage(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof TestDummyEntity dummy) {
            DamageSource source = event.getSource();
            float damage = event.getNewDamage();
            if (!event.getEntity().level().isClientSide()) {
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
                    ((IActionBarDisplay)player).powerfulDummy$sendDamage(damage, false);
                }
            }
        }

    }

}
