package net.cflip.bineclaims.guild;

import net.cflip.bineclaims.BineClaims;
import net.cflip.bineclaims.command.BineClaimsCommandResult;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class GuildInterface {
	public static BineClaimsCommandResult createGuild(String newGuildName, ServerPlayerEntity player) {
		Optional<Guild> playerGuild = BineClaims.guildManager.getGuildByPlayer(player);

		if (playerGuild.isPresent()) {
			BineClaimsCommandResult.GUILD_CREATE_ALREADY_IN_GUILD.argument = playerGuild.get().name;
			return BineClaimsCommandResult.GUILD_CREATE_ALREADY_IN_GUILD;
		}

		BineClaims.guildManager.createGuild(newGuildName, player);

		BineClaimsCommandResult.GUILD_CREATE_SUCCESS.argument = newGuildName;
		return BineClaimsCommandResult.GUILD_CREATE_SUCCESS;
	}

	public static BineClaimsCommandResult joinGuild(String guildName, ServerPlayerEntity player) {
		Optional<Guild> playerGuild = BineClaims.guildManager.getGuildByPlayer(player);

		if (playerGuild.isPresent()) {
			BineClaimsCommandResult.GUILD_JOIN_ALREADY_IN_GUILD.argument = playerGuild.get().name;
			return BineClaimsCommandResult.GUILD_JOIN_ALREADY_IN_GUILD;
		}

		BineClaims.guildManager.getGuildByName(guildName).ifPresent(guild -> guild.addMember(player));
		BineClaimsCommandResult.GUILD_JOIN_SUCCESS.argument = guildName;
		return BineClaimsCommandResult.GUILD_JOIN_SUCCESS;
	}

	public static BineClaimsCommandResult claimChunk(ServerPlayerEntity player) {
		if (BineClaims.guildManager.hasClaim(player.chunkX, player.chunkZ)) {
			return BineClaimsCommandResult.CLAIM_ALREADY_CLAIMED;
		}

		Optional<Guild> guild = BineClaims.guildManager.getGuildByPlayer(player);
		if (guild.isPresent()) {
			guild.get().claimChunk(player);
			return BineClaimsCommandResult.CLAIM_SUCCESS;
		} else {
			return BineClaimsCommandResult.CLAIM_NOT_IN_GUILD;
		}
	}

	public static BineClaimsCommandResult getOwner(int chunkX, int chunkZ) {
		Optional<Guild> chunkOwner = BineClaims.guildManager.getGuildByChunk(chunkX, chunkZ);

		if (chunkOwner.isPresent()) {
			BineClaimsCommandResult.OWNER_RESPONSE.argument = chunkOwner.get().name;
			return BineClaimsCommandResult.OWNER_RESPONSE;
		}

		return BineClaimsCommandResult.OWNER_FAIL;
	}
}
