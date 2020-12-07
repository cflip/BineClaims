package net.cflip.bineclaims.guild;

import net.cflip.bineclaims.BineClaims;
import net.cflip.bineclaims.command.BineClaimsCommandResult;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentStateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GuildManager {
	private final List<Guild> guilds = new ArrayList<>();
	private final GuildCounter counter;

	public GuildManager(PersistentStateManager stateManager) {
		counter = stateManager.getOrCreate(GuildCounter::new, GuildCounter.STATE_KEY);

		IntStream.range(0, counter.size()).forEach(i -> {
			Guild guild = stateManager.get(() -> new Guild(i), "guild_" + i);
			if (guild == null) return;
			guilds.add(guild);
		});
	}

	public BineClaimsCommandResult createGuild(String newGuildName, ServerPlayerEntity player) {
		if (getGuildByPlayer(player).isPresent()) {
			BineClaimsCommandResult.GUILD_CREATE_ALREADY_IN_GUILD.setArgument(getGuildByPlayer(player).get().name);
			return BineClaimsCommandResult.GUILD_CREATE_ALREADY_IN_GUILD;
		}

		Guild newGuild = new Guild(newGuildName, counter.getNextGuildId(), player);
		player.getServerWorld().getPersistentStateManager().set(newGuild);
		guilds.add(newGuild);

		BineClaimsCommandResult.GUILD_CREATE_SUCCESS.setArgument(newGuildName);
		return BineClaimsCommandResult.GUILD_CREATE_SUCCESS;
	}

	public BineClaimsCommandResult joinGuild(String guildName, ServerPlayerEntity player) {
		// TODO: Replace loops with stream API function
		for (Guild guild : guilds) {
			if (guild.isMember(player)) {
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
		Optional<Guild> guild = BineClaims.guildManager.getGuildByPlayer(player);

		if (guild.isPresent()) {
			for (Guild i : guilds) {
				if (i.hasClaim(player.chunkX, player.chunkZ)) {
					return BineClaimsCommandResult.CLAIM_ALREADY_CLAIMED;
				}
			}

			return guild.get().claimChunk(player);
		} else {
			return BineClaimsCommandResult.CLAIM_NOT_IN_GUILD;
		}
	}

	public boolean canInteract(ServerPlayerEntity player) {
		for (Guild i : guilds) {
			if (i.hasClaim(player.chunkX, player.chunkZ) && !i.isMember(player)) {
				return false;
			}
		}
		return true;
	}

	public BineClaimsCommandResult getOwner(ServerPlayerEntity player) {
		for (Guild i : guilds) {
			if (i.hasClaim(player.chunkX, player.chunkZ)) {
				BineClaimsCommandResult.OWNER_RESPONSE.setArgument(i.name);
				return BineClaimsCommandResult.OWNER_RESPONSE;
			}
		}

		return BineClaimsCommandResult.OWNER_FAIL;
	}

	public Optional<Guild> getGuildByPlayer(ServerPlayerEntity player) {
		return guilds.stream().filter(guild -> guild.isMember(player)).findFirst();
	}

	public Optional<Guild> getGuildByName(String name) {
		return guilds.stream().filter(guild -> guild.name.equals(name)).findFirst();
	}

	public List<String> getGuildNames() {
		return guilds.stream().filter(Objects::nonNull).map(guild -> guild.name).collect(Collectors.toList());
	}
}
