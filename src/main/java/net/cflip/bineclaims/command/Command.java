package net.cflip.bineclaims.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public interface Command {
	default ArgumentBuilder<ServerCommandSource, ?> addToRegistry() {
		return CommandManager.literal(getName()).executes(this::execute);
	}

	int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
	String getName();

	default String getHelpTranslationKey() {
		return "help." + getName();
	}
}
