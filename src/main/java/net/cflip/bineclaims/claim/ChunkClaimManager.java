package net.cflip.bineclaims.claim;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentStateManager;

public class ChunkClaimManager {
	public static ChunkClaimResult claim(ServerPlayerEntity player) {
		String key = ChunkClaimData.createKey(player.chunkX, player.chunkZ);
		PersistentStateManager stateManager = player.getServerWorld().getPersistentStateManager();

		ChunkClaimData data = stateManager.get(() -> new ChunkClaimData(player), key);

		if (data != null) {
			return ChunkClaimResult.ALREADY_CLAIMED;
		} else {
			stateManager.set(new ChunkClaimData(player));
			return ChunkClaimResult.SUCCESS;
		}
	}
}
