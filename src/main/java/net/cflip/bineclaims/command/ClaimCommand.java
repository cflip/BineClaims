package net.cflip.bineclaims.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cflip.bineclaims.guild.GuildInterface;
import net.minecraft.server.command.ServerCommandSource;

public class ClaimCommand implements Command {
	@Override
	public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		if (!source.getWorld().isClient) {
			BineClaimsCommandResult result = GuildInterface.claimChunk(source.getPlayer());
			source.sendFeedback(result.getMessage(), true);
			return result.type;
		}
		return 0;
	}

	@Override
	public String getName() {
		return "claim";
	}
}
