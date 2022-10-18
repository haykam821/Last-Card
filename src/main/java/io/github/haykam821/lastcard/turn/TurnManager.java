package io.github.haykam821.lastcard.turn;

import io.github.haykam821.lastcard.game.PlayerEntry;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;

public class TurnManager {
	private final LastCardActivePhase phase;
	private PlayerEntry turn;
	private boolean skipNextTurn = false;
	private TurnDirection direction = TurnDirection.CLOCKWISE;

	public TurnManager(LastCardActivePhase phase) {
		this.phase = phase;
	}

	public PlayerEntry getTurn() {
		return this.turn;
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

		PlayerEntry oldTurn = this.turn;

		this.turn = this.phase.getPlayerEntry(this.getNextTurnIndex(true));
		this.skipNextTurn = false;

		if (oldTurn != this.turn) {
			this.sendNextTurnMessage();
		}

		for (PlayerEntry player : this.phase.getPlayers()) {
			player.updateDirtyDisplays();
		}

		this.phase.updatePileDisplay();
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
