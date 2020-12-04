package net.cflip.bineclaims.claim;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum ChunkClaimResult {
	ALREADY_CLAIMED(new TranslatableText("command.claim.already_claimed")),
	SUCCESS(new TranslatableText("command.claim.success"));

	public Text message;

	ChunkClaimResult(Text message) {
		this.message = message;
	}
}
