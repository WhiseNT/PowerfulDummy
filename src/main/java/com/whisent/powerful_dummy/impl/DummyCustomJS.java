package com.whisent.powerful_dummy.impl;

import com.whisent.powerful_dummy.kjs.DummyCustomizer;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.Map;

public class DummyCustomJS implements KubeEvent {
    public void addAttributeColor(Attribute attribute, int color) {
        DummyCustomizer.addAttributeColor(attribute, color);
    }
    public void addDamageTypeColor(Holder<DamageType> damageType, int color) {
        DummyCustomizer.addDamageTypeColor(damageType, color);
    }
    public void setCuriosSlots(int curiosSlots) {
        DummyCustomizer.setCuriosSlots(curiosSlots);
    }
    public int getAttributeColor(Attribute attribute) {
        return DummyCustomizer.getAttributeColor(attribute);
    }
    public int getDamageTypeColor(Holder<DamageType> damageType) {
        return DummyCustomizer.getDamageTypeColor(damageType);
    }
}
