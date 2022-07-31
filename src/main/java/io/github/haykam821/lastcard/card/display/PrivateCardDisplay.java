package io.github.haykam821.lastcard.card.display;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.game.player.PlayerEntry;
import xyz.nucleoid.map_templates.TemplateRegion;

public class PrivateCardDisplay extends PlayerCardDisplay {
	public PrivateCardDisplay(PlayerEntry player, TemplateRegion region) {
		super(player, region);
	}

	@Override
	public DrawableCanvas renderCardCanvas(Card card) {
		return card.render();
	}
}
