package com.whisent.powerful_dummy.utils;

import net.minecraft.network.chat.Component;

public class TimeUtils {
    public static Component formatRelativeTime(long timestamp) {
        long current = System.currentTimeMillis();
        long diff = current - timestamp;
        double seconds = diff / 1000.0;

        if (seconds < 1) {
            return Component.translatable("chat.powerful_dummy.damagelog.time.justnow");
        } else {
            return Component.translatable("chat.powerful_dummy.damagelog.time.secondsago", String.format("%.1f", seconds));
        }
    }
}
