package io.github.haykam821.lastcard.turn.action;

import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SkipTurnAction implements TurnAction {
	public static final TurnAction INSTANCE = new SkipTurnAction();

	private SkipTurnAction() {
		return;
	}

	@Override
	public void run(AbstractPlayerEntry player) {
		LastCardActivePhase phase = player.getPhase();

		phase.sendMessageWithException(this.getTurnSkippedMessage(player), player, this.getTurnSkippedYouMessage());
		phase.getTurnManager().cycleTurn();
	}

	private Text getTurnSkippedMessage(AbstractPlayerEntry player) {
		return Text.translatable("text.lastcard.turn.skipped", player.getName()).formatted(Formatting.GOLD);
	}

	private Text getTurnSkippedYouMessage() {
		return Text.translatable("text.lastcard.turn.skipped.you").formatted(Formatting.GOLD);
	}
}
