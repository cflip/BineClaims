package net.cflip.bineclaims.claim;

import net.minecraft.text.Text;

public enum ChunkClaimResult {
	ALREADY_CLAIMED(Text.of("This chunk has already been claimed by you or another player")),
	SUCCESS(Text.of("Chunk successfully claimed"));

	public Text message;

	ChunkClaimResult(Text message) {
		this.message = message;
	}
}
