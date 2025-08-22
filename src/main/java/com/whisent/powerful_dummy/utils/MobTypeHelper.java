package com.whisent.powerful_dummy.utils;


import net.minecraft.network.chat.Component;

import java.util.*;

public class MobTypeHelper {
    public enum MobTypeEnum {
        UNDEFINED,
        UNDEAD,
        ARTHROPOD,
        ILLAGER,
        WATER
    }
    private static final Map<Integer, MobTypeEnum> ID_TO_TYPE = new LinkedHashMap<>();
    private static final Map<MobTypeEnum, Integer> TYPE_TO_ID = new HashMap<>();

    private static final Map<MobTypeEnum, String> DISPLAY_NAMES = new HashMap<>();
    static {
        for (int i = 0; i < 5; i++) {
            ID_TO_TYPE.put(i, getAllMobTypes().get(i));
            TYPE_TO_ID.put(getAllMobTypes().get(i), i);
        }
        DISPLAY_NAMES.put(MobTypeEnum.UNDEFINED, "button.powerful_dummy.undefined");
        DISPLAY_NAMES.put(MobTypeEnum.UNDEAD, "button.powerful_dummy.undead");
        DISPLAY_NAMES.put(MobTypeEnum.ARTHROPOD, "button.powerful_dummy.arthropod");
        DISPLAY_NAMES.put(MobTypeEnum.ILLAGER, "button.powerful_dummy.illager");
        DISPLAY_NAMES.put(MobTypeEnum.WATER, "button.powerful_dummy.water");
    }

    public static MobTypeEnum fromId(int id) {
        return ID_TO_TYPE.getOrDefault(id, MobTypeEnum.UNDEFINED);
    }

    // 获取编号
    public static int toId(MobTypeEnum type) {
        return TYPE_TO_ID.getOrDefault(type, 0);
    }

    // 获取所有 MobType 可迭代列表
    public static List<MobTypeEnum> getAllMobTypes() {
        return Arrays.stream(MobTypeEnum.values()).toList();
    }

    // 获取 MobType 的显示名称（Component）
    public static Component getDisplayName(MobTypeEnum type) {
        return Component.translatable(getDisplayNameString(type));
    }

    // 获取 MobType 的显示名称（字符串）
    public static String getDisplayNameString(MobTypeEnum type) {
        return DISPLAY_NAMES.getOrDefault(type, "未知类型");
    }

    // 获取下一个 MobType（用于循环切换）
    public static MobTypeEnum getNextMobType(MobTypeEnum current) {
        List<MobTypeEnum> all = getAllMobTypes();
        int index = all.indexOf(current);
        if (index == -1 || index == all.size() - 1) {
            return all.get(0);
        } else {
            return all.get(index + 1);
        }
    }

}
