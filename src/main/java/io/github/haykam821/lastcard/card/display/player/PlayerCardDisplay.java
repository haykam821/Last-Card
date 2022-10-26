package io.github.haykam821.lastcard.card.display.player;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.display.CardDisplay;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import xyz.nucleoid.map_templates.TemplateRegion;

public abstract class PlayerCardDisplay extends CardDisplay {
	protected final AbstractPlayerEntry player;

	protected PlayerCardDisplay(AbstractPlayerEntry player, TemplateRegion region) {
		super(player.getPhase(), region);

		this.player = player;
	}

	@Override
	public Iterable<Card> getCards() {
		return this.player.getCards();
	}
}
