package com.keyfive.origins_vamp_compat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OriginsVampCompatMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("origins_vamp_compat")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("witherProtected")
                .then(Commands.argument("value", BoolArgumentType.bool())
                    .executes(ctx -> {
                        boolean value = BoolArgumentType.getBool(ctx, "value");
                        Config.SERVER.witherOnProtectedDrink.set(value);
                        ctx.getSource().sendSuccess(() -> Component.literal("witherOnProtectedDrink alterado para " + value), true);
                        return 1;
                    })
                )
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal("witherOnProtectedDrink: " + Config.witherOnProtectedDrink()), false);
                    return 1;
                })
            )
            .then(Commands.literal("infectProtected")
                .then(Commands.argument("value", BoolArgumentType.bool())
                    .executes(ctx -> {
                        boolean value = BoolArgumentType.getBool(ctx, "value");
                        Config.SERVER.infectProtectedRaces.set(value);
                        ctx.getSource().sendSuccess(() -> Component.literal("infectProtectedRaces alterado para " + value), true);
                        return 1;
                    })
                )
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal("infectProtectedRaces: " + Config.infectProtectedRaces()), false);
                    return 1;
                })
            )
            .then(Commands.literal("protectHuman")
                .then(Commands.argument("value", BoolArgumentType.bool())
                    .executes(ctx -> {
                        boolean value = BoolArgumentType.getBool(ctx, "value");
                        Config.SERVER.protectHumanRace.set(value);
                        ctx.getSource().sendSuccess(() -> Component.literal("protectHumanRace alterado para " + value), true);
                        return 1;
                    })
                )
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal("protectHumanRace: " + Config.protectHumanRace()), false);
                    return 1;
                })
            )
            .then(Commands.literal("witherHuman")
                .then(Commands.argument("value", BoolArgumentType.bool())
                    .executes(ctx -> {
                        boolean value = BoolArgumentType.getBool(ctx, "value");
                        Config.SERVER.witherOnHumanDrink.set(value);
                        ctx.getSource().sendSuccess(() -> Component.literal("witherOnHumanDrink alterado para " + value), true);
                        return 1;
                    })
                )
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal("witherOnHumanDrink: " + Config.witherOnHumanDrink()), false);
                    return 1;
                })
            )
            .then(Commands.literal("status")
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal(
                        "--- OriginsVampCompat Config ---\n" +
                        "witherOnProtectedDrink: " + Config.witherOnProtectedDrink() + "\n" +
                        "infectProtectedRaces: " + Config.infectProtectedRaces() + "\n" +
                        "protectHumanRace: " + Config.protectHumanRace() + "\n" +
                        "witherOnHumanDrink: " + Config.witherOnHumanDrink()
                    ), false);
                    return 1;
                })
            )
        );
    }
}
