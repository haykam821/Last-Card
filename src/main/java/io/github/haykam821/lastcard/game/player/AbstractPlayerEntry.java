package io.github.haykam821.lastcard.game.player;

import java.util.ArrayList;
import java.util.List;

import eu.pb4.holograms.api.holograms.AbstractHologram;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.display.CardDisplay;
import io.github.haykam821.lastcard.card.display.player.PublicCardDisplay;
import io.github.haykam821.lastcard.game.map.Chair;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.map_templates.TemplateRegion;

public abstract class AbstractPlayerEntry extends PlayerIdentifiable {
	private final LastCardActivePhase phase;
	private final List<Card> cards;

	private final Chair chair;
	private final AbstractHologram statusHologram;
	private final CardDisplay publicDisplay;

	private boolean dirtyDisplays = false;

	public AbstractPlayerEntry(LastCardActivePhase phase, TemplateRegion chair, TemplateRegion publicDisplay) {
		this.phase = phase;

		int initialHandCount = phase.getConfig().getInitialHandCount().get(phase.getWorld().getRandom());
		this.cards = new ArrayList<>(initialHandCount);

		for (int index = 0; index < initialHandCount; index++) {
			this.cards.add(this.phase.getDeck().draw());
		}

		this.chair = new Chair(chair);
		this.statusHologram = this.chair.createStatusHologram(this.phase.getWorld());

		this.publicDisplay = new PublicCardDisplay(this, publicDisplay);
	}

	public abstract void spawn();

	@Override
	public final boolean hasTurn() {
		return this.phase.getTurnManager().hasTurn(this);
	}

	@Override
	public final int getCardCount() {
		return this.cards.size();
	}

	public final Chair getChair() {
		return this.chair;
	}

	public final LastCardActivePhase getPhase() {
		return this.phase;
	}

	public final Iterable<Card> getCards() {
		return this.cards;
	}

	public final void playCard(Card card, CardColor color) {
		if (card.canPlay(this)) {
			this.discardCard(card, color);
			card.play(this);

			if (this.cards.isEmpty()) {
				this.phase.endWithWinner(this);
			} else {
				this.phase.getTurnManager().cycleTurn();
			}
		}
	}

	public final boolean hasPlayableCard() {	
		for (Card card : this.getCards()) {
			if (card.canPlay(this)) {
				return true;
			}
		}

		return false;
	}

	private final void discardCard(Card card, CardColor color) {
		this.phase.getDeck().discard(card, color);

		this.cards.remove(card);
		this.markDirtyDisplays();
	}

	/**
	 * Draws a single card from the deck into this player's hand.
	 * @return the drawn card
	 */
	public final Card draw() {
		Card card = this.phase.getDeck().draw();

		this.cards.add(card);
		this.markDirtyDisplays();

		return card;
	}

	public final void drawForTurn() {
		if (this.hasTurn() && !this.hasPlayableCard()) {
			Card card = this.draw();

			this.phase.sendMessageWithException(this.getCardDrewMessage(1), this, this.getCardDrewYouMessage(card));
			this.phase.getTurnManager().cycleTurn();
		}
	}

	// Displays
	protected CardDisplay getDisplayViewableBy(ServerPlayerEntity viewer) {
		return this.publicDisplay;
	}

	public final void addDisplay(ServerPlayerEntity viewer) {
		this.getDisplayViewableBy(viewer).add(viewer);
	}

	public final void removeDisplay(ServerPlayerEntity viewer) {
		this.getDisplayViewableBy(viewer).remove(viewer);
	}

	public void destroyDisplays() {
		this.statusHologram.hide();
		this.publicDisplay.destroy();
	}

	private void updateStatus() {
		this.statusHologram.setItemStack(0, this.createHeadStack(), true);

		this.statusHologram.setText(1, this.getTurnName(), false);
		this.statusHologram.setText(2, this.getCardStatus(), false);
	}

	public void updateDisplays() {
		this.updateStatus();
		this.publicDisplay.update();
	}

	public final void updateDirtyDisplays() {
		if (this.dirtyDisplays) {
			this.updateDisplays();
			this.dirtyDisplays = false;
		}
	}

	public final void markDirtyDisplays() {
		this.dirtyDisplays = true;
	}

	public void performVirtualAction() {
		return;
	}
}
