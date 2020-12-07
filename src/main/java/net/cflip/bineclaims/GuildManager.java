package net.cflip.bineclaims;

import net.cflip.bineclaims.command.BineClaimsCommandResult;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
			BineClaimsCommandResult.GUILD_CREATE_ALREADY_IN_GUILD.setArgument(playerGuildMap.get(player.getUuid()).name);
			return BineClaimsCommandResult.GUILD_CREATE_ALREADY_IN_GUILD;
		}

		Guild newGuild = new Guild(newGuildName, counter.getNextGuildId(), player);

		stateManager.set(newGuild);
		playerGuildMap.put(player.getUuid(), newGuild);

		BineClaimsCommandResult.GUILD_CREATE_SUCCESS.setArgument(newGuildName);
		return BineClaimsCommandResult.GUILD_CREATE_SUCCESS;
	}

	public BineClaimsCommandResult joinGuild(String guildName, ServerPlayerEntity player) {
		update(player.getServerWorld().getPersistentStateManager());

		// TODO: Replace loops with stream API function
		for (Guild guild : playerGuildMap.values()) {
			if (guild.members.contains(player.getUuid())) {
				if (!guild.name.equals(guildName)) {
					BineClaimsCommandResult.GUILD_JOIN_ALREADY_IN_GUILD.setArgument(guild.name);
					return BineClaimsCommandResult.GUILD_JOIN_ALREADY_IN_GUILD;
				}
			}
		}

		getGuildByName(guildName).ifPresent(guild -> guild.addMember(player));
		BineClaimsCommandResult.GUILD_JOIN_SUCCESS.setArgument(guildName);
		return BineClaimsCommandResult.GUILD_JOIN_SUCCESS;
	}

	public BineClaimsCommandResult claimChunk(ServerPlayerEntity player) {
		Guild guild = BineClaims.guildManager.getGuildByPlayer(player);
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

	public Guild getGuildByPlayer(ServerPlayerEntity player) {
		return playerGuildMap.get(player.getUuid());
	}

	public Optional<Guild> getGuildByName(String name) {
		return playerGuildMap.values()
			.stream()
			.filter(guild -> guild.name.equals(name))
			.findFirst();
	}

	public List<String> getGuildNames() {
		return playerGuildMap.values()
			.stream()
			.filter(Objects::nonNull)
			.map(guild -> guild.name)
			.collect(Collectors.toList());
	}
}
