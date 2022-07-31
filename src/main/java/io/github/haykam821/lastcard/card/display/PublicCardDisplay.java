package io.github.haykam821.lastcard.card.display;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.game.player.PlayerEntry;
import net.minecraft.util.math.BlockPos;

public class PublicCardDisplay extends PlayerCardDisplay {
	public PublicCardDisplay(PlayerEntry player, PlayerCanvas canvas, BlockPos pos, int rotation) {
		super(player, canvas, pos, rotation);
	}

	@Override
	public DrawableCanvas renderCardCanvas(Card card) {
		return CardTemplates.BACK;
	}
}
