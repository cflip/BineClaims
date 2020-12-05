package net.cflip.bineclaims;

import net.cflip.bineclaims.claim.ChunkClaimData;
import net.cflip.bineclaims.claim.ChunkClaimResult;
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
	public final List<UUID> members = new ArrayList<>();

	private final Map<String, ChunkClaimData> claimDataList = new HashMap<>();

	public Guild(String name, ServerPlayerEntity player) {
		super(name);
		this.name = name;
		owner = player.getUuid();
		members.add(owner);

		setDirty(true);
	}

	public ChunkClaimResult claimChunk(ServerPlayerEntity player) {
		ChunkClaimData data = claimDataList.get(getChunkKey(player.chunkX, player.chunkZ));

		if (data != null) {
			return ChunkClaimResult.ALREADY_CLAIMED;
		} else {
			claimDataList.put(getChunkKey(player.chunkX, player.chunkZ), new ChunkClaimData(player));
			return ChunkClaimResult.SUCCESS;
		}
	}

	private static String getChunkKey(int chunkX, int chunkZ) {
		return chunkX + ":" + chunkZ;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		name = tag.getString("name");
		owner = tag.getUuid("owner");

		ListTag membersTag = tag.getList("members", 9);
		for (Tag value : membersTag) {
			members.add(NbtHelper.toUuid(value));
		}

		ListTag claimsTag = tag.getList("claims", 9);
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
