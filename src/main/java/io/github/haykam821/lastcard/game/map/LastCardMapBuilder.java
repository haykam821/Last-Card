package io.github.haykam821.lastcard.game.map;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.plasmid.map.template.MapTemplate;

public class LastCardMapBuilder {
	private final LastCardMapConfig mapConfig;

	public LastCardMapBuilder(LastCardMapConfig mapConfig) {
		this.mapConfig = mapConfig;
	}

	public LastCardMap create() {
		MapTemplate template = MapTemplate.createEmpty();

		this.buildFloor(template);
		return new LastCardMap(this.mapConfig, template);
	}

	private void buildFloor(MapTemplate template) {
		Random random = new Random();

		BlockPos.Mutable pos = new BlockPos.Mutable();
		for (int x = 0; x < this.mapConfig.getX(); x++) {
			pos.setX(x);
			for (int z = 0; z < this.mapConfig.getZ(); z++) {
				pos.setZ(z);
				template.setBlockState(pos, this.mapConfig.getFloorProvider().getBlockState(random, pos));
			}
		}
	}
}