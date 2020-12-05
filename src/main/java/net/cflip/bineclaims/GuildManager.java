package net.cflip.bineclaims;

import net.cflip.bineclaims.claim.ChunkClaimResult;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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

	public Text createGuild(String newGuildName, ServerPlayerEntity player) {
		GuildCreateResult result;
		PersistentStateManager stateManager = player.getServerWorld().getPersistentStateManager();
		update(stateManager);

		if (playerGuildMap.containsKey(player.getUuid())) {
			result = GuildCreateResult.ALREADY_IN_GUILD;
			return result.getMessage(playerGuildMap.get(player.getUuid()).name);
		}

		Guild newGuild = new Guild(newGuildName, counter.getNextGuildId(), player);

		stateManager.set(newGuild);
		playerGuildMap.put(player.getUuid(), newGuild);

		result = GuildCreateResult.SUCCESS;
		return result.getMessage(newGuildName);
	}

	public ChunkClaimResult claimChunk(ServerPlayerEntity player) {
		Guild guild = BineClaims.guildManager.getGuild(player);
		update(player.getServerWorld().getPersistentStateManager());

		if (guild == null) {
			return ChunkClaimResult.NOT_IN_GUILD;
		} else {
			guild.setDirty(true);
			for (Guild i : playerGuildMap.values()) {
				System.out.println("Checking if guild " + i.name + " already owns chunk.");
				if (i.hasClaim(player.chunkX, player.chunkZ)) {
					return ChunkClaimResult.ALREADY_CLAIMED;
				}
			}

			return guild.claimChunk(player);
		}
	}

	public Guild getGuild(ServerPlayerEntity player) {
		return playerGuildMap.get(player.getUuid());
	}
}
