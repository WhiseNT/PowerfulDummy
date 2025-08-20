package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.Config;
import com.whisent.powerful_dummy.client.DpsActionBar;
import com.whisent.powerful_dummy.utils.DummyEventUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DpsComponentPacket {
    private double damage;
    private double dps;
    private double totalDamage;
    private int combo;
    private int color;

    public DpsComponentPacket(double damage, double dps, double totalDamage, int combo, int color) {
        this.damage = damage;
        this.dps = dps;
        this.totalDamage = totalDamage;
        this.combo = combo;
        this.color = color;

    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(damage);
        buf.writeDouble(dps);
        buf.writeDouble(totalDamage);
        buf.writeInt(combo);
        buf.writeInt(color);
    }

    public static DpsComponentPacket decode(FriendlyByteBuf buf) {
        return new DpsComponentPacket(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt(), buf.readInt());
    }
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(()->{
           if (contextSupplier.get().getDirection().getReceptionSide().isClient()) {
               if (!Config.useActionbarToShowData) {
                   DpsActionBar.displayText(damage, dps, totalDamage, combo, color);
               } else {
                   Minecraft minecraft = Minecraft.getInstance();
                   if (minecraft.player != null) {
                       minecraft.player.displayClientMessage(
                               DummyEventUtils.getInfoComponent(damage,dps,totalDamage,color), true);
                   }
               }

           } else return;
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
