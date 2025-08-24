package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.entity.TestDummyEntity;
import com.whisent.powerful_dummy.utils.Debugger;
import com.whisent.powerful_dummy.utils.MobTypeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class DummyInfoPacket implements CustomPacketPayload {
    public static final Type<DummyInfoPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Powerful_dummy.MODID, "dummy_info"));
    public static final StreamCodec<FriendlyByteBuf, DummyInfoPacket> STREAM_CODEC =
            StreamCodec.of(DummyInfoPacket::encode, DummyInfoPacket::decode);

    private final int id;
    private final int mobTypeId;
    private final CompoundTag map;

    public DummyInfoPacket(int id, int mobTypeId, CompoundTag attributesMap) {
        this.id = id;
        this.mobTypeId = mobTypeId;
        this.map = attributesMap;
    }

    public static void encode(FriendlyByteBuf buf, DummyInfoPacket packet) {
        buf.writeInt(packet.id);
        buf.writeInt(packet.mobTypeId);
        buf.writeNbt(packet.map);
    }

    public static DummyInfoPacket decode(FriendlyByteBuf buf) {
        return new DummyInfoPacket(buf.readInt(), buf.readInt(), buf.readNbt());
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.connection().getDirection().getReceptionSide().isServer()) {
                // 服务器端处理 - 来自客户端的更新请求
                handleOnServer(context);
            } else {
                // 客户端处理 - 来自服务器的同步数据
                handleOnClient();
            }
        }).exceptionally(e -> {
            Powerful_dummy.LOGGER.error("Failed to handle DummyInfoPacket", e);
            return null;
        });
    }

    private void handleOnServer(IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        if (player == null) return;

        Level world = player.level();
        Entity entity = world.getEntity(this.id);

        if (entity instanceof TestDummyEntity testDummy && !testDummy.isRemoved()) {
            // 更新假人属性

            for (String attributeKey : this.map.getAllKeys()) {
                try {
                    ResourceLocation rl = ResourceLocation.parse(attributeKey);
                    double value = this.map.getDouble(attributeKey);

                    var registry = testDummy.level().registryAccess().registryOrThrow(Registries.ATTRIBUTE);
                    registry.getHolder(rl).ifPresent(attribute -> {
                        testDummy.getAttribute(attribute).setBaseValue(value);
                    });
                } catch (Exception e) {
                    //Powerful_dummy.LOGGER.warn("Failed to set attribute {}: {}", attributeKey, e.getMessage());
                }
            }
            testDummy.setMobType(MobTypeHelper.fromId(this.mobTypeId));

            PacketDistributor.sendToAllPlayers(new DummyInfoPacket(
                    testDummy.getId(),
                    MobTypeHelper.toId(testDummy.getMobType()),
                    this.map
            ));
        }
    }

    private void handleOnClient() {
        Minecraft mc = Minecraft.getInstance();
        Level world = mc.level;

        if (world != null) {
            Entity entity = world.getEntity(this.id);

            if (entity instanceof TestDummyEntity testDummy && !testDummy.isRemoved()) {
                // 只更新客户端显示，不调用服务器方法
                testDummy.setMobType(MobTypeHelper.fromId(this.mobTypeId));

                for (String attributeKey : this.map.getAllKeys()) {
                    try {
                        ResourceLocation rl = ResourceLocation.parse(attributeKey);
                        double value = this.map.getDouble(attributeKey);

                        var registry = testDummy.level().registryAccess().registryOrThrow(Registries.ATTRIBUTE);
                        registry.getHolder(rl).ifPresent(attribute -> {
                            testDummy.getAttribute(attribute).setBaseValue(value);
                        });
                    } catch (Exception e) {
                        //Powerful_dummy.LOGGER.warn("Failed to set attribute {} on client: {}", attributeKey, e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
