package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.Powerful_dummy;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetWorkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(
            new ResourceLocation(Powerful_dummy.MODID,"main"))
            .serverAcceptedVersions((version) -> true)
            .clientAcceptedVersions((version) -> true)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();
    public static final SimpleChannel CHANNEL_DPS = NetworkRegistry.ChannelBuilder.named(
            new ResourceLocation(Powerful_dummy.MODID,"dps"))
            .serverAcceptedVersions((version) -> true)
            .clientAcceptedVersions((version) -> true)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(DummyInfoPacket.class,id)
                .encoder(DummyInfoPacket::encode)
                .decoder(DummyInfoPacket::decode)
                .consumerMainThread(DummyInfoPacket::handle)
                .add();
        CHANNEL.messageBuilder(DpsComponentPacket.class, id+1)
                .encoder(DpsComponentPacket::encode)
                .decoder(DpsComponentPacket::decode)
                .consumerMainThread(DpsComponentPacket::handle)
                .add();
        CHANNEL.messageBuilder(ClearDpsDataPacket.class, id+2)
                .encoder(ClearDpsDataPacket::encode)
                .decoder(ClearDpsDataPacket::decode)
                .consumerMainThread(ClearDpsDataPacket::handle)
                .add();

    }
    public static void sendToServer(Object msg) {
        CHANNEL.send(PacketDistributor.SERVER.noArg(),msg);
    }
    public static void sendToAllClient(Object msg) {
        CHANNEL.send(PacketDistributor.ALL.noArg(),msg);
    }
}
