package net.cflip.bineclaims.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;
import java.util.Map;

public class RootCommand {
	private static final Map<String, String> commandHelpMap = new HashMap<>();

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		commandHelpMap.put("guild", "help.guild");

		dispatcher.register(CommandManager.literal("bclaims")
			.then(addCommand(new ClaimCommand()))
			.then(addCommand(new OwnerCommand()))
			.then(addCommand(new ListCommand()))
			.then(CommandManager.literal("guild")
				.then(addCommand(new GuildCreateCommand()))
				.then(addCommand(new GuildDeleteCommand()))
				.then(addCommand(new GuildJoinCommand()))
				.then(addCommand(new GuildLeaveCommand()))
			).then(addCommand(new HelpCommand(commandHelpMap))));
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addCommand(Command command) {
		commandHelpMap.put(command.getName(), command.getHelpTranslationKey());
		return command.addToRegistry();
	}
}
