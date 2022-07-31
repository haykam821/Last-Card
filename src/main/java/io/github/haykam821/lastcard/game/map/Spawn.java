package io.github.haykam821.lastcard.game.map;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.map_templates.TemplateRegion;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult.Accept;

public class Spawn {
	public final Vec3d pos;
	public final float rotation;

	public Spawn(TemplateRegion region) {
		this.pos = region.getBounds().centerBottom();
		this.rotation = LastCardRegions.getRotation(region);
	}

	public void teleport(ServerPlayerEntity player) {
		player.teleport(player.getWorld(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.rotation, 0);
	}

	public Accept acceptOffer(PlayerOffer offer, ServerWorld world, GameMode gameMode) {
		return offer.accept(world, this.pos).and(() -> {
			offer.player().changeGameMode(gameMode);
			offer.player().setYaw(this.rotation);
		});
	}
}
