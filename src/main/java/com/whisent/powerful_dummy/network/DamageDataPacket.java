package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.client.DpsActionBar;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DamageDataPacket {
    private final double amount;
    private final boolean flag;
    public DamageDataPacket(double amount,boolean flag) {
        this.amount = amount;
        this.flag = flag;
    }
    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(amount);
        buf.writeBoolean(flag);
    }
    public static DamageDataPacket decode(FriendlyByteBuf buf) {
        return new DamageDataPacket(buf.readDouble(),buf.readBoolean());
    }
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            if (contextSupplier.get().getDirection().getReceptionSide().isClient()) {
                if (flag) {
                    //DpsActionBar.getDamageHistory().clean();
                } else {
                    //DpsActionBar.getDamageHistory().addDamage(amount);
                }
            }
        });

    }
}
