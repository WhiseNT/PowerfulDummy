package com.whisent.powerful_dummy.network;


import com.whisent.powerful_dummy.Powerful_dummy;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Powerful_dummy.MODID)
public class NetworkRegistry {

    @SubscribeEvent
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

        System.out.println("=== POWERFUL_DUMMY: Network registration complete ===");
    }
}
