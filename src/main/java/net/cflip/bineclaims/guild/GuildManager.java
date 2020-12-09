package net.cflip.bineclaims.guild;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

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

	public void createGuild(String name, ServerPlayerEntity owner) {
		Guild newGuild = new Guild(name, counter.getNextGuildId(), owner);
		owner.getServerWorld().getPersistentStateManager().set(newGuild);
		guilds.add(newGuild);
	}

	public boolean hasClaim(int chunkX, int chunkZ, RegistryKey<World> dimension) {
		return guilds.stream().anyMatch(guild -> guild.hasClaim(chunkX, chunkZ, dimension));
	}

	public boolean canInteract(ServerPlayerEntity player, BlockPos blockPos) {
		for (Guild i : guilds) {
			int chunkX = (int) Math.floor(blockPos.getX() / 16f);
			int chunkZ = (int) Math.floor(blockPos.getZ() / 16f);

			if (i.hasClaim(chunkX, chunkZ, player.getServerWorld().getRegistryKey()) && !i.isMember(player)) {
				return false;
			}
		}
		return true;
	}

	public Optional<Guild> getGuildByPlayer(ServerPlayerEntity player) {
		return guilds.stream().filter(guild -> guild.isMember(player)).findFirst();
	}

	public Optional<Guild> getGuildByName(String name) {
		return guilds.stream().filter(guild -> guild.name.equals(name)).findFirst();
	}

	public Optional<Guild> getGuildByChunk(int chunkX, int chunkZ, RegistryKey<World> dimension) {
		return guilds.stream().filter(guild -> guild.hasClaim(chunkX, chunkZ, dimension)).findFirst();
	}

	public List<String> getGuildNames() {
		return guilds.stream().filter(Objects::nonNull).map(guild -> guild.name).collect(Collectors.toList());
	}
}
