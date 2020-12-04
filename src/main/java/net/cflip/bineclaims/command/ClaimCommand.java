package net.cflip.bineclaims.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cflip.bineclaims.claim.ChunkClaimManager;
import net.cflip.bineclaims.claim.ChunkClaimResult;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ClaimCommand  {
	public static final String NAME = "claim";

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal(NAME).executes(ClaimCommand::execute));
	}

	public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		if (!source.getWorld().isClient) {
			ChunkClaimResult result = ChunkClaimManager.claim(source.getPlayer());
			source.sendFeedback(result.message, true);
		}
		return 0;
	}
}
