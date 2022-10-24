package io.github.haykam821.lastcard.turn;

import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.game.PlayerEntry;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class TurnManager {
	private static final double PARTICLE_RADIUS = 1.5d;
	public static final float PARTICLE_SIZE = 0.8f;

	public static final Vec3f BLACK_PARTICLE_COLOR = Vec3f.ZERO;
	public static final ParticleEffect NO_COLOR_PARTICLE = CardColor.createParticleEffect(0xDDDDDD);

	private static final double PARTICLE_SPEED = 1 / 15d;
	private static final int PARTICLE_UPDATE_RATE = 1;

	private final LastCardActivePhase phase;
	private PlayerEntry turn;
	private boolean skipNextTurn = false;
	private TurnDirection direction = TurnDirection.CLOCKWISE;

	private final Vec3d particleOrigin;
	private int ticks = 0;

	public TurnManager(LastCardActivePhase phase, Vec3d particleOrigin) {
		this.phase = phase;
		this.particleOrigin = particleOrigin;
	}

	public PlayerEntry getTurn() {
		return this.turn;
	}

	public boolean hasTurn(PlayerEntry entry) {
		return this.turn == entry;
	}

	public void setTurn(PlayerEntry turn) {
		this.turn = turn;
	}

	public int getNextTurnIndex(boolean skipNextTurn) {
		int offset = this.direction.multiply(skipNextTurn && this.skipNextTurn ? 2 : 1);

		return this.phase.getPlayers().indexOf(this.turn) + offset;
	}

	public void cycleTurn() {
		if (this.phase.getPlayers().isEmpty()) return;

		PlayerEntry oldTurn = this.turn;

		this.turn = this.phase.getPlayerEntry(this.getNextTurnIndex(true));
		this.skipNextTurn = false;

		if (oldTurn != this.turn) {
			this.sendNextTurnEffects();
		}

		oldTurn.markDirtyDisplays();
		this.turn.markDirtyDisplays();

		for (PlayerEntry player : this.phase.getPlayers()) {
			player.updateDirtyDisplays();
		}

		this.phase.updatePileDisplay();
	}

	public void skipNextTurn() {
		this.skipNextTurn = true;
	}

	public TurnDirection reverseDirection() {
		return this.direction = this.direction.getOpposite();
	}

	public void sendNextTurnEffects() {
		if (this.turn != null) {
			this.phase.sendMessage(this.turn.getNextTurnMessage());
			TurnSounds.playTurnSounds(this.turn.getPlayer());
		}
	}

	public void tick() {
		this.ticks += this.direction.multiply(-1);

		if (this.ticks % PARTICLE_UPDATE_RATE == 0) {
			double x = this.particleOrigin.getX();
			double y = this.particleOrigin.getY();
			double z = this.particleOrigin.getZ();

			double deltaX = Math.sin(this.ticks * PARTICLE_SPEED) * PARTICLE_RADIUS;
			double deltaZ = Math.cos(this.ticks * PARTICLE_SPEED) * PARTICLE_RADIUS;

			CardColor color = this.phase.getDeck().getPreviousColor();
			ParticleEffect particle = color == null ? NO_COLOR_PARTICLE : color.getParticle();

			ServerWorld world = this.phase.getWorld();

			world.spawnParticles(particle, x + deltaX, y, z + deltaZ, 1, 0, 0, 0, 0);
			world.spawnParticles(particle, x - deltaX, y, z - deltaZ, 1, 0, 0, 0, 0);
		}
	}
}
