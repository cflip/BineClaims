package net.cflip.bineclaims.command;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum BineClaimsCommandResult {
	CLAIM_ALREADY_CLAIMED("command.claim.already_claimed", -1),
	CLAIM_NOT_IN_GUILD("command.claim.not_in_guild", -1),
	CLAIM_SUCCESS("command.claim.success", 1),
	OWNER_RESPONSE("command.owner.response", 0),
	OWNER_FAIL("command.owner.fail", -1),
	GUILD_CREATE("command.guild.create", 1),
	GUILD_ALREADY_IN_GUILD("command.guild.already_in_guild", -1);

	public static final int SUCCESS = 1;
	public static final int INFO = 0;
	public static final int FAIL = -1;

	private final String translationKey;
	public final int type;

	private String argument;

	BineClaimsCommandResult(String translationKey, int type) {
		this.translationKey = translationKey;
		this.type = type;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	public Text getMessage() {
		Formatting colour = Formatting.WHITE;
		switch (type) {
			case SUCCESS:
				colour = Formatting.GREEN;
				break;
			case INFO:
				colour = Formatting.YELLOW;
				break;
			case FAIL:
				colour = Formatting.RED;
				break;
		}

		return new TranslatableText(translationKey, argument).formatted(colour);
	}
}
