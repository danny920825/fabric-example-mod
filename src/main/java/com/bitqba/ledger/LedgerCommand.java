package com.bitqba.ledger;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/** Debug and validation commands for the field ledger. */
public final class LedgerCommand {
    private LedgerCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("arcanum")
                .then(CommandManager.literal("ledger")
                        .then(CommandManager.literal("status")
                                .executes(context -> status(context.getSource().getPlayerOrThrow())))
                        .then(CommandManager.literal("facts")
                                .executes(context -> facts(context.getSource().getPlayerOrThrow())))
                        .then(CommandManager.literal("open")
                                .executes(context -> {
                                    try {
                                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                        LedgerService.open(player);
                                    } catch (CommandSyntaxException exception) {
                                        context.getSource().sendError(Text.translatable("command.failed"));
                                    }
                                    return 1;
                                }))
                        .then(CommandManager.literal("seed")
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> seed(context.getSource().getPlayerOrThrow()))
                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                        .executes(context -> seed(EntityArgumentType.getPlayer(context, "target")))))
                        .then(CommandManager.literal("grant")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("fact", StringArgumentType.greedyString())
                                        .executes(LedgerCommand::grant)))
                        .then(CommandManager.literal("reset")
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> reset(context.getSource().getPlayerOrThrow()))
                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                        .executes(context -> reset(EntityArgumentType.getPlayer(context, "target")))))));
    }

    private static int status(ServerPlayerEntity player) {
        int completed = LedgerService.completed(player);
        int total = LedgerContent.get().entries().size();
        boolean seeded = LedgerService.isSeeded(player);
        player.sendMessage(Text.translatable("command.arcanum_storage.ledger.status",
                completed, total, seeded ? "yes" : "no").formatted(Formatting.GRAY), false);
        return completed;
    }

    private static int facts(ServerPlayerEntity player) {
        List<String> granted = LedgerService.grantedFacts(player);
        List<String> pending = LedgerService.pendingFacts(player);
        player.sendMessage(Text.translatable("command.arcanum_storage.ledger.facts.summary",
                granted.size(), pending.size()).formatted(Formatting.GOLD), false);

        int shown = 0;
        for (String fact : granted) {
            if (shown++ >= 80) {
                player.sendMessage(Text.translatable("command.arcanum_storage.ledger.facts.more",
                        granted.size() - 80).formatted(Formatting.DARK_GRAY), false);
                break;
            }
            player.sendMessage(Text.literal("+ " + fact).formatted(Formatting.GREEN), false);
        }
        shown = 0;
        for (String fact : pending) {
            if (shown++ >= 40) {
                player.sendMessage(Text.translatable("command.arcanum_storage.ledger.facts.more",
                        pending.size() - 40).formatted(Formatting.DARK_GRAY), false);
                break;
            }
            player.sendMessage(Text.literal("- " + fact).formatted(Formatting.DARK_GRAY), false);
        }
        return granted.size();
    }

    private static int seed(ServerPlayerEntity player) {
        LedgerService.seed(player);
        player.sendMessage(Text.translatable("command.arcanum_storage.ledger.seeded",
                LedgerService.isSeeded(player) ? "yes" : "no").formatted(Formatting.GRAY), false);
        return 1;
    }

    private static int grant(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            String raw = StringArgumentType.getString(context, "fact");
            if (!LedgerFact.isValid(raw)) {
                context.getSource().sendError(Text.translatable("command.arcanum_storage.ledger.invalid", raw));
                return 0;
            }
            boolean granted = LedgerService.grant(player, raw);
            context.getSource().sendFeedback(() -> Text.translatable(
                    granted ? "command.arcanum_storage.ledger.granted"
                            : "command.arcanum_storage.ledger.already", raw).formatted(Formatting.GRAY), false);
            return granted ? 1 : 0;
        } catch (CommandSyntaxException exception) {
            context.getSource().sendError(Text.translatable("command.failed"));
            return 0;
        }
    }

    private static int reset(ServerPlayerEntity player) {
        LedgerService.reset(player);
        player.sendMessage(Text.translatable("command.arcanum_storage.ledger.reset")
                .formatted(Formatting.GRAY), false);
        return 1;
    }
}
