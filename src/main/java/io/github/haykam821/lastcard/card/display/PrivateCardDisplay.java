package io.github.haykam821.lastcard.card.display;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.display.region.CardRegion;
import io.github.haykam821.lastcard.card.display.region.PlayCardRegion;
import io.github.haykam821.lastcard.game.PlayerEntry;
import xyz.nucleoid.map_templates.TemplateRegion;

public class PrivateCardDisplay extends PlayerCardDisplay {
	public PrivateCardDisplay(PlayerEntry player, TemplateRegion region) {
		super(player, region);
	}

	@Override
	public DrawableCanvas renderCardCanvas(Card card) {
		return card.render();
	}

	@Override
	public CardRegion getCardRegion(Card card, int minX, int minY, int maxX, int maxY) {
		return new PlayCardRegion(card, minX, minY, maxX, maxY);
	}
}
