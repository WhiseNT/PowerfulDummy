package com.whisent.powerful_dummy.utils;

import com.whisent.powerful_dummy.data.tag.DamageTagLoader;
import com.whisent.powerful_dummy.dps.DpsData;
import com.whisent.powerful_dummy.dps.DpsTracker;
import com.whisent.powerful_dummy.impl.IActionBarDisplay;
import com.whisent.powerful_dummy.network.ClearDpsDataPacket;
import com.whisent.powerful_dummy.network.DamageDataPacket;
import com.whisent.powerful_dummy.network.DpsComponentPacket;
import com.whisent.powerful_dummy.network.NetWorkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;


public class DummyEventUtils {
    public static void updateDpsMessages(Level level) {
        if (!level.isClientSide()) {
            for (Player player : level.players()) {
                sendHurtMessage((ServerPlayer) player);
            }
        }
    }
    public static void sendHurtMessage(ServerPlayer player) {
        float totalDamage = DpsTracker.getDpsData(player).getTotalDamage();
        if (!DpsTracker.getDpsData(player).needsReset() && totalDamage > 0.0f) {
            DpsData data = DpsTracker.getDpsData(player);
            int color = DamageTagLoader.findDisplayColor(data.getDamageSource());
            float damage = data.getLastDamage();
            float dps = DpsTracker.getDps(player);
            float total = data.getTotalDamage();

            //((IActionBarDisplay)player).sendActionBarMessage(finalText);
            DpsComponentPacket packet = new DpsComponentPacket(damage, dps, total, data.getDamageDataList().size(), color);
            PacketDistributor.sendToPlayer(player, packet);


            //NetWorkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
            //ClientboundSetActionBarTextPacket packet2= new ClientboundSetActionBarTextPacket(finalText);
            //player.connection.send(packet2);
        } else {
            ((IActionBarDisplay)player).powerfulDummy$sendDamage(0.0, true);
        }
    }
    public static MutableComponent getInfoComponent(double damage,double dps,double total,int color) {
        MutableComponent damageComponent = Component.translatable("chat.powerful_dummy.damage")
                .withStyle(style -> style.withColor(ChatFormatting.WHITE));
        MutableComponent damageCountComponent = Component.literal(String.format("%.1f", damage))
                .withStyle(style -> style.withColor(color));

        MutableComponent dpsComponent = Component.translatable("chat.powerful_dummy.dps").append(String.format("%.1f", dps))
                .withStyle(style -> style.withColor(ChatFormatting.WHITE));
        MutableComponent totalComponent = Component.translatable("chat.powerful_dummy.total_damage").append(String.format("%.1f", total))
                .withStyle(style -> style.withColor(ChatFormatting.WHITE));

        return damageComponent.append(damageCountComponent)
                .append(dpsComponent).append(totalComponent);
    }
}
