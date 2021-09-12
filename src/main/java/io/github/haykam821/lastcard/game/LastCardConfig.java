package io.github.haykam821.lastcard.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.haykam821.lastcard.game.map.LastCardMapConfig;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

public class LastCardConfig {
	public static final Codec<LastCardConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			LastCardMapConfig.CODEC.fieldOf("map").forGetter(LastCardConfig::getMapConfig),
			PlayerConfig.CODEC.fieldOf("players").forGetter(LastCardConfig::getPlayerConfig)
		).apply(instance, LastCardConfig::new);
	});

	private final LastCardMapConfig mapConfig;
	private final PlayerConfig playerConfig;

	public LastCardConfig(LastCardMapConfig mapConfig, PlayerConfig playerConfig) {
		this.mapConfig = mapConfig;
		this.playerConfig = playerConfig;
	}

	public LastCardMapConfig getMapConfig() {
		return this.mapConfig;
	}

	public PlayerConfig getPlayerConfig() {
		return this.playerConfig;
	}
}