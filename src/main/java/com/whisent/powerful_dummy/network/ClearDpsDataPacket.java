package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.dps.DpsData;
import com.whisent.powerful_dummy.dps.DpsTracker;
import com.whisent.powerful_dummy.impl.IActionBarDisplay;
import com.whisent.powerful_dummy.utils.DummyEventUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClearDpsDataPacket {
    private byte clear;
    public ClearDpsDataPacket(byte clear) {
        this.clear = clear;
    }
    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(clear);
    }
    public static ClearDpsDataPacket decode(FriendlyByteBuf buf) {
        return new ClearDpsDataPacket(buf.readByte());
    }
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isServer()) {

                Player player = context.getSender();
                if (player == null) return;
                DpsTracker.getDpsData(player).reset();
                ((IActionBarDisplay)player)
                        .sendActionBarMessage(Component.translatable("powerful_dummy.dps.cleared"));

            } else {
                System.out.print("客户端");
            }

        });
        context.setPacketHandled(true);
    }

}
