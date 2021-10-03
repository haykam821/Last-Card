package io.github.haykam821.lastcard.card;

import io.github.haykam821.lastcard.game.player.PlayerEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipSection;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public abstract class Card {
	private final CardColor color;

	public Card(CardColor color) {
		this.color = color;
	}

	public final ItemStack createStack(PlayerEntry player) {
		ItemStackBuilder builder = ItemStackBuilder.of(this.color.getItem());
		builder.setName(this.getFullName());

		if (this.canPlay(player)) {
			builder.addEnchantment(Enchantments.POWER, 1);
		}

		ItemStack stack = builder.build();
		stack.getNbt().putInt("HideFlags", TooltipSection.ENCHANTMENTS.getFlag());

		return stack;
	}

	public abstract Text getName();

	public final Text getFullName() {
		return new LiteralText("")
			.append(this.color.getName())
			.append(" ")
			.append(this.getName())
			.formatted(this.color.getFormatting());
	}

	public final boolean canPlay(PlayerEntry player) {
		if (!player.hasTurn()) return false;

		Card previousCard = player.getPhase().getDeck().getPreviousCard();
		return previousCard == null || this.isMatching(previousCard);
	}

	public boolean isMatching(Card card) {
		return this.color == card.color;
	}

	public void play(PlayerEntry player) {
		player.getPhase().sendMessageWithException(this.getCardPlayedMessage(player), player, this.getCardPlayedYouMessage());
		player.getPhase().updateBar();
	}

	public BossBar.Color getBossBarColor() {
		return this.color.getBossBarColor();
	}

	private Text getCardPlayedMessage(PlayerEntry player) {
		return new TranslatableText("text.lastcard.card_played", player.getName(), this.getFullName()).formatted(Formatting.GOLD);
	}

	private Text getCardPlayedYouMessage() {
		return new TranslatableText("text.lastcard.card_played.you", this.getFullName()).formatted(Formatting.GOLD);
	}
}
