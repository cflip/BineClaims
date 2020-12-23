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
	GUILD_CREATE_SUCCESS("command.guild.create.success", 1),
	GUILD_CREATE_ALREADY_IN_GUILD("command.guild.create.already_in_guild", -1),
	GUILD_DELETE_SUCCESS("command.guild.delete.success", 1),
	GUILD_DELETE_NOT_OWNER("command.guild.delete.not_owner", -1),
	GUILD_JOIN_SUCCESS("command.guild.join.success", 1),
	GUILD_JOIN_ALREADY_IN_GUILD("command.guild.join.already_in_guild", -1),
	GUILD_LEAVE_SUCCESS("command.guild.leave.success", 1),
	GUILD_LEAVE_NOT_IN_GUILD("command.guild.leave.not_in_guild", -1);

	public static final int SUCCESS = 1;
	public static final int INFO = 0;
	public static final int FAIL = -1;

	private final String translationKey;
	public final int type;

	public String argument;

	BineClaimsCommandResult(String translationKey, int type) {
		this.translationKey = translationKey;
		this.type = type;
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
