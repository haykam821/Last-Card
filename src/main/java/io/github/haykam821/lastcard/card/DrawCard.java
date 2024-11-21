package io.github.haykam821.lastcard.card;

import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.color.ColorSelector;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import io.github.haykam821.lastcard.turn.action.DrawTurnAction;

public abstract class DrawCard extends SymbolCard {
	private final int value;

	public DrawCard(ColorSelector selector, int value) {
		super(selector);

		this.value = value;
	}

	@Override
	public boolean isMatching(Card card, CardColor color) {
		// Draw cards can only increase their value
		if (card instanceof DrawCard) {
			return this.value >= ((DrawCard) card).value;
		}

		return super.isMatching(card, color);
	}

	@Override
	public void play(AbstractPlayerEntry player) {
		super.play(player);
		player.getPhase().getTurnManager().setNextTurnAction(new DrawTurnAction(this.value));
	}
}
