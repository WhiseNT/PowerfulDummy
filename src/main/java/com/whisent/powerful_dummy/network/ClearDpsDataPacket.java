package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.dps.DpsTracker;
import com.whisent.powerful_dummy.impl.IActionBarDisplay;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClearDpsDataPacket(byte clear) implements CustomPacketPayload {
    public static final Type<ClearDpsDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("powerful_dummy", "clear_dps_data"));

    public static final StreamCodec<ByteBuf, ClearDpsDataPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BYTE, ClearDpsDataPacket::clear,
                    ClearDpsDataPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.connection().getDirection().getReceptionSide().isServer()) {
                // 服务器端处理
                Player player = context.player();
                if (player != null) {
                    DpsTracker.getDpsData(player).reset();
                    if (player instanceof IActionBarDisplay actionBarDisplay) {
                        actionBarDisplay.sendActionBarMessage(Component.translatable("powerful_dummy.dps.cleared"));
                    }
                }
            } else {
                // 客户端处理
                System.out.print("客户端收到清空DPS数据包");
            }
        }).exceptionally(e -> {
            Powerful_dummy.LOGGER.error("Failed to handle ClearDpsDataPacket", e);
            return null;
        });
    }
}
