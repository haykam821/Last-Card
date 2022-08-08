package io.github.haykam821.lastcard.card.display.region;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.game.PlayerEntry;

public class PlayCardRegion extends CardRegion {
	private final Card card;

	public PlayCardRegion(Card card, int minX, int minY, int maxX, int maxY) {
		super(minX, minY, maxX, maxY);

		this.card = card;
	}

	@Override
	public void onClick(PlayerEntry player) {
		player.playCard(this.card);
	}

	@Override
	public String toString() {
		return "PlayCardRegion{card=" + this.card + "}" + this.getBoundsString();
	}
}
