package net.cflip.bineclaims.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

import java.util.Map;

public class HelpCommand implements Command {
	private final Map<String, String> commandHelpMap;

	public HelpCommand(Map<String, String> commandHelpMap) {
		this.commandHelpMap = commandHelpMap;
	}

	@Override
	public ArgumentBuilder<ServerCommandSource, ?> addToRegistry() {
		return CommandManager.literal(getName()).then(CommandManager.argument("command", StringArgumentType.greedyString()).executes(this::execute)).executes(this::executeNoCommand);
	}

	@Override
	public int execute(CommandContext<ServerCommandSource> context) {
		String argument = context.getArgument("command", String.class);
		if (commandHelpMap.containsKey(argument)) {
			TranslatableText desc = new TranslatableText("command.help.command_info", "/bclaims " + argument);
			context.getSource().sendFeedback(desc.append(new TranslatableText(commandHelpMap.get(argument))), false);
		} else {
			Text commands = Texts.join(commandHelpMap.keySet(), Text::of);
			context.getSource().sendFeedback(new TranslatableText("command.help.unknown", commands), false);
		}
		return 0;
	}

	public int executeNoCommand(CommandContext<ServerCommandSource> context) {
		Text commands = Texts.join(commandHelpMap.keySet(), Text::of);
		context.getSource().sendFeedback(new TranslatableText("command.help.main", commands), false);
		return 0;
	}

	@Override
	public String getName() {
		return "help";
	}
}
