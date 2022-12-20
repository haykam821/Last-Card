package io.github.haykam821.lastcard.game.player;

import org.apache.commons.lang3.RandomStringUtils;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.ColorSelector;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.map_templates.TemplateRegion;

public class VirtualPlayerEntry extends AbstractPlayerEntry {
	private final String key = RandomStringUtils.randomAlphabetic(6);
	private final Text name = Text.literal(this.key).formatted(Formatting.GRAY);

	public VirtualPlayerEntry(LastCardActivePhase phase, TemplateRegion chair, TemplateRegion publicDisplay) {
		super(phase, chair, publicDisplay);
	}

	@Override
	public void spawn() {
		return;
	}

	@Override
	public Text getName() {
		return this.name;
	}

	@Override
	public ServerPlayerEntity getPlayer() {
		return null;
	}

	@Override
	protected ItemStack createHeadStack() {
		return new ItemStack(Items.COMMAND_BLOCK);
	}

	@Override
	public void performVirtualAction() {
		// Play the first playable card
		for (Card card : this.getCards()) {
			if (card.canPlay(this)) {
				ColorSelector selector = card.getSelector();
				this.playCard(card, selector.select(0, 0));

				return;
			}
		}

		// If no cards are playable, draw
		this.drawForTurn();
	}

	@Override
	public String toString() {
		return "VirtualPlayerEntry[" + this.key + "]";
	}
}
