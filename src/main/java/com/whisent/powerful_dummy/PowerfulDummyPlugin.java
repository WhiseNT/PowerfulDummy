package com.whisent.powerful_dummy;

import com.whisent.powerful_dummy.impl.DummyEvents;
import com.whisent.powerful_dummy.kjs.DummyCustomizer;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class PowerfulDummyPlugin implements KubeJSPlugin {

    @Override
    public void registerBindings(BindingRegistry bindings) {
        KubeJSPlugin.super.registerBindings(bindings);
        bindings.add("DummyCustomizer", DummyCustomizer.class);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        KubeJSPlugin.super.registerEvents(registry);
        registry.register(DummyEvents.GROUP);
    }
}
