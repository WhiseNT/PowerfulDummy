package com.whisent.test_dummy.network;

import com.whisent.test_dummy.Test_dummy;
import com.whisent.test_dummy.entity.TestDummyEntity;
import com.whisent.test_dummy.utils.MobTypeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class DummyInfoPacket {
    private int id;
    private int mobTypeId;
    private CompoundTag map;

    public DummyInfoPacket(int id, int mobTypeId,CompoundTag attributesMap) {
        this.id = id;
        this.mobTypeId = mobTypeId;
        this.map = attributesMap;
    }
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(mobTypeId);
        buf.writeNbt(map);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            if (contextSupplier.get().getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = contextSupplier.get().getSender();
                if (player == null) return;
                Level world = player.level();
                Entity entity = world.getEntity(this.id);
                if (entity != null && entity instanceof TestDummyEntity testDummy && !testDummy.isRemoved()) {
                    testDummy.setMobType(MobTypeHelper.fromId(this.mobTypeId));
                    CompoundTag attributesMapBack = new CompoundTag();
                    for (String attributeKey : this.map.getAllKeys()) {

                        ResourceLocation rl = new ResourceLocation(attributeKey);
                        double value = this.map.getDouble(attributeKey);
                        Attribute attribute = testDummy.level().registryAccess().registryOrThrow(Registries.ATTRIBUTE).get(rl);

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
                            ResourceLocation rl = new ResourceLocation(attributeKey);
                            double value = this.map.getDouble(attributeKey);
                            Attribute attribute = testDummy.level().registryAccess().registryOrThrow(Registries.ATTRIBUTE).get(rl);
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
}
