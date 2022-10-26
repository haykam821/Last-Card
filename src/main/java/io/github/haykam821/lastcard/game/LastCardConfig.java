package io.github.haykam821.lastcard.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

public class LastCardConfig {
	public static final Codec<LastCardConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Identifier.CODEC.fieldOf("map").forGetter(LastCardConfig::getMap),
			PlayerConfig.CODEC.fieldOf("players").forGetter(LastCardConfig::getPlayerConfig),
			IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("virtual_players", ConstantIntProvider.create(0)).forGetter(LastCardConfig::getVirtualPlayers),
			IntProvider.POSITIVE_CODEC.optionalFieldOf("initial_hand_count", ConstantIntProvider.create(7)).forGetter(LastCardConfig::getInitialHandCount),
			IntProvider.POSITIVE_CODEC.optionalFieldOf("time_of_day", ConstantIntProvider.create(6000)).forGetter(LastCardConfig::getTimeOfDay),
			BlockStateProvider.TYPE_CODEC.optionalFieldOf("chair_block", BlockStateProvider.of(Blocks.DARK_OAK_STAIRS)).forGetter(LastCardConfig::getChairBlock)
		).apply(instance, LastCardConfig::new);
	});

	private final Identifier map;
	private final PlayerConfig playerConfig;
	private final IntProvider virtualPlayers;
	private final IntProvider initialHandCount;
	private final IntProvider timeOfDay;
	private final BlockStateProvider chairBlock;

	public LastCardConfig(Identifier map, PlayerConfig playerConfig, IntProvider virtualPlayers, IntProvider initialHandCount, IntProvider timeOfDay, BlockStateProvider chairBlock) {
		this.map = map;
		this.playerConfig = playerConfig;
		this.virtualPlayers = virtualPlayers;
		this.initialHandCount = initialHandCount;
		this.timeOfDay = timeOfDay;
		this.chairBlock = chairBlock;
	}

	public Identifier getMap() {
		return this.map;
	}

	public PlayerConfig getPlayerConfig() {
		return this.playerConfig;
	}

	public IntProvider getVirtualPlayers() {
		return this.virtualPlayers;
	}

	public IntProvider getInitialHandCount() {
		return this.initialHandCount;
	}

	public IntProvider getTimeOfDay() {
		return this.timeOfDay;
	}

	public BlockStateProvider getChairBlock() {
		return this.chairBlock;
	}
}