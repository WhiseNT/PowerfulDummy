package com.whisent.powerful_dummy.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.network.ClearDpsDataPacket;
import com.whisent.powerful_dummy.network.NetWorkHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;


@Mod.EventBusSubscriber(modid = Powerful_dummy.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE,value = { Dist.CLIENT })
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
    @SubscribeEvent
    public static void onOverlay(RenderGuiOverlayEvent event) {
        
    }


}
