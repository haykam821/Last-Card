package io.github.haykam821.lastcard.game.player;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.lastcard.Main;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public class PlayerEntry {
	private static final int INITIAL_HAND_COUNT = 7;

	private final LastCardActivePhase phase;
	private final ServerPlayerEntity player;
	private final Vec3d home;
	private final List<Card> cards = new ArrayList<>(INITIAL_HAND_COUNT);

	public PlayerEntry(LastCardActivePhase phase, ServerPlayerEntity player, Vec3d home) {
		this.phase = phase;
		this.player = player;
		this.home = home;
		
		for (int index = 0; index < INITIAL_HAND_COUNT; index++) {
			this.cards.add(this.phase.getDeck().draw());
		}
	}

	public boolean hasTurn() {
		return this == this.phase.getTurn();
	}

	public Vec3d getCurrentHome() {
		if (this.hasTurn()) {
			return this.phase.getMap().getPodium();
		}
		return this.home;
	}

	public void teleportHome() {
		Vec3d home = this.getCurrentHome();
		this.player.teleport(this.phase.getWorld(), home.getX(), home.getY(), home.getZ(), this.player.yaw, this.player.pitch);
	}

	public void tick() {
		if (!this.player.getPos().equals(this.getCurrentHome())) {
			this.teleportHome();
		}
	}

	public void spawn() {
		this.player.inventory.setStack(0, new ItemStack(Main.CARD_HAND));
		this.player.currentScreenHandler.sendContentUpdates();

		this.player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, Integer.MAX_VALUE, 128, true, false));
		this.teleportHome();
	}

	public Text getWinMessage() {
		return new TranslatableText("text.lastcard.win", this.getName()).formatted(Formatting.GOLD);
	}

	public Text getNextTurnMessage() {
		return new TranslatableText("text.lastcard.next_turn", this.getName()).formatted(Formatting.GOLD);
	}

	public Text getName() {
		return this.player.getDisplayName();
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

	public void playCard(int index) {
		Card card = this.cards.get(index);
		this.playCard(card);
	}

	private void playCard(Card card) {
		if (card.canPlay(this)) {
			this.discardCard(card);
			card.play(this);

			if (this.cards.isEmpty()) {
				this.phase.endWithWinner(this);
			} else {
				this.phase.cycleTurn();
			}
		}
	}

	private void discardCard(Card card) {
		this.phase.getDeck().discard(card);
		this.cards.remove(card);
	}

	public void drawForTurn() {
		Card card = this.phase.getDeck().draw();
		this.cards.add(card);

		this.phase.sendMessageWithException(this.getCardDrewMessage(), this, this.getCardDrewYouMessage(card));
		this.phase.cycleTurn();
	}

	private Text getCardDrewMessage() {
		return new TranslatableText("text.lastcard.card_drew", this.getName()).formatted(Formatting.GOLD);
	}

	private Text getCardDrewYouMessage(Card card) {
		return new TranslatableText("text.lastcard.card_drew.you", card.getFullName()).formatted(Formatting.GOLD);
	}

	public void openCardHand() {
		player.openHandledScreen(CardHandGui.build(this));
	}

	@Override
	public String toString() {
		return "PlayerEntry{player=" + this.player + ", home=" + this.home + "}";
	}
}
