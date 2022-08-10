package io.github.haykam821.lastcard.card;

import io.github.haykam821.lastcard.game.PlayerEntry;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import net.minecraft.text.Text;

public abstract class DrawCard extends SymbolCard {
	private final int value;

	public DrawCard(CardColor color, int value) {
		super(color);

		this.value = value;
	}

	@Override
	public boolean isMatching(Card card) {
		// Draw cards can only increase their value
		if (card instanceof DrawCard) {
			return this.value >= ((DrawCard) card).value;
		}

		return super.isMatching(card);
	}

	@Override
	public void play(PlayerEntry player) {
		super.play(player);

		LastCardActivePhase phase = player.getPhase();
		PlayerEntry drawPlayer = phase.getPlayerEntry(phase.getTurnManager().getNextTurnIndex(false));

		for (int index = 0; index < this.value; index++) {
			drawPlayer.draw();
		}

		this.sendDrawMessage(phase, drawPlayer);
		phase.getTurnManager().skipNextTurn();
	}

	private void sendDrawMessage(LastCardActivePhase phase, PlayerEntry player) {
		Text cardDrewMessage = player.getCardDrewMessage(this.value);
		Text cardDrewManyYouMessage = player.getCardDrewManyYouMessage(this.value);

		phase.sendMessageWithException(cardDrewMessage, player, cardDrewManyYouMessage);
	}
}
