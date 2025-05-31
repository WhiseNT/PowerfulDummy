package com.whisent.powerful_dummy.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.*;

public class EditBoxInfoHelper {
    // 单例实例
    private static final EditBoxInfoHelper INSTANCE = new EditBoxInfoHelper();
    public static EditBoxInfoHelper getInstance() {
        return INSTANCE;
    }
    // 状态变量
    private final Minecraft minecraft = Minecraft.getInstance();
    private HashMap<String, HashMap<String, Attribute>> attributesMap = new HashMap<>();
    private String lastLoadedLang;

    // 私有构造器（确保外部无法 new）
    private EditBoxInfoHelper() {
        initAttributesMap(); // 初始加载一次
    }

    // 确保数据是最新的（比如语言变化后重新加载）
    private void ensureInitialized() {
        if (attributesMap.isEmpty() || isLanguageChanged()) {
            initAttributesMap();
            lastLoadedLang = getCurrentLanguage();
        }
    }

    private boolean isLanguageChanged() {
        return !Objects.equals(getCurrentLanguage(), lastLoadedLang);
    }

    private String getCurrentLanguage() {
        return minecraft.getLanguageManager().getSelected();
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }

    public HashMap<String, HashMap<String, Attribute>> getAttributesMap() {
        ensureInitialized();
        return attributesMap;
    }

    public HashMap<String, Attribute> getAttributesByLang(String langCode) {
        ensureInitialized();
        return getAttributesMap().getOrDefault(langCode, new HashMap<>());
    }

    public Attribute getAttributeByDescriptionName(String name) {
        ensureInitialized();
        return getAttributesInSelected().getOrDefault(name.toLowerCase(Locale.ROOT), null);
    }


    public HashMap<String, Attribute> getAttributesInSelected() {
        ensureInitialized();
        return getAttributesByLang(getCurrentLanguage());
    }

    public void initAttributesMap() {
        attributesMap.clear();

        RegistryAccess registryAccess = getMinecraft().level.registryAccess();
        Registry<Attribute> attributeRegistry = registryAccess.registryOrThrow(Registries.ATTRIBUTE);

        String currentLang = getCurrentLanguage();

        for (ResourceLocation key : attributeRegistry.keySet()) {
            Attribute attribute = attributeRegistry.get(key);
            if (attribute == null) continue;

            String localizedName = Component.translatable(attribute.getDescriptionId()).getString();
            attributesMap.computeIfAbsent(currentLang, k -> new HashMap<>())
                    .put(localizedName.toLowerCase(Locale.ROOT), attribute);
        }
    }

    public Optional<Attribute> findAttributeByName(String inputName) {
        if (inputName == null || inputName.isEmpty()) return Optional.empty();
        ensureInitialized();
        String lowerInput = inputName.toLowerCase(Locale.ROOT);
        Map<String, Attribute> attrMap = getAttributesInSelected();
        return attrMap.entrySet().stream()
                .filter(entry -> entry.getKey().contains(lowerInput))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public Attribute getAttributeFromId(String idStr) {
        ensureInitialized();
        ResourceLocation id = ResourceLocation.tryParse(idStr);
        if (id != null) {
            return getMinecraft().level.registryAccess().registryOrThrow(Registries.ATTRIBUTE).get(id);
        }
        return null;
    }

    public Collection<String> getAllLocalizedAttributeNames() {
        ensureInitialized();
        return getAttributesInSelected().keySet();
    }

    public void reloadForLanguage(String langCode) {
        ensureInitialized();
        if (attributesMap.containsKey(langCode)) {
            return;
        }

        Registry<Attribute> attributeRegistry = getMinecraft().level.registryAccess().registryOrThrow(Registries.ATTRIBUTE);
        HashMap<String, Attribute> langMap = new HashMap<>();

        for (ResourceLocation key : attributeRegistry.keySet()) {
            Attribute attribute = attributeRegistry.get(key);
            if (attribute == null) continue;

            String localizedName = Component.translatable(attribute.getDescriptionId()).getString();
            langMap.put(localizedName.toLowerCase(Locale.ROOT), attribute);
        }

        attributesMap.put(langCode, langMap);
    }
    public ResourceLocation getAttributeResourceLocationByName(String attributeName) {
        ensureInitialized(); // 确保数据是最新的
        // 获取当前语言环境下的属性映射
        HashMap<String, Attribute> attributesInSelectedLang = getAttributesInSelected();
        // 查找与给定名称匹配的属性
        for (Map.Entry<String, Attribute> entry : attributesInSelectedLang.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(attributeName)) {
                return minecraft.level.registryAccess().registryOrThrow(Registries.ATTRIBUTE).getKey(entry.getValue());
            }
        }

        // 如果未找到匹配的属性，返回 null 或者抛出异常取决于你的需求
        return null;
    }
}
