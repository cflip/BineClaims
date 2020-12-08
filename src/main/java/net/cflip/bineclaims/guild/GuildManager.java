package net.cflip.bineclaims.guild;

import net.cflip.bineclaims.BineClaims;
import net.cflip.bineclaims.command.BineClaimsCommandResult;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
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
			BineClaimsCommandResult.GUILD_CREATE_ALREADY_IN_GUILD.argument = getGuildByPlayer(player).get().name;
			return BineClaimsCommandResult.GUILD_CREATE_ALREADY_IN_GUILD;
		}

		Guild newGuild = new Guild(newGuildName, counter.getNextGuildId(), player);
		player.getServerWorld().getPersistentStateManager().set(newGuild);
		guilds.add(newGuild);

		BineClaimsCommandResult.GUILD_CREATE_SUCCESS.argument = newGuildName;
		return BineClaimsCommandResult.GUILD_CREATE_SUCCESS;
	}

	public BineClaimsCommandResult joinGuild(String guildName, ServerPlayerEntity player) {
		// TODO: Replace loops with stream API function
		for (Guild guild : guilds) {
			if (guild.isMember(player)) {
				if (!guild.name.equals(guildName)) {
					BineClaimsCommandResult.GUILD_JOIN_ALREADY_IN_GUILD.argument = guild.name;
					return BineClaimsCommandResult.GUILD_JOIN_ALREADY_IN_GUILD;
				}
			}
		}

		getGuildByName(guildName).ifPresent(guild -> guild.addMember(player));
		BineClaimsCommandResult.GUILD_JOIN_SUCCESS.argument = guildName;
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

	public boolean canInteract(ServerPlayerEntity player, BlockPos blockPos) {
		for (Guild i : guilds) {
			int chunkX = (int) Math.floor(blockPos.getX() / 16f);
			int chunkZ = (int) Math.floor(blockPos.getZ() / 16f);

			if (i.hasClaim(chunkX, chunkZ) && !i.isMember(player)) {
				return false;
			}
		}
		return true;
	}

	public BineClaimsCommandResult getOwner(int chunkX, int chunkZ) {
		for (Guild i : guilds) {
			if (i.hasClaim(chunkX, chunkZ)) {
				BineClaimsCommandResult.OWNER_RESPONSE.argument = i.name;
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
