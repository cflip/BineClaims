package net.cflip.bineclaims;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum GuildCreateResult {
	ALREADY_IN_GUILD("command.guild.already_in_guild", false),
	SUCCESS("command.guild.create", true);

	private final String messageHandle;
	public final boolean success;

	GuildCreateResult(String messageHandle, boolean success) {
		this.messageHandle = messageHandle;
		this.success = success;
	}

	public Text getMessage(String guildName) {
		return new TranslatableText(messageHandle, guildName).formatted(success ? Formatting.GREEN : Formatting.RED);
	}
}
