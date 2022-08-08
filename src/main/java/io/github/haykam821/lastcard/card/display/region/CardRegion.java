package io.github.haykam821.lastcard.card.display.region;

import io.github.haykam821.lastcard.game.player.PlayerEntry;

public abstract class CardRegion {
	private final int minX;
	private final int minY;

	private final int maxX;
	private final int maxY;

	public CardRegion(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;

		this.maxX = maxX;
		this.maxY = maxY;
	}

	public abstract void onClick(PlayerEntry player);

	public boolean contains(int x, int y) {
		return x >= this.minX && y >= this.minY && x <= this.maxX && y <= this.maxY;
	}

	protected String getBoundsString() {
		return "(" + this.minX + "," + this.minY + " -> " + this.maxX + "," + this.maxY + ")";
	}

	@Override
	public String toString() {
		return "CardRegion" + this.getBoundsString();
	}
}
