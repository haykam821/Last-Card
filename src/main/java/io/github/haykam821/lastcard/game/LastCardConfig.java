package io.github.haykam821.lastcard.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

public class LastCardConfig {
	public static final Codec<LastCardConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Identifier.CODEC.fieldOf("map").forGetter(LastCardConfig::getMap),
			PlayerConfig.CODEC.fieldOf("players").forGetter(LastCardConfig::getPlayerConfig),
			IntProvider.POSITIVE_CODEC.optionalFieldOf("initial_hand_count", ConstantIntProvider.create(7)).forGetter(LastCardConfig::getInitialHandCount)
		).apply(instance, LastCardConfig::new);
	});

	private final Identifier map;
	private final PlayerConfig playerConfig;
	private final IntProvider initialHandCount;

	public LastCardConfig(Identifier map, PlayerConfig playerConfig, IntProvider initialHandCount) {
		this.map = map;
		this.playerConfig = playerConfig;
		this.initialHandCount = initialHandCount;
	}

	public Identifier getMap() {
		return this.map;
	}

	public PlayerConfig getPlayerConfig() {
		return this.playerConfig;
	}

	public IntProvider getInitialHandCount() {
		return this.initialHandCount;
	}
}