package com.whisent.powerful_dummy.impl;

import net.minecraft.network.chat.Component;

public interface IActionBarDisplay {
    void sendActionBarMessage(Component message);
    void powerfulDummy$sendDamage(double damage, boolean flag);
}
