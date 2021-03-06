package net.cflip.bineclaims;

import net.cflip.bineclaims.command.RootCommand;
import net.cflip.bineclaims.guild.Guild;
import net.cflip.bineclaims.guild.GuildManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

import java.util.Optional;

public class BineClaims implements ModInitializer {
	public static GuildManager guildManager;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> guildManager = new GuildManager(server.getOverworld().getPersistentStateManager()));

		AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
			if (!world.isClient) {
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerEntity;

				if (guildManager.canInteract(serverPlayer, blockPos)) {
					return ActionResult.PASS;
				} else {
					int chunkX = (int) Math.floor(blockPos.getX() / 16f);
					int chunkZ = (int) Math.floor(blockPos.getZ() / 16f);
					Optional<Guild> chunkOwner = guildManager.getGuildByChunk(chunkX, chunkZ, world.getRegistryKey());

					chunkOwner.ifPresent(guild ->
						serverPlayer.sendMessage(new TranslatableText("event.block_break.blocked", guild.name).formatted(Formatting.RED), true));
					return ActionResult.FAIL;
				}
			}
			return ActionResult.PASS;
		});

		UseBlockCallback.EVENT.register((playerEntity, world, hand, blockPos) -> {
			if (!world.isClient) {
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerEntity;

				if (guildManager.canInteract(serverPlayer, blockPos.getBlockPos())) {
					return ActionResult.PASS;
				} else {
					int chunkX = (int) Math.floor(blockPos.getBlockPos().getX() / 16f);
					int chunkZ = (int) Math.floor(blockPos.getBlockPos().getZ() / 16f);
					Optional<Guild> chunkOwner = guildManager.getGuildByChunk(chunkX, chunkZ, world.getRegistryKey());

					chunkOwner.ifPresent(guild ->
						serverPlayer.sendMessage(new TranslatableText("event.block_use.blocked", guild.name).formatted(Formatting.RED), true));

					return ActionResult.FAIL;
				}
			}
			return ActionResult.PASS;
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> RootCommand.register(dispatcher));
	}
}
