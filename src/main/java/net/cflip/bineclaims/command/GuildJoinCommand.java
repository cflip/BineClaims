package net.cflip.bineclaims.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cflip.bineclaims.guild.Guild;
import net.cflip.bineclaims.guild.GuildInterface;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class GuildJoinCommand implements Command {
	@Override
	public ArgumentBuilder<ServerCommandSource, ?> addToRegistry() {
		return CommandManager.literal("join").then(CommandManager.argument("guild", new GuildArgumentType()).executes(this::execute));
	}

	@Override
	public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Guild guild = context.getArgument("guild", Guild.class);
		ServerCommandSource source = context.getSource();

		if (!source.getWorld().isClient) {
			BineClaimsCommandResult result = GuildInterface.joinGuild(guild.name, source.getPlayer());
			source.sendFeedback(result.getMessage(), true);
			return result.type;
		}

		return 0;
	}

	@Override
	public String getName() {
		return "guild join";
	}

	@Override
	public String getHelpTranslationKey() {
		return "help.guild.join";
	}
}
