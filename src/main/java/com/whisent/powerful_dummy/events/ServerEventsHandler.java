package com.whisent.powerful_dummy.events;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.entity.TestDummyEntity;
import com.whisent.powerful_dummy.dps.DpsTracker;
import com.whisent.powerful_dummy.utils.DummyEventUtils;
import net.minecraft.server.level.ServerPlayer;
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
        /*
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide() && entity instanceof TestDummyEntity) {
            //System.out.println("hurt");
            double damage = event.getAmount();
            Entity source = event.getSource().getEntity();
            if (source instanceof Player) {
                ((TestDummyEntity) entity).setLastInteractPlayer((Player) source);
                DpsTracker.onEntityDamage(event.getSource(),damage);
                DummyEventUtils.sendHurtMessage((ServerPlayer) source);;
            } else {
                Player player = ((TestDummyEntity) entity).getLastInteractPlayer();
                if (player != null) {
                    DpsTracker.onEntityDamage(event.getSource(), player,damage);
                    DummyEventUtils.sendHurtMessage((ServerPlayer) player);
                }

            }
        }
        */
    }
    @SubscribeEvent
    public static void onAttackEvent(LivingAttackEvent event) {
        Entity entity = event.getEntity();
        //System.out.println("触发攻击");
    }
    @SubscribeEvent
    public static void onHurtEvent(LivingHurtEvent event) {
        //System.out.println("受伤");
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
