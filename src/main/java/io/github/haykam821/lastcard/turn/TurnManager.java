package io.github.haykam821.lastcard.turn;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import io.github.haykam821.lastcard.game.player.PlayerEntry;

public class TurnManager {
	private final LastCardActivePhase phase;
	private PlayerEntry turn;
	private boolean skipNextTurn = false;
	private TurnDirection direction = TurnDirection.CLOCKWISE;

	public TurnManager(LastCardActivePhase phase) {
		this.phase = phase;
	}

	public boolean hasTurn(PlayerEntry entry) {
		return this.turn == entry;
	}

	public void setTurn(PlayerEntry turn) {
		this.turn = turn;
	}

	public int getNextTurnIndex(boolean skipNextTurn) {
		int offset = this.direction.multiply(skipNextTurn && this.skipNextTurn ? 2 : 1);

		return this.phase.getPlayers().indexOf(this.turn) + offset;
	}

	public void cycleTurn() {
		if (this.phase.getPlayers().isEmpty()) return;

		this.turn.renderDisplays();
		this.phase.renderPileDisplay();

		PlayerEntry oldTurn = this.turn;

		this.turn = this.phase.getPlayerEntry(this.getNextTurnIndex(true));
		this.skipNextTurn = false;

		if (oldTurn != this.turn) {
			this.sendNextTurnMessage();

			oldTurn.teleportHome();
			this.turn.teleportHome();
		}

		// Draw a card if none are playable
		for (Card card : this.turn.getCards()) {
			if (card.canPlay(this.turn)) {
				return;
			}
		}
		this.turn.drawForTurn();
	}

	public void skipNextTurn() {
		this.skipNextTurn = true;
	}

	public TurnDirection reverseDirection() {
		return this.direction = this.direction.getOpposite();
	}

	public void sendNextTurnMessage() {
		if (this.turn != null) {
			this.phase.sendMessage(this.turn.getNextTurnMessage());
		}
	}
}
