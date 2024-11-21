package io.github.haykam821.lastcard.turn.action;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.ColorSelector;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;

public class VirtualTurnAction implements TurnAction {
	public static final TurnAction INSTANCE = new VirtualTurnAction();

	private VirtualTurnAction() {
		return;
	}

	@Override
	public void run(AbstractPlayerEntry player) {
		// Play the first playable card
		for (Card card : player.getCards()) {
			if (card.canPlay(player)) {
				ColorSelector selector = card.getSelector();
				player.playCard(card, selector.select(0, 0));

				return;
			}
		}

		// If no cards are playable, draw
		player.drawForTurn();
	}

	@Override
	public boolean hasNextTurnEffects() {
		return true;
	}
}
