package io.github.haykam821.lastcard.card;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class NumberCard extends Card {
	private int value;

	public NumberCard(CardColor color, int value) {
		super(color);
		this.value = value;
	}

	@Override
	public Text getName() {
		return new TranslatableText("text.lastcard.card.number", this.value);
	}

	@Override
	public boolean isMatching(Card card) {
		// Allow value to match in addition to color
		if (card instanceof NumberCard) {
			NumberCard numberCard = (NumberCard) card;
			if (this.value == numberCard.value) return true;
		}

		return super.isMatching(card);
	}
}
