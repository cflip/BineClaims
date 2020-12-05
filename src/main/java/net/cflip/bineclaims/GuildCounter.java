package net.cflip.bineclaims;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.PersistentState;

public class GuildCounter extends PersistentState {
	public static final String STATE_KEY = "guildcounter";

	private int counter;

	public GuildCounter() {
		super(STATE_KEY);
		setDirty(true);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		counter = tag.getInt("guildCount");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("guildCount", counter);
		return tag;
	}

	public int size() {
		return counter;
	}

	public int getNextGuildId() {
		setDirty(true);
		return counter++;
	}
}
