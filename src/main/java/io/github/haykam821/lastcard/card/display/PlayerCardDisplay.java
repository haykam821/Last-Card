package io.github.haykam821.lastcard.card.display;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.game.PlayerEntry;
import xyz.nucleoid.map_templates.TemplateRegion;

public abstract class PlayerCardDisplay extends CardDisplay {
	private final PlayerEntry player;

	protected PlayerCardDisplay(PlayerEntry player, TemplateRegion region) {
		super(player.getPhase(), region);

		this.player = player;
	}

	@Override
	public Iterable<Card> getCards() {
		return this.player.getCards();
	}
}
