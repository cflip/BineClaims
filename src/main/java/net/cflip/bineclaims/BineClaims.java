package net.cflip.bineclaims;

import net.cflip.bineclaims.command.ClaimCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class BineClaims implements ModInitializer {
	public static final String MODID = "bineclaims";

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> ClaimCommand.register(dispatcher));
	}
}
