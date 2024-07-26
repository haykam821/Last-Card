package io.github.haykam821.lastcard.card.display.region;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.color.ColorSelector;
import io.github.haykam821.lastcard.card.display.player.PrivateCardDisplay;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;

public class PlayCardRegion extends CardRegion {
	private final Card card;
	private final CardColor overrideColor;

	private final PrivateCardDisplay display;

	public PlayCardRegion(Card card, CardColor overrideColor, PrivateCardDisplay display, int minX, int minY, int maxX, int maxY) {
		super(minX, minY, maxX, maxY);

		this.card = card;
		this.overrideColor = overrideColor;

		this.display = display;
	}

	private CardColor getColor(int x, int y) {
		if (this.overrideColor != null) {
			return this.overrideColor;
		}

		ColorSelector selector = this.card.getSelector();

		double relativeX = (x - this.minX) / (double) (this.maxX - this.minX);
		double relativeY = (y - this.minY) / (double) (this.maxY - this.minY);

		return selector.select(relativeX, relativeY);
	}

	@Override
	public void onClick(AbstractPlayerEntry player, int x, int y) {
		player.playCard(this.card, this.getColor(x, y));
		this.display.setSelectingCard(null);
	}

	@Override
	public String toString() {
		return "PlayCardRegion{card=" + this.card + ", overrideColor=" + this.overrideColor + ", display=" + this.display + "}" + this.getBoundsString();
	}
}
