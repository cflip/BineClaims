package net.cflip.bineclaims.claim;

import net.cflip.bineclaims.BineClaims;
import net.cflip.bineclaims.command.BineClaimsCommandResult;
import net.minecraft.server.network.ServerPlayerEntity;

public class ChunkClaimManager {
	public static BineClaimsCommandResult claim(ServerPlayerEntity player) {
		return BineClaims.guildManager.claimChunk(player);
	}

	public static BineClaimsCommandResult getOwner(ServerPlayerEntity player) {
		// TODO: Remove ChunkClaimManager and re-implement owner command
		return BineClaimsCommandResult.OWNER_FAIL;
	}
}
