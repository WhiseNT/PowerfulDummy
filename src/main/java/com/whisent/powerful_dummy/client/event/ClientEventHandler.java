package com.whisent.powerful_dummy.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.entity.DummyEntityRegistry;
import com.whisent.powerful_dummy.entity.client.TestDummyModel;
import com.whisent.powerful_dummy.entity.client.TestDummyRenderer;
import com.whisent.powerful_dummy.gui.MenuRegistry;
import com.whisent.powerful_dummy.gui.TestDummyEntityScreen;
import com.whisent.powerful_dummy.network.ClearDpsDataPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;


@EventBusSubscriber(modid = Powerful_dummy.MODID,value = { Dist.CLIENT })
public class ClientEventHandler {

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (CLEAR_DPS_DATA.consumeClick()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                byte clear = 1;
                PacketDistributor.sendToServer(new ClearDpsDataPacket(clear));
            }
        }
    }
    public static final KeyMapping CLEAR_DPS_DATA =
            new KeyMapping("key.powerful_dummy.cleardps",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_GRAVE_ACCENT,
                    "key.categories.powerful_dummy");
}