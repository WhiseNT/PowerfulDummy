package com.whisent.test_dummy.events;

import com.whisent.test_dummy.Test_dummy;
import com.whisent.test_dummy.data.AttributeLoader;
import com.whisent.test_dummy.data.tag.DamageTagLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Test_dummy.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventsHandler {
    @SubscribeEvent
    public static void dataListener(AddReloadListenerEvent event){
        event.addListener(new AttributeLoader());
        event.addListener(new DamageTagLoader());
    }
}
