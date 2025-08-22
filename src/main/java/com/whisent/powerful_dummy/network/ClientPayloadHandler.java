package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.PowerfulDummyConfig;
import com.whisent.powerful_dummy.client.DpsActionBar;
import com.whisent.powerful_dummy.utils.DummyEventUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

    public static void handleDataOnMain(final DpsComponentPacket packet, final IPayloadContext context) {
        if (!PowerfulDummyConfig.useActionbarToShowData) {
            DpsActionBar.displayText(packet.damage(), packet.dps(), packet.totalDamage(), packet.combo(), packet.color());
        } else {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                minecraft.player.displayClientMessage(
                        DummyEventUtils.getInfoComponent(packet.damage(), packet.dps(), packet.totalDamage(), packet.color()),
                        true
                );
            }
        }
    }
}
