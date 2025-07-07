package com.whisent.powerful_dummy.client;

import com.whisent.powerful_dummy.Powerful_dummy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Powerful_dummy.MODID)
public class DpsActionBar {

    private static Component currentText = null;
    private static int ticksVisible = 0;
    private static final int FADE_TICKS = 10; // 淡入淡出时间
    private static final int STAY_TICKS = 30; // 显示时间
    private static final int MAX_VISIBLE_TICKS = FADE_TICKS + STAY_TICKS + FADE_TICKS;

    public DpsActionBar() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void sendText(Component text) {
        currentText = text;
        ticksVisible = 0;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && currentText != null) {
            ticksVisible++;
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (currentText == null) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int x = screenWidth / 2;
        int y = screenHeight / 2 + screenHeight / 10 * 2;

        float alpha;

        if (ticksVisible < FADE_TICKS + STAY_TICKS) {
            // Stay
            alpha = 1.0f;
        } else if (ticksVisible < MAX_VISIBLE_TICKS) {
            // Fade out
            alpha = 1.0f - ((ticksVisible - (FADE_TICKS + STAY_TICKS)) / (float) FADE_TICKS);
        } else {
            // Done
            currentText = null;
            return;
        }

        int color = 0xFFFFFF | ((int)(alpha * 255) << 24 & 0xFF000000);
        guiGraphics.drawString(mc.font, currentText,
                x - mc.font.width(currentText) / 2,
                y,
                color,
                true);
    }

}