package io.github.haykam821.lastcard.card;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.color.ColorSelector;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SkipCard extends SymbolCard {
	public SkipCard(ColorSelector selector) {
		super(selector);
	}

	@Override
	public Text getName() {
		return Text.translatable("text.lastcard.card.skip");
	}

	@Override
	public boolean isMatching(Card card, CardColor color) {
		// Allow skip cards to match
		if (card instanceof SkipCard) {
			return true;
		}

		return super.isMatching(card, color);
	}

	@Override
	public void play(AbstractPlayerEntry player) {
		super.play(player);

		this.sendTurnSkippedMessage(player.getPhase());
		player.getPhase().getTurnManager().skipNextTurn();
	}

	private void sendTurnSkippedMessage(LastCardActivePhase phase) {
		int skippedIndex = phase.getTurnManager().getNextTurnIndex(false);
		AbstractPlayerEntry skippedPlayer = phase.getPlayerEntry(skippedIndex);

		phase.sendMessageWithException(this.getTurnSkippedMessage(skippedPlayer), skippedPlayer, this.getTurnSkippedYouMessage());
	}

	private Text getTurnSkippedMessage(AbstractPlayerEntry player) {
		return Text.translatable("text.lastcard.turn.skipped", player.getName()).formatted(Formatting.GOLD);
	}

	private Text getTurnSkippedYouMessage() {
		return Text.translatable("text.lastcard.turn.skipped.you").formatted(Formatting.GOLD);
	}

	@Override
	public DrawableCanvas getSymbol() {
		return CardTemplates.SKIP_SYMBOL;
	}
}
