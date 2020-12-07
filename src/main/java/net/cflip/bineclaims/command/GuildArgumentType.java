package net.cflip.bineclaims.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.cflip.bineclaims.BineClaims;
import net.cflip.bineclaims.guild.Guild;
import net.minecraft.text.TranslatableText;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GuildArgumentType implements ArgumentType<Guild> {
	private static final DynamicCommandExceptionType UNKNOWN_GUILD_EXCEPTION =
		new DynamicCommandExceptionType((object) -> new TranslatableText("arguments.guild.not_found", object));

	@Override
	public Guild parse(StringReader reader) throws CommandSyntaxException {
		StringBuilder builder = new StringBuilder();

		while (reader.canRead()) {
			builder.append(reader.read());
		}

		String name = builder.toString();
		Optional<Guild> result = BineClaims.guildManager.getGuildByName(name);

		return result.orElse(result.orElseThrow(() -> UNKNOWN_GUILD_EXCEPTION.create(name)));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		BineClaims.guildManager.getGuildNames().forEach(builder::suggest);
		return builder.buildFuture();
	}
}
