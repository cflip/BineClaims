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

import java.util.ArrayList;
import java.util.List;

public class BineClaimsCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("bclaims")
			.then(CommandManager.literal("help").executes(BineClaimsCommand::help))
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
		// TODO: Just trying to get this function implemented for now, eventually I'll do this in a fancy OOP way.
		List<Text> commandList = new ArrayList<>();
		commandList.add(Text.of("help"));
		commandList.add(Text.of("claim"));
		commandList.add(Text.of("owner"));
		commandList.add(Text.of("guild create"));
		commandList.add(Text.of("guild join"));
		commandList.add(Text.of("guild leave"));

		Text commands = Texts.join(commandList, text -> text);

		context.getSource().sendFeedback(new TranslatableText("command.help.main", commands), false);
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
