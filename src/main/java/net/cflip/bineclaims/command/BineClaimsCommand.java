package net.cflip.bineclaims.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cflip.bineclaims.BineClaims;
import net.cflip.bineclaims.guild.Guild;
import net.cflip.bineclaims.guild.GuildInterface;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

import java.util.HashMap;
import java.util.Map;

public class BineClaimsCommand {
	private static final Map<String, Text> commandHelpMap = new HashMap<>();

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		commandHelpMap.put("claim", new TranslatableText("command.help.claim"));
		commandHelpMap.put("owner", new TranslatableText("command.help.owner"));
		commandHelpMap.put("list", new TranslatableText("command.help.list"));
		commandHelpMap.put("guild", new TranslatableText("command.help.guild"));
		commandHelpMap.put("guild create", new TranslatableText("command.help.guild.create"));
		commandHelpMap.put("guild join", new TranslatableText("command.help.guild.join"));
		commandHelpMap.put("guild leave", new TranslatableText("command.help.guild.leave"));

		dispatcher.register(CommandManager.literal("bclaims")
			.then(CommandManager.literal("help").then(CommandManager.argument("command", StringArgumentType.greedyString()).executes(BineClaimsCommand::helpCommand)).executes(BineClaimsCommand::help))
			.then(CommandManager.literal("claim").executes(BineClaimsCommand::claim))
			.then(CommandManager.literal("owner").executes(BineClaimsCommand::owner))
			.then(CommandManager.literal("list").executes(BineClaimsCommand::listGuilds))
			.then(CommandManager.literal("guild")
				.then(CommandManager.literal("create").then(CommandManager.argument("name", StringArgumentType.greedyString()).executes(BineClaimsCommand::guildCreate)))
				.then(CommandManager.literal("join").then(CommandManager.argument("guild", new GuildArgumentType()).executes(BineClaimsCommand::guildJoin)))
				.then(CommandManager.literal("leave").executes(BineClaimsCommand::guildLeave))
		));
	}

	public static int help(CommandContext<ServerCommandSource> context) {
		Text commands = Texts.join(commandHelpMap.keySet(), Text::of);
		context.getSource().sendFeedback(new TranslatableText("command.help.main", commands), false);
		return 0;
	}

	public static int helpCommand(CommandContext<ServerCommandSource> context) {
		String argument = context.getArgument("command", String.class);
		if (commandHelpMap.containsKey(argument)) {
			TranslatableText desc = new TranslatableText("command.help.command_info", "/bclaims " + argument);
			context.getSource().sendFeedback(desc.append(commandHelpMap.get(argument)), false);
		} else {
			Text commands = Texts.join(commandHelpMap.keySet(), Text::of);
			context.getSource().sendFeedback(new TranslatableText("command.help.unknown", commands), false);
		}
		return 0;
	}

	public static int claim(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		if (!source.getWorld().isClient) {
			BineClaimsCommandResult result = GuildInterface.claimChunk(source.getPlayer());
			source.sendFeedback(result.getMessage(), true);
			return result.type;
		}
		return 0;
	}

	public static int owner(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		BineClaimsCommandResult result = GuildInterface.getOwner(player.chunkX, player.chunkZ, player.getServerWorld().getRegistryKey());
		context.getSource().sendFeedback(result.getMessage(), false);
		return result.type;
	}

	public static int listGuilds(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		if (!source.getWorld().isClient) {
			Text result = Texts.join(BineClaims.guildManager.getGuildNames(), LiteralText::new);
			source.sendFeedback(result, false);
		}
		return 0;
	}

	public static int guildCreate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String guildName = GuildArgumentType.checkName(context, "name");
		ServerCommandSource source = context.getSource();

		if (!source.getWorld().isClient) {
			BineClaimsCommandResult result = GuildInterface.createGuild(guildName, source.getPlayer());
			source.sendFeedback(result.getMessage(), true);
			return result.type;
		}
		return 0;
	}

	public static int guildJoin(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Guild guild = context.getArgument("guild", Guild.class);
		ServerCommandSource source = context.getSource();

		if (!source.getWorld().isClient) {
			BineClaimsCommandResult result = GuildInterface.joinGuild(guild.name, source.getPlayer());
			source.sendFeedback(result.getMessage(), true);
			return result.type;
		}

		return 0;
	}

	public static int guildLeave(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();

		if (!source.getWorld().isClient) {
			BineClaimsCommandResult result = GuildInterface.leaveGuild(source.getPlayer());
			source.sendFeedback(result.getMessage(), true);
			return result.type;
		}

		return 0;
	}
}
