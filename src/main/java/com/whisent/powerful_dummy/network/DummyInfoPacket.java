package com.whisent.powerful_dummy.network;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.entity.TestDummyEntity;
import com.whisent.powerful_dummy.utils.MobTypeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class DummyInfoPacket implements CustomPacketPayload {
    public static final Type<DummyInfoPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Powerful_dummy.MODID, "dummy_info"));
    public static final StreamCodec<FriendlyByteBuf, DummyInfoPacket> STREAM_CODEC =
            StreamCodec.of(DummyInfoPacket::encode, DummyInfoPacket::decode);
    private int id;
    private int mobTypeId;
    private CompoundTag map;

    public DummyInfoPacket(int id, int mobTypeId,CompoundTag attributesMap) {
        this.id = id;
        this.mobTypeId = mobTypeId;
        this.map = attributesMap;
    }
    public static void encode(FriendlyByteBuf buf,DummyInfoPacket packet) {
        buf.writeInt(packet.id);
        buf.writeInt(packet.mobTypeId);
        buf.writeNbt(packet.map);
    }

    public void handle(IPayloadContext contextSupplier) {
        contextSupplier.enqueueWork(() -> {
            if (contextSupplier.connection().getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = (ServerPlayer) contextSupplier.player();
                if (player == null) return;

                Level world = player.level();
                Entity entity = world.getEntity(this.id);
                if (entity != null && entity instanceof TestDummyEntity testDummy && !testDummy.isRemoved()) {
                    testDummy.setMobType(MobTypeHelper.fromId(this.mobTypeId));
                    CompoundTag attributesMapBack = new CompoundTag();
                    for (String attributeKey : this.map.getAllKeys()) {

                        ResourceLocation rl = ResourceLocation.parse(attributeKey);
                        double value = this.map.getDouble(attributeKey);
                        Holder<Attribute> attribute = testDummy.level().registryAccess().registryOrThrow(Registries.ATTRIBUTE).getHolder(rl).get();

                        testDummy.getAttribute(attribute).setBaseValue(value);
                        attributesMapBack.putDouble(attributeKey, value);
                    }
                    NetWorkHandler.sendToAllClient(new DummyInfoPacket(
                            testDummy.getId(),
                            MobTypeHelper.toId(testDummy.getMobType()),
                            map
                    ));
                }
            } else {
                Minecraft mc = Minecraft.getInstance();
                Level world = mc.level;
                if (world != null) {
                    Entity entity = world.getEntity(this.id);
                    if (entity != null && entity instanceof TestDummyEntity testDummy && !testDummy.isRemoved()) {
                        testDummy.setMobType(MobTypeHelper.fromId(this.mobTypeId));
                        for (String attributeKey : this.map.getAllKeys()) {
                            ResourceLocation rl = ResourceLocation.parse(attributeKey);
                            double value = this.map.getDouble(attributeKey);
                            Holder<Attribute> attribute = testDummy.level().registryAccess().registryOrThrow(Registries.ATTRIBUTE).getHolder(rl).get();
                            testDummy.getAttribute(attribute).setBaseValue(value);
                            testDummy.getServer().sendSystemMessage(Component.literal(String.valueOf(attribute) + " "+ value));
                        }
                    }
                }
            }
        });

    }
    public static DummyInfoPacket decode(FriendlyByteBuf buf) {
        return new DummyInfoPacket(buf.readInt(),buf.readInt(),buf.readNbt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
