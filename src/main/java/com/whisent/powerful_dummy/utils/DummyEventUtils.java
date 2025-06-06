package com.whisent.powerful_dummy.utils;

import com.whisent.powerful_dummy.data.tag.DamageTagLoader;
import com.whisent.powerful_dummy.dps.DpsData;
import com.whisent.powerful_dummy.dps.DpsTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DummyEventUtils {
    public static void updateDpsMessages(Level level) {
        if (!level.isClientSide()) {
            for (Player player : level.players()) {
                sendHurtMessage((ServerPlayer) player);
            }
        }
    }
    public static void sendHurtMessage(ServerPlayer player) {

        float dps = DpsTracker.getDps(player);
        if (dps > 0.0f) {
            DpsData data = DpsTracker.getDpsData(player);
            int color = DamageTagLoader.findDisplayColor(data.getDamageSource());
            float damage = data.getLastDamage();
            float total = data.getTotalDamage();
            MutableComponent damageComponent = Component.translatable("chat.test_dummy.damage")
                    .withStyle(style -> style.withColor(ChatFormatting.WHITE));
            MutableComponent damageCountComponent = Component.literal(String.format("%.1f", damage))
                    .withStyle(style -> style.withColor(color));

            MutableComponent dpsComponent = Component.translatable("chat.powerful_dummy.dps").append(String.format("%.1f", dps))
                    .withStyle(style -> style.withColor(ChatFormatting.WHITE));
            MutableComponent totalComponent = Component.translatable("chat.powerful_dummy.total_damage").append(String.format("%.1f", total))
                    .withStyle(style -> style.withColor(ChatFormatting.WHITE));

            MutableComponent finalText = damageComponent.append(damageCountComponent)
                    .append(dpsComponent).append(totalComponent);
            ClientboundSetActionBarTextPacket packet= new ClientboundSetActionBarTextPacket(finalText);
            player.connection.send(packet);
        }
    }
}
