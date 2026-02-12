package com.whisent.powerful_dummy.entity;

import net.minecraft.network.chat.Component;

public class HealModeMagicNumber {
    public static final int AFTER_HURT = 0;
    public static final int INTERVAL = 1;
    public static final int LOW_HP = 2;

    public static Component getDisplayName(int mode) {
        switch (mode) {
            case AFTER_HURT:
                return Component.translatable("heal_mode.powerful_dummy.after_hurt");
            case INTERVAL:
                return Component.translatable("heal_mode.powerful_dummy.interval");
            case LOW_HP:
                return Component.translatable("heal_mode.powerful_dummy.low_hp");
            default:
                return Component.translatable("heal_mode.powerful_dummy.none");
        }
    }
}