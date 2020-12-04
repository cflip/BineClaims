package net.cflip.bineclaims.claim;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkClaimData implements Serializable {
	public int chunkX, chunkZ;
	public DimensionType dimension;
	public UUID owner;
	public List<UUID> members = new ArrayList<>();

	public ChunkClaimData(ServerPlayerEntity player) {
		chunkX = player.chunkX;
		chunkZ = player.chunkZ;
		dimension = player.world.getDimension();
		owner = player.getUuid();
	}
}
