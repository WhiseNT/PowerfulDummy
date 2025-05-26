package com.whisent.test_dummy.dps;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class DpsData {
    private double totalDamage = 0.0;
    private double lastDamage = 0.0f;
    private DamageSource ds;
    private long startTime = System.currentTimeMillis();
    private long lastAttackTime = System.currentTimeMillis();
    private static final long REST_TIME = 1_000;

    public void addDamage(double damage,DamageSource damageSource) {
        totalDamage += damage;
        lastDamage = damage;
        this.ds = damageSource;
        lastAttackTime = System.currentTimeMillis();
    }
    public float getLastDamage() {
        return (float) lastDamage;
    }
    public float getDps() {
        long now = System.currentTimeMillis();
        long elapsedTime = now - startTime;

        if (elapsedTime <= 0) return 0.0f;
        if (now - startTime < 1000) return (float) totalDamage;
        return (float) (totalDamage / (elapsedTime / 1000.0));
    }
    public float getTotalDamage() {
        return (float) totalDamage;
    }
    public boolean needsReset() {
        long now = System.currentTimeMillis();
        return now - lastAttackTime > REST_TIME;
    }
    public boolean hasDamageTag(TagKey<DamageType> tag) {
        return this.ds.is(tag);
    }

    public DamageSource getDamageSource() {
        return ds;
    }

    public void reset() {
        totalDamage = 0.0;
        startTime = System.currentTimeMillis();
        lastAttackTime = startTime;
    }
}
