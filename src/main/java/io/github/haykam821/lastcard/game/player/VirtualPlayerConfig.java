package io.github.haykam821.lastcard.game.player;

import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;

public record VirtualPlayerConfig(
	IntProvider singleplayerCount,
	IntProvider multiplayerCount
) {
	public static final VirtualPlayerConfig DEFAULT = new VirtualPlayerConfig(ConstantIntProvider.create(3), ConstantIntProvider.create(0));

	private static final Codec<VirtualPlayerConfig> RECORD_CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("singleplayer_count", DEFAULT.singleplayerCount()).forGetter(VirtualPlayerConfig::singleplayerCount),
			IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("multiplayer_count", DEFAULT.multiplayerCount()).forGetter(VirtualPlayerConfig::multiplayerCount)
		).apply(instance, VirtualPlayerConfig::new);
	});

	public static final Codec<VirtualPlayerConfig> CODEC = Codec.either(IntProvider.NON_NEGATIVE_CODEC, VirtualPlayerConfig.RECORD_CODEC).xmap(
		either -> either.map(VirtualPlayerConfig::new, Function.identity()),
		config -> config.singleplayerCount().equals(config.multiplayerCount()) ? Either.left(config.singleplayerCount()) : Either.right(config)
	);

	public VirtualPlayerConfig(IntProvider count) {
		this(count, count);
	}

	public int getCount(Random random, boolean singleplayer) {
		IntProvider provider = singleplayer ? this.singleplayerCount() : this.multiplayerCount();
		return provider.get(random);
	}
}
