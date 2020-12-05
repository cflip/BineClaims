package net.cflip.bineclaims;

import net.cflip.bineclaims.claim.ChunkClaimResult;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuildManager  {
	private final Map<UUID, Guild> playerGuildMap = new HashMap<>();

	public void addGuild(Guild guild, ServerPlayerEntity player) {
		player.getServerWorld().getPersistentStateManager().set(guild);
		playerGuildMap.put(player.getUuid(), guild);
	}

	public ChunkClaimResult claimChunk(ServerPlayerEntity player) {
		Guild guild = BineClaims.guildManager.getGuild(player);

		if (guild == null) {
			return ChunkClaimResult.NOT_IN_GUILD;
		} else {
			player.getServerWorld().getPersistentStateManager().set(guild);
			return guild.claimChunk(player);
		}
	}

	public Guild getGuild(ServerPlayerEntity player) {
		return playerGuildMap.get(player.getUuid());
	}

	public Guild getGuild(UUID playerId) {
		return playerGuildMap.get(playerId);
	}
}
