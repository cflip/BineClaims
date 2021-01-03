package net.cflip.bineclaims.command;

import com.mojang.brigadier.context.CommandContext;
import net.cflip.bineclaims.BineClaims;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class ListCommand implements Command {
	@Override
	public int execute(CommandContext<ServerCommandSource> context) {
		Text result = Texts.join(BineClaims.guildManager.getGuildNames(), LiteralText::new);
		context.getSource().sendFeedback(result, false);
		return 0;
	}

	@Override
	public String getName() {
		return "list";
	}
}
