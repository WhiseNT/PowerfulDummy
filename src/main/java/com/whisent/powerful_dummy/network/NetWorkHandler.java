package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.Powerful_dummy;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;


public class NetWorkHandler {

    public static void register(final RegisterPayloadHandlersEvent event) {
        System.out.println("=== POWERFUL_DUMMY: Registering network payloads ===");

        final PayloadRegistrar registrar = event.registrar("1.0");

        // 注册 DPS 组件包（服务器 -> 客户端）
        System.out.println("Registering DpsComponentPacket for client...");
        registrar.playToClient(
                DpsComponentPacket.TYPE,
                DpsComponentPacket.STREAM_CODEC,
                ClientPayloadHandler::handleDataOnMain
        );

        // 注册其他包...
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
                // 客户端处理器在 RegisterClientPayloadHandlersEvent 中注册
        );

        System.out.println("=== POWERFUL_DUMMY: Network registration complete ===");
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
