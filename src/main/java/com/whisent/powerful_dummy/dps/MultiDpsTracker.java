package com.whisent.powerful_dummy.dps;


import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;


public class MultiDpsTracker {
    private static final Map<UUID, Map<UUID, DpsData>> MULTIPLE_ENTITY_DPS_MAP = new WeakHashMap<>();

    public static void onEntityDamage(Entity source, Entity target, double damage, DamageSource damageSource) {
        if (source instanceof Player) {
            UUID playerId = source.getUUID();
            UUID targetId = target.getUUID();

            // 更新玩家对目标实体的伤害数据
            Map<UUID, DpsData> playerDpsMap = MULTIPLE_ENTITY_DPS_MAP.computeIfAbsent(playerId, k -> new WeakHashMap<>());
            DpsData data = playerDpsMap.computeIfAbsent(targetId, id -> new DpsData());
            data.addDamage(damage,damageSource);
        }
    }

    public static float getPlayerDps(UUID playerId, Entity entity) {
        UUID entityId = entity.getUUID();
        Map<UUID, DpsData> playerDpsMap = MULTIPLE_ENTITY_DPS_MAP.get(playerId);
        if (playerDpsMap != null) {
            DpsData data = playerDpsMap.get(entityId);
            return data == null ? 0.0f : data.getDps();
        }
        return 0.0f;
    }

    public static DpsData getPlayerDpsData(UUID playerId, Entity entity) {
        UUID entityId = entity.getUUID();
        Map<UUID, DpsData> playerDpsMap = MULTIPLE_ENTITY_DPS_MAP.get(playerId);
        if (playerDpsMap != null) {
            DpsData data = playerDpsMap.get(entityId);
            return data == null ? new DpsData() : data;
        }
        return new DpsData();
    }

    public static void checkAndResetDps() {
        for (Map<UUID, DpsData> playerDpsMap : MULTIPLE_ENTITY_DPS_MAP.values()) {
            for (DpsData data : playerDpsMap.values()) {
                if (data.needsReset()) {
                    data.reset();
                }
            }
        }
    }
}