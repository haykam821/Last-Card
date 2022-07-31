package io.github.haykam821.lastcard.card.display;

import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.game.player.PlayerEntry;
import net.minecraft.util.math.BlockPos;

public abstract class PlayerCardDisplay extends CardDisplay {
	private final PlayerEntry player;

	protected PlayerCardDisplay(PlayerEntry player, PlayerCanvas canvas, BlockPos pos, int rotation) {
		super(canvas, pos, rotation);

		this.player = player;
	}

	@Override
	public Iterable<Card> getCards() {
		return this.player.getCards();
	}
}
