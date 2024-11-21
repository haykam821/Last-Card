package io.github.haykam821.lastcard.turn.action;

import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;

public interface TurnAction {
	public void run(AbstractPlayerEntry player);

	public default boolean hasNextTurnEffects() {
		return false;
	}
}
