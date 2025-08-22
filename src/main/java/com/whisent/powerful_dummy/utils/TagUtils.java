package com.whisent.powerful_dummy.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class TagUtils {

    /**
     * 检查实体类型是否属于某个 tag
     */
    public static boolean isEntityInTag(EntityType<?> entityType, TagKey<EntityType<?>> tag) {
        return entityType.is(tag);
    }

    /**
     * 获取实体类型的所有 tag
     */
    public static java.util.stream.Stream<TagKey<EntityType<?>>> getEntityTags(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE.getTags()
                .filter(tag -> tag.getSecond().contains((Holder<EntityType<?>>) entityType))
                .map(Pair::getFirst);
    }

    /**
     * 检查实体类型是否属于特定命名空间的 tag
     */
    public static boolean isEntityInNamespaceTag(EntityType<?> entityType, String namespace, String tagName) {
        TagKey<EntityType<?>> tag = TagKey.create(
                BuiltInRegistries.ENTITY_TYPE.key(),
                ResourceLocation.fromNamespaceAndPath(namespace, tagName)
        );
        return entityType.is(tag);
    }
}

