package net.cflip.bineclaims;

import net.cflip.bineclaims.command.BineClaimsCommandResult;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuildManager {
	private final Map<UUID, Guild> playerGuildMap = new HashMap<>();
	private GuildCounter counter;

	private void update(PersistentStateManager stateManager) {
		counter = stateManager.getOrCreate(GuildCounter::new, GuildCounter.STATE_KEY);

		for (int i = 0; i < counter.size(); i++) {
			int ii = i;
			Guild guild = stateManager.get(() -> new Guild(ii), "guild_" + i);

			if (guild == null) continue;
			playerGuildMap.put(guild.owner, guild);
		}
	}

	public BineClaimsCommandResult createGuild(String newGuildName, ServerPlayerEntity player) {
		PersistentStateManager stateManager = player.getServerWorld().getPersistentStateManager();
		update(stateManager);

		if (playerGuildMap.containsKey(player.getUuid())) {
			BineClaimsCommandResult.GUILD_ALREADY_IN_GUILD.setArgument(playerGuildMap.get(player.getUuid()).name);
			return BineClaimsCommandResult.GUILD_ALREADY_IN_GUILD;
		}

		Guild newGuild = new Guild(newGuildName, counter.getNextGuildId(), player);

		stateManager.set(newGuild);
		playerGuildMap.put(player.getUuid(), newGuild);

		BineClaimsCommandResult.GUILD_CREATE.setArgument(newGuildName);
		return BineClaimsCommandResult.GUILD_CREATE;
	}

	public BineClaimsCommandResult claimChunk(ServerPlayerEntity player) {
		Guild guild = BineClaims.guildManager.getGuild(player);
		update(player.getServerWorld().getPersistentStateManager());

		if (guild == null) {
			return BineClaimsCommandResult.CLAIM_NOT_IN_GUILD;
		} else {
			guild.setDirty(true);
			for (Guild i : playerGuildMap.values()) {
				if (i.hasClaim(player.chunkX, player.chunkZ)) {
					return BineClaimsCommandResult.CLAIM_ALREADY_CLAIMED;
				}
			}

			return guild.claimChunk(player);
		}
	}

	public boolean canInteract(ServerPlayerEntity player) {
		update(player.getServerWorld().getPersistentStateManager());

		for (Guild i : playerGuildMap.values()) {
			if (i.hasClaim(player.chunkX, player.chunkZ) && !(i.owner.compareTo(player.getUuid()) == 0)) {
				return false;
			}
		}

		return true;
	}

	public BineClaimsCommandResult getOwner(ServerPlayerEntity player) {
		update(player.getServerWorld().getPersistentStateManager());

		for (Guild i : playerGuildMap.values()) {
			if (i.hasClaim(player.chunkX, player.chunkZ)) {
				BineClaimsCommandResult.OWNER_RESPONSE.setArgument(i.name);
				return BineClaimsCommandResult.OWNER_RESPONSE;
			}
		}
		return BineClaimsCommandResult.OWNER_FAIL;
	}

	public Guild getGuild(ServerPlayerEntity player) {
		return playerGuildMap.get(player.getUuid());
	}
}
