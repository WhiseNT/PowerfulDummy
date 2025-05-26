package com.whisent.test_dummy.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobType;

import java.util.*;

public class MobTypeHelper {

    private static final Map<Integer, MobType> ID_TO_TYPE = new LinkedHashMap<>();
    private static final Map<MobType, Integer> TYPE_TO_ID = new HashMap<>();

    private static final Map<MobType, String> DISPLAY_NAMES = new HashMap<>();
    static {
        for (int i = 0; i < 5; i++) {
            ID_TO_TYPE.put(i, getAllMobTypes().get(i));
            TYPE_TO_ID.put(getAllMobTypes().get(i), i);
        }
        DISPLAY_NAMES.put(MobType.UNDEFINED, "button.test_dummy.undefined");
        DISPLAY_NAMES.put(MobType.UNDEAD, "button.test_dummy.undead");
        DISPLAY_NAMES.put(MobType.ARTHROPOD, "button.test_dummy.arthropod");
        DISPLAY_NAMES.put(MobType.ILLAGER, "button.test_dummy.illager");
        DISPLAY_NAMES.put(MobType.WATER, "button.test_dummy.water");
    }

    public static MobType fromId(int id) {
        return ID_TO_TYPE.getOrDefault(id, MobType.UNDEFINED);
    }

    // 获取编号
    public static int toId(MobType type) {
        return TYPE_TO_ID.getOrDefault(type, 0);
    }

    // 获取所有 MobType 可迭代列表
    public static List<MobType> getAllMobTypes() {
        ArrayList<MobType>list = new ArrayList<>();
        list.add(MobType.UNDEFINED);
        list.add(MobType.UNDEAD);
        list.add(MobType.ILLAGER);
        list.add(MobType.WATER);
        list.add(MobType.ARTHROPOD);
        //DamageTypeTags.WITCH_RESISTANT_TO
        return list;
    }

    // 获取 MobType 的显示名称（Component）
    public static Component getDisplayName(MobType type) {
        return Component.translatable(getDisplayNameString(type));
    }

    // 获取 MobType 的显示名称（字符串）
    public static String getDisplayNameString(MobType type) {
        return DISPLAY_NAMES.getOrDefault(type, "未知类型");
    }

    // 获取下一个 MobType（用于循环切换）
    public static MobType getNextMobType(MobType current) {
        List<MobType> all = getAllMobTypes();
        int index = all.indexOf(current);
        if (index == -1 || index == all.size() - 1) {
            return all.get(0);
        } else {
            return all.get(index + 1);
        }
    }
}
