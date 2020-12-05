package net.cflip.bineclaims.claim;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.cflip.bineclaims.BineClaims;
import net.cflip.bineclaims.Guild;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.UUID;

public class ChunkClaimData {
	public final int chunkX;
	public final int chunkZ;
	public final RegistryKey<World> dimension;
	public final Guild guild;
	public final UUID owner;

	public ChunkClaimData(ServerPlayerEntity player) {
		chunkX = player.chunkX;
		chunkZ = player.chunkZ;
		dimension = player.getEntityWorld().getRegistryKey();
		owner = player.getUuid();
		guild = BineClaims.guildManager.getGuild(player);
	}

	public ChunkClaimData(CompoundTag tag) {
		chunkX = tag.getInt("chunkX");
		chunkZ = tag.getInt("chunkZ");
		owner = tag.getUuid("owner");

		@SuppressWarnings("deprecation")
		DataResult<RegistryKey<World>> dimensionTag = DimensionType.method_28521(new Dynamic<>(NbtOps.INSTANCE, tag.get("dimension")));
		dimension = dimensionTag.getOrThrow(true, s -> {
			throw new IllegalArgumentException("Invalid map dimension: " + tag.get("dimension"));
		});

		guild = BineClaims.guildManager.getGuild(owner);
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
