package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.PowerfulDummyConfig;
import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.client.DpsActionBar;
import com.whisent.powerful_dummy.utils.DummyEventUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.function.Supplier;

public class DpsComponentPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DpsComponentPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Powerful_dummy.MODID, "dps_component"));
    public static final StreamCodec<FriendlyByteBuf, DpsComponentPacket> STREAM_CODEC =
            StreamCodec.of(DpsComponentPacket::encode, DpsComponentPacket::decode);
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

    public static void encode(FriendlyByteBuf buf,DpsComponentPacket packet) {
        buf.writeDouble(packet.damage);
        buf.writeDouble(packet.dps);
        buf.writeDouble(packet.totalDamage);
        buf.writeInt(packet.combo);
        buf.writeInt(packet.color);
    }

    public static DpsComponentPacket decode(FriendlyByteBuf buf) {
        return new DpsComponentPacket(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt(), buf.readInt());
    }
    public static void handle(DpsComponentPacket packet,IPayloadContext contextSupplier) {
        contextSupplier.enqueueWork(()->{
           if (contextSupplier.connection().getDirection().getReceptionSide().isClient()) {
               if (!PowerfulDummyConfig.useActionbarToShowData) {
                   DpsActionBar.displayText(packet.damage, packet.dps, packet.totalDamage, packet.combo, packet.color);
               } else {
                   Minecraft minecraft = Minecraft.getInstance();
                   if (minecraft.player != null) {
                       minecraft.player.displayClientMessage(
                               DummyEventUtils.getInfoComponent(packet.damage,packet.dps,packet.totalDamage,packet.color), true);
                   }
               }

           } else return;
        });
    }
    public static void handleClient(DpsComponentPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.connection().getDirection().getReceptionSide().isClient()) {
                if (!PowerfulDummyConfig.useActionbarToShowData) {
                    DpsActionBar.displayText(packet.damage, packet.dps, packet.totalDamage, packet.combo, packet.color);
                } else {
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft.player != null) {
                        minecraft.player.displayClientMessage(
                                DummyEventUtils.getInfoComponent(packet.damage, packet.dps, packet.totalDamage, packet.color),
                                true
                        );
                    }
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
