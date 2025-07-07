package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.client.DpsActionBar;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DpsComponentPacket {
    private Component text;

    public DpsComponentPacket(Component text) {
        if (text == null) {
            this.text = Component.literal("");
        } else {
            this.text = text;
        }
        if (text == null) {
            Powerful_dummy.LOGGER.warn("DpsComponentPacket constructed with null Component, replaced with empty string.");
        }

    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(this.text);
    }

    public static DpsComponentPacket decode(FriendlyByteBuf buf) {
        return new DpsComponentPacket(buf.readComponent());
    }
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(()->{
           if (contextSupplier.get().getDirection().getReceptionSide().isClient()) {
               DpsActionBar.sendText(this.text);
           } else return;
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
