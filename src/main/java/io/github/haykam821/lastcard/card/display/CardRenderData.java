package io.github.haykam821.lastcard.card.display;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.color.ColorRepresentation;

public record CardRenderData(Card card, CardColor overrideColor) {
	public DrawableCanvas renderCard() {
		ColorRepresentation color = this.overrideColor == null ? this.card.getSelector() : this.overrideColor;
		return this.card.render(color);
	}
}
