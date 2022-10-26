package io.github.haykam821.lastcard.card;

import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.color.ColorSelector;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import net.minecraft.text.Text;

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

		LastCardActivePhase phase = player.getPhase();
		AbstractPlayerEntry drawPlayer = phase.getPlayerEntry(phase.getTurnManager().getNextTurnIndex(false));

		for (int index = 0; index < this.value; index++) {
			drawPlayer.draw();
		}

		this.sendDrawMessage(phase, drawPlayer);
		phase.getTurnManager().skipNextTurn();
	}

	private void sendDrawMessage(LastCardActivePhase phase, AbstractPlayerEntry player) {
		Text cardDrewMessage = player.getCardDrewMessage(this.value);
		Text cardDrewManyYouMessage = player.getCardDrewManyYouMessage(this.value);

		phase.sendMessageWithException(cardDrewMessage, player, cardDrewManyYouMessage);
	}
}
