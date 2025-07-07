package com.whisent.powerful_dummy.client;


import com.whisent.powerful_dummy.Powerful_dummy;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Powerful_dummy.MODID)
public class KeyHandler {

    public static void init() {
        // 注册按键监听
        MinecraftForge.EVENT_BUS.addListener(KeyHandler::onKeyInput);
    }

    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.getKey() == GLFW.GLFW_KEY_J && event.getAction() == GLFW.GLFW_PRESS) {
            DpsActionBar.sendText(Component.literal("这是一个自定义的 ActionBar 文字"));
        }
    }
}