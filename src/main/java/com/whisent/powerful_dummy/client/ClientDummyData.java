package com.whisent.powerful_dummy.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record ClientDummyData(double damage, double dps, double totalDamage, int combo, int color) {
}
