package net.cflip.bineclaims.guild;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Guild extends PersistentState {
	public String name;
	public UUID owner;
	private final List<UUID> members = new ArrayList<>();
	private final List<ChunkClaim> claims = new ArrayList<>();

	public Guild(int id) {
		super("guild_" + id);
		setDirty(true);
	}

	public Guild(String name, int id, ServerPlayerEntity player) {
		super("guild_" + id);
		this.name = name;
		owner = player.getUuid();
		members.add(owner);

		setDirty(true);
	}

	public void claimChunk(ServerPlayerEntity player) {
		if (!hasClaim(player.chunkX, player.chunkZ, player.getServerWorld().getRegistryKey())) {
			claims.add(new ChunkClaim(player));
			setDirty(true);
		}
	}

	public void addMember(ServerPlayerEntity player) {
		members.add(player.getUuid());
		setDirty(true);
	}

	public boolean isMember(ServerPlayerEntity player) {
		return members.contains(player.getUuid());
	}

	public boolean hasClaim(int chunkX, int chunkZ, RegistryKey<World> dimension) {
		return claims.stream().anyMatch(chunk -> chunk.isWithinBounds(chunkX, chunkZ, dimension));
	}

	@Override
	public void fromTag(CompoundTag tag) {
		name = tag.getString("name");
		owner = tag.getUuid("owner");

		ListTag membersTag = tag.getList("members", NbtType.INT_ARRAY);
		for (Tag value : membersTag) {
			members.add(NbtHelper.toUuid(value));
		}

		ListTag claimsTag = tag.getList("claims", NbtType.COMPOUND);
		for (Tag value : claimsTag) {
			ChunkClaim data = new ChunkClaim((CompoundTag) value);
			claims.add(data);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putString("name", name);
		tag.putUuid("owner", owner);

		ListTag membersTag = new ListTag();
		for (int i = 0; i < members.size(); i++) {
			membersTag.add(i, NbtHelper.fromUuid(members.get(i)));
		}

		ListTag claimListTag = new ListTag();
		int i = 0;
		for (ChunkClaim data : claims) {
			claimListTag.add(i++, data.toTag(new CompoundTag()));
		}

		tag.put("members", membersTag);
		tag.put("claims", claimListTag);

		return tag;
	}
}
