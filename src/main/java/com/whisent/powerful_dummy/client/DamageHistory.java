package com.whisent.powerful_dummy.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayDeque;
import java.util.Queue;

public class DamageHistory {
    private final Queue<Double> damageQueue = new ArrayDeque<>();
    private static final int MAX_SIZE = 5;
    public void addDamage(double damage) {
        damageQueue.add(damage);
        if (damageQueue.size() > MAX_SIZE ) {
            damageQueue.poll();
        }
    }

    public Queue<Double> getDamageHistory() {
        return new ArrayDeque<>(damageQueue);
    }


    public void clean() {
        damageQueue.clear();
    }
}
