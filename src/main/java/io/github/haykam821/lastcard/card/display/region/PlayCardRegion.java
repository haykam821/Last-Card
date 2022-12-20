package io.github.haykam821.lastcard.card.display.region;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.ColorSelector;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;

public class PlayCardRegion extends CardRegion {
	private final Card card;

	public PlayCardRegion(Card card, int minX, int minY, int maxX, int maxY) {
		super(minX, minY, maxX, maxY);

		this.card = card;
	}

	@Override
	public void onClick(AbstractPlayerEntry player, int x, int y) {
		ColorSelector selector = this.card.getSelector();

		double relativeX = (x - this.minX) / (double) (this.maxX - this.minX);
		double relativeY = (y - this.minY) / (double) (this.maxY - this.minY);

		player.playCard(this.card, selector.select(relativeX, relativeY));
	}

	@Override
	public String toString() {
		return "PlayCardRegion{card=" + this.card + "}" + this.getBoundsString();
	}
}
