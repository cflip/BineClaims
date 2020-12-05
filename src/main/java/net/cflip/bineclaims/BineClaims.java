package net.cflip.bineclaims;

import net.cflip.bineclaims.command.BineClaimsCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class BineClaims implements ModInitializer {
	public static final String MODID = "bineclaims";

	public static GuildManager guildManager;

	@Override
	public void onInitialize() {
		guildManager = new GuildManager();

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> BineClaimsCommand.register(dispatcher));
	}
}
