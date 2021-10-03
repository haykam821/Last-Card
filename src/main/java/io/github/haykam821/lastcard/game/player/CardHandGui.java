package io.github.haykam821.lastcard.game.player;

import java.util.function.Consumer;

import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.haykam821.lastcard.card.Card;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.shop.ShopEntry;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public final class CardHandGui {
	private CardHandGui() {
		return;
	}

	private static Consumer<ServerPlayerEntity> getCardAction(PlayerEntry player, int index) {
		return playerx -> {
			player.playCard(index);
			player.openCardHand();
		};
	}

	public static SimpleGui build(PlayerEntry player) {
		SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X5, player.getPlayer(), false);

		gui.setTitle(CardHandGui.getTitle(player));

		ItemStack infoStack = CardHandGui.getInfoStack(player);
		gui.addSlot(ShopEntry.ofIcon(infoStack).noCost());

		int index = 0;
		for (Card card : player.getCards()) {
			ShopEntry entry = ShopEntry
				.ofIcon(card.createStack(player))
				.noCost()
				.onBuy(CardHandGui.getCardAction(player, index));

			gui.addSlot(entry);
			index += 1;
		}

		return gui;
	}

	private static ItemStack getInfoStack(PlayerEntry player) {
		int cardCount = player.getCardCount();

		return ItemStackBuilder.of(Items.PAPER)
			.setName(new TranslatableText("text.lastcard.card_hand.info"))
			.addLore(new TranslatableText("text.lastcard.card_hand.info.count", cardCount).formatted(Formatting.GRAY))
			.setCount(cardCount)
			.build();
	}

	private static Text getTitle(PlayerEntry player) {
		return new TranslatableText("text.lastcard.card_hand", player.getName());
	}
}
