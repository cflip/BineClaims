package net.cflip.bineclaims.claim;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;

import java.util.UUID;

public class ChunkClaimData extends PersistentState {
	public int chunkX, chunkZ;
	public DimensionType dimension;
	public UUID owner;

	public ChunkClaimData(ServerPlayerEntity player) {
		super(createKey(player.chunkX, player.chunkZ));
		chunkX = player.chunkX;
		chunkZ = player.chunkZ;
		dimension = player.world.getDimension();
		owner = player.getUuid();

		setDirty(true);
	}

	public static String createKey(int chunkX, int chunkZ) {
		return chunkX + "_" + chunkZ;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		chunkX = tag.getInt("chunkX");
		chunkZ = tag.getInt("chunkZ");
		owner = tag.getUuid("owner");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("chunkX", chunkX);
		tag.putInt("chunkZ", chunkZ);
		tag.putUuid("owner", owner);

		return tag;
	}
}
