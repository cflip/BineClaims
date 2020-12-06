package net.cflip.bineclaims.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cflip.bineclaims.BineClaims;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class BineClaimsCommand {
	public static final String NAME = "bclaims";

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal(NAME)
			.then(CommandManager.literal("claim").executes(BineClaimsCommand::claim))
			.then(CommandManager.literal("owner").executes(BineClaimsCommand::owner))
			.then(CommandManager.literal("guild").then(CommandManager.argument("guildName", StringReader::readQuotedString).executes(BineClaimsCommand::guildCreate)))
		);
	}

	public static int claim(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		if (!source.getWorld().isClient) {
			BineClaimsCommandResult result = BineClaims.guildManager.claimChunk(source.getPlayer());
			source.sendFeedback(result.getMessage(), true);
			return result.type;
		}
		return 0;
	}

	public static int owner(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		BineClaimsCommandResult result = BineClaims.guildManager.getOwner(context.getSource().getPlayer());
		context.getSource().sendFeedback(result.getMessage(), false);
		return result.type;
	}

	public static int guildCreate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String guildName = context.getArgument("guildName", String.class);
		ServerCommandSource source = context.getSource();

		if (!source.getWorld().isClient) {
			BineClaimsCommandResult result = BineClaims.guildManager.createGuild(guildName, source.getPlayer());
			source.sendFeedback(result.getMessage(), true);
			return result.type;
		}
		return 0;
	}
}