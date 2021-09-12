package io.github.haykam821.lastcard.game.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

public class LastCardMapConfig {
	private static final BlockStateProvider DEFAULT_FLOOR_PROVIDER = new SimpleBlockStateProvider(Blocks.GRAY_CONCRETE.getDefaultState());

	public static final Codec<LastCardMapConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			Codec.INT.fieldOf("x").forGetter(LastCardMapConfig::getX),
			Codec.INT.fieldOf("z").forGetter(LastCardMapConfig::getZ),
			BlockStateProvider.TYPE_CODEC.optionalFieldOf("floor_provider", DEFAULT_FLOOR_PROVIDER).forGetter(LastCardMapConfig::getFloorProvider)
		).apply(instance, LastCardMapConfig::new);
	});

	private final int x;
	private final int z;
	private final BlockStateProvider floorProvider;

	public LastCardMapConfig(int x, int z, BlockStateProvider floorProvider) {
		this.x = x;
		this.z = z;
		this.floorProvider = floorProvider;
	}

	public int getX() {
		return this.x;
	}

	public int getZ() {
		return this.z;
	}

	public BlockStateProvider getFloorProvider() {
		return this.floorProvider;
	}
}