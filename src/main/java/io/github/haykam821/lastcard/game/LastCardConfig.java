package io.github.haykam821.lastcard.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

public class LastCardConfig {
	public static final Codec<LastCardConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Identifier.CODEC.fieldOf("map").forGetter(LastCardConfig::getMap),
			PlayerConfig.CODEC.fieldOf("players").forGetter(LastCardConfig::getPlayerConfig)
		).apply(instance, LastCardConfig::new);
	});

	private final Identifier map;
	private final PlayerConfig playerConfig;

	public LastCardConfig(Identifier map, PlayerConfig playerConfig) {
		this.map = map;
		this.playerConfig = playerConfig;
	}

	public Identifier getMap() {
		return this.map;
	}

	public PlayerConfig getPlayerConfig() {
		return this.playerConfig;
	}
}