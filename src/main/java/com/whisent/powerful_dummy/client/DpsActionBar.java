package com.whisent.powerful_dummy.client;

import com.mojang.blaze3d.platform.Window;
import com.whisent.powerful_dummy.Powerful_dummy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(modid = Powerful_dummy.MODID, value = Dist.CLIENT)
public class DpsActionBar {
    // 使用AtomicReference确保线程安全
    private static final AtomicReference<ClientDummyData> currentData = new AtomicReference<>();
    private static final DamageHistory damageHistory = new DamageHistory();
    private static volatile int ticksVisible = 0;
    private static final int FADE_TICKS = 10;
    private static final int STAY_TICKS = 30;
    private static final int MAX_VISIBLE_TICKS = FADE_TICKS + STAY_TICKS + FADE_TICKS;
    public static void displayText(double damage, double dps, double totalDamage, int combo, int color) {
        currentData.set(new ClientDummyData(damage, dps, totalDamage, combo, color));
        ticksVisible = 0;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && currentData.get() != null) {
            if (++ticksVisible >= MAX_VISIBLE_TICKS) {
                currentData.set(null);
                damageHistory.clean();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        ClientDummyData data = currentData.get();
        if (data == null) return;

        float alpha = calculateAlpha();
        if (alpha <= 0) {
            currentData.set(null);
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics gui = event.getGuiGraphics();
        Window window = mc.getWindow();

        int x = window.getGuiScaledWidth() / 2 + 40;
        int y = window.getGuiScaledHeight() / 2 - window.getGuiScaledHeight() / 10 - 10;

        renderDamageInfo(gui, mc, x, y, data, alpha);
    }

    private static float calculateAlpha() {
        return 1.0f;
    }

    private static void renderDamageInfo(GuiGraphics gui, Minecraft mc, int x, int y,
                                         ClientDummyData data, float alpha) {
        int baseColor = data.color() | ((int)(alpha * 255) << 24);

        // 计算文字总高度
        int lineHeight = mc.font.lineHeight;
        int totalHeight = lineHeight * 3 + 6;

        String dpsValue = String.format("%.1f", data.dps());
        String totalDamageValue = String.format("%.1f", data.totalDamage());
        String damagePerHitValue = String.format("%.1f", data.damage());
        // 渲染标签文本
        drawScaledString(gui, mc.font,
                Component.translatable("gui.powerful_dummy.stats.dps")
                        .append(" ")
                        .append(dpsValue).getString(),
                x, y, 0xFFFFFF, 2f);
        gui.drawString(mc.font,
                Component.translatable("gui.powerful_dummy.stats.total_damage")
                        .append(" ")
                        .append(totalDamageValue)
                        .getString(),
                x, y + 24, 0xFFFFFF, true);
        gui.drawString(mc.font, Component.translatable("gui.powerful_dummy.stats.damage_per_hit")
                        .append(" ")
                        .append(damagePerHitValue)
                        .getString(),
                x, y + 36, baseColor, true);


        // 绘制连击计数
        if (data.combo() >= 1) {
            String comboText = data.combo() + " HITS!";
            int textWidth = mc.font.width(comboText);
            gui.drawString(mc.font, comboText,
                    x ,
                    y - lineHeight - 6,
                    0xFFFF00 | ((int)(alpha * 255) << 24),
                    true);
        }
    }

    private static void drawScaledString(GuiGraphics gui, Font font, String text, int x, int y, int color, float scale) {
        gui.pose().pushPose();
        gui.pose().scale(scale, scale, 1.0f);
        gui.drawString(font, text,
                x/scale , (y)/scale,color, true);
        gui.pose().popPose();
    }

    public static DamageHistory getDamageHistory() {
        return damageHistory;
    }
}
