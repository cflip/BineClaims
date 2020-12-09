package net.cflip.bineclaims.guild;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.UUID;

public class ChunkClaim {
	public final int chunkX;
	public final int chunkZ;
	public final RegistryKey<World> dimension;
	public final UUID owner;

	public ChunkClaim(ServerPlayerEntity player) {
		chunkX = player.chunkX;
		chunkZ = player.chunkZ;
		dimension = player.getEntityWorld().getRegistryKey();
		owner = player.getUuid();
	}

	public ChunkClaim(CompoundTag tag) {
		chunkX = tag.getInt("chunkX");
		chunkZ = tag.getInt("chunkZ");
		owner = tag.getUuid("owner");

		@SuppressWarnings("deprecation")
		DataResult<RegistryKey<World>> dimensionTag = DimensionType.method_28521(new Dynamic<>(NbtOps.INSTANCE, tag.get("dimension")));
		dimension = dimensionTag.getOrThrow(true, s -> {
			throw new IllegalArgumentException("Invalid dimension: " + tag.get("dimension"));
		});

	}

	public boolean isWithinBounds(int chunkX, int chunkZ, RegistryKey<World> dimension) {
		return (chunkX == this.chunkX) && (chunkZ == this.chunkZ) && (this.dimension.equals(dimension));
	}

	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("chunkX", chunkX);
		tag.putInt("chunkZ", chunkZ);
		DataResult<Tag> dimensionId = Identifier.CODEC.encodeStart(NbtOps.INSTANCE, this.dimension.getValue());
		dimensionId.map(dimensionTag -> tag.put("dimension", dimensionTag));
		tag.putUuid("owner", owner);

		return tag;
	}
}
