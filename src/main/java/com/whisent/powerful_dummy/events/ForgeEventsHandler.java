package com.whisent.powerful_dummy.events;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.data.AttributeLoader;
import com.whisent.powerful_dummy.data.tag.DamageTagLoader;
import com.whisent.powerful_dummy.dps.DamageData;
import com.whisent.powerful_dummy.dps.DpsTracker;
import com.whisent.powerful_dummy.utils.Debugger;
import com.whisent.powerful_dummy.utils.TimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;


@EventBusSubscriber(modid = Powerful_dummy.MODID, value = Dist.CLIENT)
public class ForgeEventsHandler {
    @SubscribeEvent
    public static void dataListener(AddReloadListenerEvent event){
        event.addListener(new AttributeLoader());
        event.addListener(new DamageTagLoader());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("damagelog")
                        .requires(source -> source.hasPermission(0))
                        .then(Commands.literal("get")
                        .executes(context -> { // 不带参数的 get
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            List<DamageData> list = DpsTracker.getDpsData(player).getDamageDataList();
                            int maxEntries = 20; // 默认显示20条
                            displayDamageLog(player, list, maxEntries);
                            return 1;
                        })
                        .then(Commands.argument("maxEntries", IntegerArgumentType.integer(1, 100)) // 带参数的 get
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                int maxEntries = IntegerArgumentType.getInteger(context, "maxEntries");
                                List<DamageData> list = DpsTracker.getDpsData(player).getDamageDataList();
                                displayDamageLog(player, list, maxEntries);
                                return 1;
                            })
                        ))
                        .then(Commands.literal("time") // 新增 time 子命令
                        .then(Commands.literal("get") // time get 命令
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                long currentDuration = DpsTracker.getDpsData(player).getResetTime();
                                player.sendSystemMessage(Component.translatable(
                                        "chat.powerful_dummy.damagelog.time.current",
                                        currentDuration / 1000
                                ));
                                return 1;
                            })
                        )
                        .then(Commands.literal("set")
                        .then(Commands.argument("seconds", IntegerArgumentType.integer(0, 100))
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                int seconds = IntegerArgumentType.getInteger(context, "seconds");
                                DpsTracker.getDpsData(player).setResetTime(seconds);
                                player.sendSystemMessage(Component.translatable(
                                        "chat.powerful_dummy.damagelog.time.set",
                                        seconds
                                ));
                                return 1;
                                })
                        )))
        );

        event.getDispatcher()
            .register(Commands.literal("debuglog")
            .then(
            Commands.argument("state", BoolArgumentType.bool()) // 添加参数名称
            .executes(context -> {
                boolean open = BoolArgumentType.getBool(context, "state");
                Debugger.setOpen(open);

                Component message = Component.literal("Debug mode set to: " + open);
                context.getSource().sendSuccess(() -> message, false);

                Debugger.sendAlwaysDebugMessage("[Command] Debug mode set to: " + open);
                return 1;
            })
            )
            .executes(context -> {
                boolean currentState = Debugger.isOpen();
                Component message = Component.literal("Debug mode is currently: " + currentState);
                context.getSource().sendSuccess(() -> message, false);

                Debugger.sendAlwaysDebugMessage("[Command] Debug mode query: " + currentState);
                return 1;
            })
            );
    };
    private static void displayDamageLog(ServerPlayer player, List<DamageData> list, int maxEntries) {
        if (list.isEmpty()) {
            player.sendSystemMessage(Component.translatable("chat.powerful_dummy.damagelog.nodata"));
            return;
        }

        // 计算实际显示的条目数（不超过列表大小）
        int displayCount = Math.min(maxEntries, list.size());

        // 发送显示信息
        player.sendSystemMessage(Component.translatable("chat.powerful_dummy.damagelog.title")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        player.sendSystemMessage(Component.translatable("chat.powerful_dummy.damagelog.display", displayCount)
                        .append(Component.translatable("chat.powerful_dummy.damagelog.display2",list.size()))
                .withStyle(ChatFormatting.YELLOW));

        List<DamageData> subList = list.subList(Math.max(0, list.size() - displayCount), list.size());

        subList.forEach(damageData -> {
            int color = DamageTagLoader.findDisplayColor(damageData.getDamageSource());
            MutableComponent damageComponent = Component.translatable("chat.powerful_dummy.damage")
                    .withStyle(style -> style.withColor(ChatFormatting.WHITE));

            MutableComponent damageCountComponent = Component.literal(String.format("%.1f", damageData.getAmount()))
                    .withStyle(style -> style.withColor(color));

            MutableComponent dpsComponent = Component.translatable("chat.powerful_dummy.dps")
                    .append(Component.literal(String.format("%.1f", damageData.getCurrentDps()))
                            .withStyle(style -> style.withColor(ChatFormatting.WHITE)));

            // 添加时间戳信息
            MutableComponent timeComponent =
                    Component.literal(" [")
                    .append(TimeUtils.formatRelativeTime(damageData.getTimestamp()))
                    .append(Component.literal("] "))
                    .withStyle(ChatFormatting.GRAY);

            MutableComponent totalComponent = damageComponent
                    .append(damageCountComponent)
                    .append(dpsComponent)
                    .append(timeComponent);

            player.sendSystemMessage(totalComponent);
        });

        // 添加提示信息
        if (list.size() > maxEntries) {
            player.sendSystemMessage(Component.translatable("chat.powerful_dummy.damagelog.return",list.size())
                    .withStyle(ChatFormatting.GREEN));
        }
    }


}
