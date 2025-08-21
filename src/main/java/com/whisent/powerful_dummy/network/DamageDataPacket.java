package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.client.DpsActionBar;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class DamageDataPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DamageDataPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Powerful_dummy.MODID, "dps_component"));
    public static final StreamCodec<FriendlyByteBuf, DamageDataPacket> STREAM_CODEC =
            StreamCodec.of(DamageDataPacket::encode, DamageDataPacket::decode);
    private final double amount;
    private final boolean flag;
    public DamageDataPacket(double amount,boolean flag) {
        this.amount = amount;
        this.flag = flag;
    }
    public static void encode(FriendlyByteBuf buf,DamageDataPacket  packet) {
        buf.writeDouble(packet.amount);
        buf.writeBoolean(packet.flag);
    }
    public static DamageDataPacket decode(FriendlyByteBuf buf) {
        return new DamageDataPacket(buf.readDouble(),buf.readBoolean());
    }
    public void handle(IPayloadContext contextSupplier) {
        contextSupplier.enqueueWork(() -> {
            if (contextSupplier.connection().getDirection().getReceptionSide().isClient()) {
                if (flag) {
                    DpsActionBar.getDamageHistory().clean();
                } else {
                    DpsActionBar.getDamageHistory().addDamage(amount);
                }
            }
        });

    }
    public static void handleClient(DamageDataPacket packet, IPayloadContext context) {
        if (packet.flag) {
            DpsActionBar.getDamageHistory().clean();
        } else {
            DpsActionBar.getDamageHistory().addDamage(packet.amount);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
