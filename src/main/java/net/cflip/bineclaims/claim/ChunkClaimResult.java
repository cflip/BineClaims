package net.cflip.bineclaims.claim;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum ChunkClaimResult {
	ALREADY_CLAIMED(new TranslatableText("command.claim.already_claimed").formatted(Formatting.RED)),
	SUCCESS(new TranslatableText("command.claim.success").formatted(Formatting.GREEN));

	public Text message;

	ChunkClaimResult(Text message) {
		this.message = message;
	}
}
