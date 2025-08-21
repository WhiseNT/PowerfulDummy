package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.Powerful_dummy;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetWorkHandler {
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Powerful_dummy.MODID)
                .versioned("1.0"); // 版本号

        registrar.playToClient(
                DpsComponentPacket.TYPE,
                DpsComponentPacket.STREAM_CODEC,
                DpsComponentPacket::handleClient
        );

        registrar.playToServer(
                DummyInfoPacket.TYPE,
                DummyInfoPacket.STREAM_CODEC,
                DummyInfoPacket::handle
        );

        registrar.playToServer(
                ClearDpsDataPacket.TYPE,
                ClearDpsDataPacket.STREAM_CODEC,
                ClearDpsDataPacket::handle
        );

        registrar.playToClient(
                DamageDataPacket.TYPE,
                DamageDataPacket.STREAM_CODEC,
                DamageDataPacket::handle
        );

        // 如果需要客户端到客户端的通信，使用 playToClient
        // registrar.playToClient(...)
    }

    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    public static void sendToClient(CustomPacketPayload payload, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    public static void sendToAllClient(CustomPacketPayload payload) {
        PacketDistributor.sendToAllPlayers(payload);
    }
}
