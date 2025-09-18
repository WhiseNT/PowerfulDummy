package com.whisent.powerful_dummy.impl;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import dev.latvian.mods.kubejs.server.CommandKubeEvent;

public interface DummyEvents {
    EventGroup GROUP = EventGroup.of("DummyEvents");

    EventHandler CUSTOM = GROUP.common("custom",()->{
        return DummyCustomJS.class;
    });
}
