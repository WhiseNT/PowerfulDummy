package com.whisent.powerful_dummy.client;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record ClientDummyData(double damage, double dps, double totalDamage, int combo, int color) {
}
