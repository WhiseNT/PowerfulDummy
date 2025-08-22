package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.client.DpsActionBar;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;


public record DamageDataPacket(double amount, boolean flag) implements CustomPacketPayload {

    public static final Type<DamageDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("powerful_dummy", "damage_data"));

    public static final StreamCodec<ByteBuf, DamageDataPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.DOUBLE, DamageDataPacket::amount,
                    ByteBufCodecs.BOOL, DamageDataPacket::flag,
                    DamageDataPacket::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnClient(final DamageDataPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.flag()) {
                DpsActionBar.getDamageHistory().clean();
            } else {
                DpsActionBar.getDamageHistory().addDamage(packet.amount());
            }
        }).exceptionally(e -> {
            System.err.println("Failed to handle damage data packet: " + e.getMessage());
            e.printStackTrace();
            return null;
        });
    }
}
