package com.whisent.powerful_dummy.client.event;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.client.overlay.DpsOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = Powerful_dummy.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientOverlayRegistration {
    
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerBelowAll(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(Powerful_dummy.MODID, "dps_overlay"), new DpsOverlay());
    }
}