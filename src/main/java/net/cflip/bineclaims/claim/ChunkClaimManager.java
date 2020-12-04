package net.cflip.bineclaims.claim;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class ChunkClaimManager {
	public static Map<String, ChunkClaimData> dataMap = new HashMap<>();

	public static ChunkClaimResult claim(ServerPlayerEntity player) {
		ChunkClaimData data = dataMap.get(createKey(player.chunkX, player.chunkZ));

		if (data != null) {
			return ChunkClaimResult.ALREADY_CLAIMED;
		} else {
			dataMap.put(createKey(player.chunkX, player.chunkZ), new ChunkClaimData(player));
			return ChunkClaimResult.SUCCESS;
		}
	}

	public static String createKey(int chunkX, int chunkZ) {
		return chunkX + ":" + chunkZ;
	}
}
