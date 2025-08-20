package com.whisent.powerful_dummy.dps;

import net.minecraft.world.damagesource.DamageSource;

public class DamageData {
    private final double amount;
    private final DamageSource damageSource;
    private final double currentDps;
    private final long timestamp;

    public DamageData(double amount, DamageSource damageSource,double currentDps) {
        this.amount = amount;
        this.damageSource = damageSource;
        this.currentDps =  currentDps;
        this.timestamp = System.currentTimeMillis();
    }

    public double getAmount() {
        return amount;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public double getCurrentDps() {
        return currentDps;
    }
    public long getTimestamp() {
        return timestamp;
    }
}
