package com.whisent.powerful_dummy.client.event;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.network.ClearDpsDataPacket;
import com.whisent.powerful_dummy.network.NetWorkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;


@EventBusSubscriber(modid = Powerful_dummy.MODID,value = { Dist.CLIENT })
public class ClientEventHandler {

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (Powerful_dummy.ClientModEvents.CLEAR_DPS_DATA.consumeClick()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                byte clear = 1;
                NetWorkHandler.sendToServer(new ClearDpsDataPacket(clear));
            }
        }
    }


}
