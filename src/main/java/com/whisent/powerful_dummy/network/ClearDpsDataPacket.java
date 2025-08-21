package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.dps.DpsData;
import com.whisent.powerful_dummy.dps.DpsTracker;
import com.whisent.powerful_dummy.impl.IActionBarDisplay;
import com.whisent.powerful_dummy.utils.DummyEventUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public class ClearDpsDataPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClearDpsDataPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Powerful_dummy.MODID, "clear_dps_data"));
    public static final StreamCodec<FriendlyByteBuf, ClearDpsDataPacket> STREAM_CODEC =
            StreamCodec.of(ClearDpsDataPacket::encode, ClearDpsDataPacket::decode);
    private byte clear;
    public ClearDpsDataPacket(byte clear) {
        this.clear = clear;
    }
    public static void encode(FriendlyByteBuf buf,ClearDpsDataPacket packet) {
        buf.writeByte(packet.clear);
    }
    public static ClearDpsDataPacket decode(FriendlyByteBuf buf) {
        return new ClearDpsDataPacket(buf.readByte());
    }
    public void handle(IPayloadContext contextSupplier) {
        contextSupplier.enqueueWork(() -> {
            if (contextSupplier.connection().getDirection().getReceptionSide().isServer()) {

                Player player = contextSupplier.player();
                if (player == null) return;
                DpsTracker.getDpsData(player).reset();
                ((IActionBarDisplay)player)
                        .sendActionBarMessage(Component.translatable("powerful_dummy.dps.cleared"));

            } else {
                System.out.print("客户端");
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
