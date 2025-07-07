package com.whisent.powerful_dummy.mixin;

import com.whisent.powerful_dummy.impl.IActionBarDisplay;
import com.whisent.powerful_dummy.network.DpsComponentPacket;
import com.whisent.powerful_dummy.network.NetWorkHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements IActionBarDisplay {

    @Override
    public void sendActionBarMessage(Component message) {
        if (((ServerPlayer)(Object)this).connection != null) { // 确保连接存在
            ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(message);
            ((ServerPlayer)(Object)this).connection.send(packet);
        }
    }

    @Override
    public void sendDpsBarMessage(Component message) {
        if (((ServerPlayer)(Object)this).connection != null) { // 确保连接存在
            DpsComponentPacket packet = new DpsComponentPacket(message);
            NetWorkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(
                    () -> ((ServerPlayer)(Object)this)), packet);
        }
    }

}
