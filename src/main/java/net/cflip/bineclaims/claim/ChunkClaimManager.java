package net.cflip.bineclaims.claim;

import net.cflip.bineclaims.BineClaims;
import net.minecraft.server.network.ServerPlayerEntity;

public class ChunkClaimManager {
	public static ChunkClaimResult claim(ServerPlayerEntity player) {
		return BineClaims.guildManager.claimChunk(player);
	}

	public static String getOwner(ServerPlayerEntity player) {
		// TODO: Remove ChunkClaimManager and re-implement owner command
		return "TBD";
	}
}
