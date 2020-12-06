package net.cflip.bineclaims;

import net.cflip.bineclaims.command.BineClaimsCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class BineClaims implements ModInitializer {
	public static final String MODID = "bineclaims";

	public static GuildManager guildManager;

	@Override
	public void onInitialize() {
		guildManager = new GuildManager();

		AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
			if (!world.isClient) {
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerEntity;
				System.out.println(serverPlayer.getUuidAsString());

				if (guildManager.canInteract(serverPlayer)) {
					return ActionResult.PASS;
				} else {
					return ActionResult.FAIL;
				}
			}
			return ActionResult.PASS;
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> BineClaimsCommand.register(dispatcher));
	}
}
