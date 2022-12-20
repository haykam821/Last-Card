package io.github.haykam821.lastcard.card.display.pile;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.CardDeck;
import io.github.haykam821.lastcard.card.display.region.CardRegion;
import io.github.haykam821.lastcard.card.display.region.DrawCardRegion;
import io.github.haykam821.lastcard.game.phase.PlayerEntryGetter;
import xyz.nucleoid.map_templates.TemplateRegion;

public class PrivatePileCardDisplay extends PileCardDisplay {
	public PrivatePileCardDisplay(CardDeck deck, PlayerEntryGetter entryGetter, TemplateRegion region) {
		super(deck, entryGetter, region);
	}

	@Override
	public boolean hasOutline(Card card) {
		return card == null && !this.entryGetter.getTurn().hasPlayableCard();
	}

	@Override
	public CardRegion getCardRegion(Card card, int minX, int minY, int maxX, int maxY) {
		if (card == null) {
			return new DrawCardRegion(minX, minY, maxX, maxY);
		}

		return null;
	}
}
