package io.github.haykam821.lastcard.card;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import io.github.haykam821.lastcard.game.PlayerEntry;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class SkipCard extends SymbolCard {
	public SkipCard(CardColor color) {
		super(color);
	}

	@Override
	public Text getName() {
		return new TranslatableText("text.lastcard.card.skip");
	}

	@Override
	public boolean isMatching(Card card) {
		// Allow skip cards to match
		if (card instanceof SkipCard) {
			return true;
		}

		return super.isMatching(card);
	}

	@Override
	public void play(PlayerEntry player) {
		super.play(player);

		this.sendTurnSkippedMessage(player.getPhase());
		player.getPhase().getTurnManager().skipNextTurn();
	}

	private void sendTurnSkippedMessage(LastCardActivePhase phase) {
		int skippedIndex = phase.getTurnManager().getNextTurnIndex(false);
		PlayerEntry skippedPlayer = phase.getPlayerEntry(skippedIndex);

		phase.sendMessageWithException(this.getTurnSkippedMessage(skippedPlayer), skippedPlayer, this.getTurnSkippedYouMessage());
	}

	private Text getTurnSkippedMessage(PlayerEntry player) {
		return new TranslatableText("text.lastcard.turn.skipped", player.getName()).formatted(Formatting.GOLD);
	}

	private Text getTurnSkippedYouMessage() {
		return new TranslatableText("text.lastcard.turn.skipped.you").formatted(Formatting.GOLD);
	}

	@Override
	public DrawableCanvas getSymbol() {
		return CardTemplates.SKIP_SYMBOL;
	}
}
