package io.github.haykam821.lastcard.game;

import java.util.ArrayList;
import java.util.List;

import eu.pb4.holograms.api.holograms.AbstractHologram;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.display.CardDisplay;
import io.github.haykam821.lastcard.card.display.player.PrivateCardDisplay;
import io.github.haykam821.lastcard.card.display.player.PublicCardDisplay;
import io.github.haykam821.lastcard.game.map.Chair;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.map_templates.TemplateRegion;

public class PlayerEntry {
	private final LastCardActivePhase phase;
	private final ServerPlayerEntity player;
	private final List<Card> cards;

	private final Chair chair;
	private final AbstractHologram statusHologram;

	private final CardDisplay privateDisplay;
	private final CardDisplay publicDisplay;

	private boolean dirtyDisplays = false;

	public PlayerEntry(LastCardActivePhase phase, ServerPlayerEntity player, TemplateRegion chair, TemplateRegion privateDisplay, TemplateRegion publicDisplay) {
		this.phase = phase;
		this.player = player;

		int initialHandCount = phase.getConfig().getInitialHandCount().get(player.getRandom());
		this.cards = new ArrayList<>(initialHandCount);

		for (int index = 0; index < initialHandCount; index++) {
			this.cards.add(this.phase.getDeck().draw());
		}

		this.chair = new Chair(chair);
		this.statusHologram = this.chair.createStatusHologram(this.phase.getWorld());

		this.privateDisplay = new PrivateCardDisplay(this, privateDisplay);
		this.publicDisplay = new PublicCardDisplay(this, publicDisplay);
	}

	public boolean hasTurn() {
		return this.phase.getTurnManager().hasTurn(this);
	}

	public Chair getChair() {
		return this.chair;
	}

	public void spawn() {
		this.chair.teleport(this.player);
	}

	public Text getWinMessage() {
		return new TranslatableText("text.lastcard.win", this.getName()).formatted(Formatting.GOLD);
	}

	public Text getNextTurnMessage() {
		return new TranslatableText("text.lastcard.turn.next", this.getName()).formatted(Formatting.GOLD);
	}

	public Text getName() {
		return this.player.getDisplayName();
	}

	private Text getTurnName() {
		Text name = this.getName().shallowCopy().formatted(Formatting.BOLD);
		return this.hasTurn() ? new TranslatableText("text.lastcard.status.player_turn", name).formatted(Formatting.AQUA) : name;
	}

	public LastCardActivePhase getPhase() {
		return this.phase;
	}

	public ServerPlayerEntity getPlayer() {
		return this.player;
	}

	public Iterable<Card> getCards() {
		return this.cards;
	}

	public int getCardCount() {
		return this.cards.size();
	}

	private Text getCardStatus() {
		int cards = this.getCardCount();
		String key = "text.lastcard.status.cards" + (cards == 1 ? ".single" : "");

		return new TranslatableText(key, cards);
	}

	public void playCard(Card card, CardColor color) {
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

	public boolean hasPlayableCard() {	
		for (Card card : this.getCards()) {
			if (card.canPlay(this)) {
				return true;
			}
		}

		return false;
	}

	private void discardCard(Card card, CardColor color) {
		this.phase.getDeck().discard(card, color);

		this.cards.remove(card);
		this.markDirtyDisplays();
	}

	/**
	 * Draws a single card from the deck into this player's hand.
	 * @return the drawn card
	 */
	public Card draw() {
		Card card = this.phase.getDeck().draw();

		this.cards.add(card);
		this.markDirtyDisplays();

		return card;
	}

	public void drawForTurn() {
		if (this.hasTurn() && !this.hasPlayableCard()) {
			Card card = this.draw();

			this.phase.sendMessageWithException(this.getCardDrewMessage(1), this, this.getCardDrewYouMessage(card));
			this.phase.getTurnManager().cycleTurn();
		}
	}

	public Text getCardDrewMessage(int count) {
		MutableText text;

		if (count == 1) {
			text = new TranslatableText("text.lastcard.card_drew", this.getName());
		} else if (count > 1) {
			text = new TranslatableText("text.lastcard.card_drew.many", this.getName(), count);
		} else {
			throw new IllegalStateException("Cannot get negative card drew message");
		}

		return text.formatted(Formatting.GOLD);
	}

	private Text getCardDrewYouMessage(Card card) {
		return new TranslatableText("text.lastcard.card_drew.you", card.getFullName()).formatted(Formatting.GOLD);
	}

	public Text getCardDrewManyYouMessage(int count) {
		return new TranslatableText("text.lastcard.card_drew.many.you", count).formatted(Formatting.GOLD);
	}

	// Displays
	private CardDisplay getDisplayViewableBy(ServerPlayerEntity viewer) {
		return this.getPlayer() == viewer ? this.privateDisplay : this.publicDisplay;
	}

	public void addDisplay(ServerPlayerEntity viewer) {
		this.getDisplayViewableBy(viewer).add(viewer);
	}

	public void removeDisplay(ServerPlayerEntity viewer) {
		this.getDisplayViewableBy(viewer).remove(viewer);
	}

	public void destroyDisplays() {
		this.statusHologram.hide();
		this.privateDisplay.destroy();
		this.publicDisplay.destroy();
	}

	private ItemStack createHeadStack() {
		ItemStack stack = new ItemStack(Items.PLAYER_HEAD);

		NbtCompound nbt = new NbtCompound();
		nbt.putString(SkullBlockEntity.SKULL_OWNER_KEY, this.player.getEntityName());

		stack.setNbt(nbt);
		return stack;
	}

	private void updateStatus() {
		this.statusHologram.setItemStack(0, this.createHeadStack(), true);

		this.statusHologram.setText(1, this.getTurnName(), false);
		this.statusHologram.setText(2, this.getCardStatus(), false);
	}

	public void updateDisplays() {
		this.updateStatus();
		this.privateDisplay.update();
		this.publicDisplay.update();
	}

	public void updateDirtyDisplays() {
		if (this.dirtyDisplays) {
			this.updateDisplays();
			this.dirtyDisplays = false;
		}
	}

	public void markDirtyDisplays() {
		this.dirtyDisplays = true;
	}

	@Override
	public String toString() {
		return "PlayerEntry{player=" + this.player + ", chair=" + this.chair + "}";
	}
}
