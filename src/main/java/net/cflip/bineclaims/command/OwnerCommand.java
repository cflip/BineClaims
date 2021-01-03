package net.cflip.bineclaims.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cflip.bineclaims.guild.GuildInterface;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class OwnerCommand implements Command {
	@Override
	public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		BineClaimsCommandResult result = GuildInterface.getOwner(player.chunkX, player.chunkZ, player.getServerWorld().getRegistryKey());
		context.getSource().sendFeedback(result.getMessage(), false);
		return result.type;
	}

	@Override
	public String getName() {
		return "owner";
	}
}
