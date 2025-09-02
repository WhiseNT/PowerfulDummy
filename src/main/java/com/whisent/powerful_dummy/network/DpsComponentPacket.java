package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.PowerfulDummyConfig;
import com.whisent.powerful_dummy.client.overlay.DpsOverlay;
import com.whisent.powerful_dummy.utils.Debugger;
import com.whisent.powerful_dummy.utils.DummyEventUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
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

    public static void handleOnClient(final DpsComponentPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!PowerfulDummyConfig.useActionbarToShowData) {
                DpsOverlay.displayText(packet.damage(), packet.dps(), packet.totalDamage(), packet.combo(), packet.color());
            } else {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player != null) {
                    minecraft.player.displayClientMessage(
                            DummyEventUtils.getInfoComponent(packet.damage(), packet.dps(), packet.totalDamage(), packet.color()),
                            true
                    );
                }
            }
        }).exceptionally(e -> {
            Debugger.sendDebugMessage("Failed to handle DPS component packet: " + e.getMessage());
            e.printStackTrace();
            return null;
        });
    }
}