package net.cflip.bineclaims.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cflip.bineclaims.BineClaims;
import net.cflip.bineclaims.claim.ChunkClaimManager;
import net.cflip.bineclaims.claim.ChunkClaimResult;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class BineClaimsCommand {
	public static final String NAME = "bclaims";

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(net.minecraft.server.command.CommandManager.literal(NAME)
			.then(net.minecraft.server.command.CommandManager.literal("claim").executes(BineClaimsCommand::claim))
			.then(net.minecraft.server.command.CommandManager.literal("owner").executes(BineClaimsCommand::owner))
			.then(net.minecraft.server.command.CommandManager.literal("guild").then(CommandManager.argument("guildName", StringReader::readString).executes(BineClaimsCommand::guildCreate)))
		);
	}

	public static int claim(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		if (!source.getWorld().isClient) {
			ChunkClaimResult result = ChunkClaimManager.claim(source.getPlayer());
			source.sendFeedback(result.message, true);
		}
		return 0;
	}

	public static int owner(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String name = ChunkClaimManager.getOwner(context.getSource().getPlayer());
		Text text;

		if (name == null) {
			text = new TranslatableText("command.owner.fail").formatted(Formatting.RED);
		} else {
			text = new TranslatableText("command.owner.response", name).formatted(Formatting.YELLOW);
		}

		context.getSource().sendFeedback(text, false);
		return 0;
	}

	public static int guildCreate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String guildName = context.getArgument("guildName", String.class);
		ServerPlayerEntity player = context.getSource().getPlayer();

		Text result = BineClaims.guildManager.createGuild(guildName, player);
		context.getSource().sendFeedback(result, false);
		return 0;
	}
}
