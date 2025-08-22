package com.whisent.powerful_dummy.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record DpsComponentPacket(double damage, double dps, double totalDamage, int combo,
                                 int color) implements CustomPacketPayload {
    public static final Type<DpsComponentPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("powerful_dummy", "dps_component"));

    // 使用正确的 StreamCodec.composite() 方法
    public static final StreamCodec<ByteBuf, DpsComponentPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.DOUBLE, DpsComponentPacket::damage,
                    ByteBufCodecs.DOUBLE, DpsComponentPacket::dps,
                    ByteBufCodecs.DOUBLE, DpsComponentPacket::totalDamage,
                    ByteBufCodecs.INT, DpsComponentPacket::combo,
                    ByteBufCodecs.INT, DpsComponentPacket::color,
                    DpsComponentPacket::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
