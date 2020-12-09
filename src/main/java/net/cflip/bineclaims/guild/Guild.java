package net.cflip.bineclaims.guild;

import net.cflip.bineclaims.command.BineClaimsCommandResult;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Guild extends PersistentState {
	public String name;
	public UUID owner;
	private final List<UUID> members = new ArrayList<>();

	private final Map<String, ChunkClaimData> claimDataList = new HashMap<>();

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

	public BineClaimsCommandResult claimChunk(ServerPlayerEntity player) {
		ChunkClaimData data = claimDataList.get(getChunkKey(player.chunkX, player.chunkZ));

		// TODO: It might be better if BineClaimsCommandResult was not returned here
		if (data != null) {
			return BineClaimsCommandResult.CLAIM_ALREADY_CLAIMED;
		} else {
			claimDataList.put(getChunkKey(player.chunkX, player.chunkZ), new ChunkClaimData(player));
			setDirty(true);
			return BineClaimsCommandResult.CLAIM_SUCCESS;
		}
	}

	public void addMember(ServerPlayerEntity player) {
		members.add(player.getUuid());
		setDirty(true);
	}

	public boolean isMember(ServerPlayerEntity player) {
		return members.contains(player.getUuid());
	}

	public boolean hasClaim(int chunkX, int chunkZ) {
		return claimDataList.containsKey(getChunkKey(chunkX, chunkZ));
	}

	private static String getChunkKey(int chunkX, int chunkZ) {
		return chunkX + ":" + chunkZ;
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
			ChunkClaimData data = new ChunkClaimData((CompoundTag) value);
			claimDataList.put(getChunkKey(data.chunkX, data.chunkZ), data);
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
		for (ChunkClaimData data : claimDataList.values()) {
			claimListTag.add(i++, data.toTag(new CompoundTag()));
		}

		tag.put("members", membersTag);
		tag.put("claims", claimListTag);

		return tag;
	}
}
