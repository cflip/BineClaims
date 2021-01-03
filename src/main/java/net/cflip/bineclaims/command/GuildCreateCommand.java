package net.cflip.bineclaims.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cflip.bineclaims.guild.GuildInterface;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class GuildCreateCommand implements Command {
	@Override
	public ArgumentBuilder<ServerCommandSource, ?> addToRegistry() {
		return CommandManager.literal("create").then(CommandManager.argument("name", StringArgumentType.greedyString()).executes(this::execute));
	}

	@Override
	public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String guildName = GuildArgumentType.checkName(context, "name");
		ServerCommandSource source = context.getSource();

		if (!source.getWorld().isClient) {
			BineClaimsCommandResult result = GuildInterface.createGuild(guildName, source.getPlayer());
			source.sendFeedback(result.getMessage(), true);
			return result.type;
		}

		return 0;
	}

	@Override
	public String getName() {
		return "guild create";
	}

	@Override
	public String getHelpTranslationKey() {
		return "help.guild.create";
	}
}
