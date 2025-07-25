package com.whisent.powerful_dummy.dps;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class DpsTracker {
    private static final Map<UUID, DpsData> ENTITY_DPS_MAP = new WeakHashMap<>();
    public static void onEntityDamage(DamageSource damageSource, double damage) {
        checkAndResetDps();
        Entity source = damageSource.getEntity();
        if (source instanceof Player) {
            UUID playerId = source.getUUID();
            DpsData data = ENTITY_DPS_MAP.computeIfAbsent(playerId, id -> new DpsData());
            data.addDamage(damage,damageSource);
        }
    }
    public static void onEntityDamage(DamageSource damageSource,Player player, double damage) {
        checkAndResetDps();
        UUID playerId = player.getUUID();
        DpsData data = ENTITY_DPS_MAP.computeIfAbsent(playerId, id -> new DpsData());
        data.addDamage(damage,damageSource);
    }

    public static float getDps(Entity entity) {
        DpsData data = ENTITY_DPS_MAP.get(entity.getUUID());
        return data == null ? 0.0f : data.getDps();
    }

    public static DpsData getDpsData(Entity entity) {
        DpsData data = ENTITY_DPS_MAP.get(entity.getUUID());
        return data == null ? new DpsData() : data;
    }

    public static void checkAndResetDps() {
        for (Map.Entry<UUID, DpsData> entry : ENTITY_DPS_MAP.entrySet()) {
            DpsData data = entry.getValue();
            if (data.needsReset()) {
                data.reset();
            }
        }
    }
}
