package com.whisent.powerful_dummy.network;


import com.whisent.powerful_dummy.Powerful_dummy;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Powerful_dummy.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkRegistry {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0");

        // 注册 DpsComponentPacket（服务器 -> 客户端）
        registrar.playToClient(
                DpsComponentPacket.TYPE,
                DpsComponentPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        DpsComponentPacket::handleOnClient,
                        null
                )
        );

        // 注册 DamageDataPacket（服务器 -> 客户端）
        registrar.playToClient(
                DamageDataPacket.TYPE,
                DamageDataPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        DamageDataPacket::handleOnClient,
                        null
                )
        );
        registrar.playBidirectional(
                DummyInfoPacket.TYPE,
                DummyInfoPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        DummyInfoPacket::handle,
                        DummyInfoPacket::handle
                )
        );
        registrar.playToServer(
                ClearDpsDataPacket.TYPE,
                ClearDpsDataPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClearDpsDataPacket::handle,
                        ClearDpsDataPacket::handle
                )
        );
    }
}
