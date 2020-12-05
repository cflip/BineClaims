package net.cflip.bineclaims.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cflip.bineclaims.claim.ChunkClaimManager;
import net.cflip.bineclaims.claim.ChunkClaimResult;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class CommandManager {
	public static final String NAME = "bclaims";

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(net.minecraft.server.command.CommandManager.literal(NAME)
			.then(net.minecraft.server.command.CommandManager.literal("claim").executes(CommandManager::claim))
			.then(net.minecraft.server.command.CommandManager.literal("owner").executes(CommandManager::owner))
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
			text = new TranslatableText("command.claim.owner_fail").formatted(Formatting.RED);
		} else {
			text = new TranslatableText("command.claim.owner", name).formatted(Formatting.YELLOW);
		}

		context.getSource().sendFeedback(text, false);
		return 0;
	}
}
