package io.github.haykam821.lastcard.game.map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.plasmid.game.world.generator.TemplateChunkGenerator;

public class LastCardMap {
	private final LastCardMapConfig mapConfig;
	private final MapTemplate template;
	private final Vec3d podium;
	private final Vec3d spawnPos;

	public LastCardMap(LastCardMapConfig mapConfig, MapTemplate template) {
		this.mapConfig = mapConfig;
		this.template = template;
		this.podium = LastCardMap.calculatePodium(this.mapConfig);
		this.spawnPos = this.podium.add(0, 3, 0);
	}

	public Vec3d getPodium() {
		return this.podium;
	}

	public Vec3d getSpawnPos() {
		return this.spawnPos;
	}

	public double getPodiumDistance() {
		if (this.mapConfig.getX() < this.mapConfig.getZ()) {
			return this.mapConfig.getX() / (double) 2 - 3;
		} else {
			return this.mapConfig.getZ() / (double) 2 - 3;
		}
	}

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new TemplateChunkGenerator(server, this.template);
	}

	private static Vec3d calculatePodium(LastCardMapConfig mapConfig) {
		return new Vec3d(mapConfig.getX() / 2f, 1, mapConfig.getZ() / 2f);
	}
}