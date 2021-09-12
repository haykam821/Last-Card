package io.github.haykam821.lastcard.game.player;

import java.util.function.Consumer;

import io.github.haykam821.lastcard.Main;
import io.github.haykam821.lastcard.card.Card;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.shop.ShopEntry;
import xyz.nucleoid.plasmid.shop.ShopUi;

public final class CardHandGui {
	private static final Text TITLE = Main.CARD_HAND.getName();

	private CardHandGui() {
		return;
	}

	private static Consumer<ServerPlayerEntity> getCardAction(PlayerEntry player, int index) {
		return playerx -> {
			player.playCard(index);
		};
	}

	public static ShopUi build(PlayerEntry player) {
		return ShopUi.create(TITLE, builder -> {
			int index = 0;
			for (Card card : player.getCards()) {
				ShopEntry entry = ShopEntry
					.ofIcon(card.createStack(player))
					.noCost()
					.onBuy(CardHandGui.getCardAction(player, index));

				builder.add(entry);
				index += 1;
			}
		});
	}
}
