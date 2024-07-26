package io.github.haykam821.lastcard.card.display.pile;

import java.util.ArrayList;
import java.util.List;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.CardDeck;
import io.github.haykam821.lastcard.card.display.CardDisplay;
import io.github.haykam821.lastcard.card.display.CardRenderData;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import io.github.haykam821.lastcard.game.phase.PlayerEntryGetter;
import xyz.nucleoid.map_templates.TemplateRegion;

public class PileCardDisplay extends CardDisplay {
	private final CardDeck deck;

	public PileCardDisplay(CardDeck deck, PlayerEntryGetter entryGetter, TemplateRegion region) {
		super(entryGetter, region);

		this.deck = deck;
	}
	
	@Override
	public Iterable<Card> getCards() {
		List<Card> cards = new ArrayList<>();

		// Draw pile
		cards.add(null);

		// Discard pile
		Card previousCard = this.deck.getPreviousCard();
		if (previousCard != null) {
			cards.add(previousCard);
		}

		return cards;
	}

	@Override
	public DrawableCanvas renderCardCanvas(CardRenderData data) {
		if (data.card() == null) {
			return CardTemplates.BACK;
		}

		return data.renderCard();
	}
}
