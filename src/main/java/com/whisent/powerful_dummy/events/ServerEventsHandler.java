package com.whisent.powerful_dummy.events;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.entity.TestDummyEntity;
import com.whisent.powerful_dummy.dps.DpsTracker;
import com.whisent.powerful_dummy.utils.DummyEventUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

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

}
