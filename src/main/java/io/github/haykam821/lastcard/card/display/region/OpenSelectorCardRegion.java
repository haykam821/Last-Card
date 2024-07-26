package io.github.haykam821.lastcard.card.display.region;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.display.player.PrivateCardDisplay;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;

public class OpenSelectorCardRegion extends CardRegion {
	private final Card card;
	private final PrivateCardDisplay display;

	public OpenSelectorCardRegion(Card card, PrivateCardDisplay display, int minX, int minY, int maxX, int maxY) {
		super(minX, minY, maxX, maxY);

		this.card = card;
		this.display = display;
	}

	@Override
	public void onClick(AbstractPlayerEntry player, int x, int y) {
		this.display.setSelectingCard(this.card);
	}

	@Override
	public String toString() {
		return "OpenSelectorCardRegion{card=" + this.card + ", display=" + this.display + "}" + this.getBoundsString();
	}
}
