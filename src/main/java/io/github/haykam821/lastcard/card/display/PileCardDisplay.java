package io.github.haykam821.lastcard.card.display;

import java.util.ArrayList;
import java.util.List;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.CardDeck;
import net.minecraft.util.math.BlockPos;

public class PileCardDisplay extends CardDisplay {
	private final CardDeck deck;

	public PileCardDisplay(CardDeck deck, PlayerCanvas canvas, BlockPos pos, int rotation) {
		super(canvas, pos, rotation);

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
	public DrawableCanvas renderCardCanvas(Card card) {
		if (card == null) {
			return CardTemplates.BACK;
		}

		return card.render();
	}
}
