package com.whisent.powerful_dummy.events;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.data.AttributeLoader;
import com.whisent.powerful_dummy.data.tag.DamageTagLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Powerful_dummy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventsHandler {
    @SubscribeEvent
    public static void dataListener(AddReloadListenerEvent event){
        event.addListener(new AttributeLoader());
        event.addListener(new DamageTagLoader());
    }

}
