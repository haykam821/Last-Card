package io.github.haykam821.lastcard.card.display;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.game.PlayerEntry;
import xyz.nucleoid.map_templates.TemplateRegion;

public class PublicCardDisplay extends PlayerCardDisplay {
	public PublicCardDisplay(PlayerEntry player, TemplateRegion region) {
		super(player, region);
	}

	@Override
	public DrawableCanvas renderCardCanvas(Card card) {
		return CardTemplates.BACK;
	}

	@Override
	public int getHorizontalSpacing(int width) {
		return width / 4;
	}

	@Override
	public int getHorizontalMargin(int row) {
		return 0;
	}
}
