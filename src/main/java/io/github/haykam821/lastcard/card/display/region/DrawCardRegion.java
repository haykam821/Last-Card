package io.github.haykam821.lastcard.card.display.region;

import io.github.haykam821.lastcard.game.PlayerEntry;

public class DrawCardRegion extends CardRegion {
	public DrawCardRegion(int minX, int minY, int maxX, int maxY) {
		super(minX, minY, maxX, maxY);
	}

	@Override
	public void onClick(PlayerEntry player, int x, int y) {
		player.drawForTurn();
	}

	@Override
	public String toString() {
		return "DrawCardRegion" + this.getBoundsString();
	}
}
