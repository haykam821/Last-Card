package io.github.haykam821.lastcard.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardDeck {
	private static final int CARD_TOTAL = 76;

	private final List<Card> cards = new ArrayList<>(CARD_TOTAL);
	private final List<Card> discarded = new ArrayList<>(CARD_TOTAL);

	public CardDeck() {
		for (CardColor color : CardColor.VALUES) {
			for (int set = 0; set < 8; set++) {
				for (int value = set < 4 ? 0 : 1; value < 10; value++) {
					this.cards.add(new NumberCard(color, value));
				}

				this.cards.add(new SkipCard(color));
			}
		}
		Collections.shuffle(this.cards);
	}

	public Card draw() {
		// Refill cards from discarded
		if (this.cards.isEmpty()) {
			for (int index = 1; index < this.discarded.size(); index++) {
				this.cards.add(this.discarded.get(index));
			}

			if (this.cards.isEmpty()) {
				throw new IllegalStateException("Ran out of cards");
			} else {
				Collections.shuffle(this.cards);
			}
		}

		return this.cards.remove(0);
	}

	public void discard(Card card) {
		this.discarded.add(0, card);
	}

	public Card getPreviousCard() {
		if (this.discarded.isEmpty()) {
			return null;
		}
		return this.discarded.get(0);
	}
}
