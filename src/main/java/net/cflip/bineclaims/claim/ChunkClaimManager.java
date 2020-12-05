package net.cflip.bineclaims.claim;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentStateManager;

public class ChunkClaimManager {
	public static ChunkClaimData getClaimData(ServerPlayerEntity player) {
		String key = ChunkClaimData.createKey(player.chunkX, player.chunkZ);
		PersistentStateManager stateManager = player.getServerWorld().getPersistentStateManager();

		return stateManager.get(() -> new ChunkClaimData(player), key);
	}

	public static ChunkClaimResult claim(ServerPlayerEntity player) {
		ChunkClaimData data = getClaimData(player);

		if (data != null) {
			return ChunkClaimResult.ALREADY_CLAIMED;
		} else {
			player.getServerWorld().getPersistentStateManager().set(new ChunkClaimData(player));
			return ChunkClaimResult.SUCCESS;
		}
	}

	public static String getOwner(ServerPlayerEntity player) {
		ChunkClaimData data = getClaimData(player);
		return data == null ? null : data.ownerName;
	}
}
