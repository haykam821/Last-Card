package io.github.haykam821.lastcard.card.display.layout;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.display.CardOutlineRenderer;

public record LayoutEntry(Card card, CardColor overrideColor, int x, int y, int width, int height) {
	public void render(DrawableCanvas canvas, DrawableCanvas cardCanvas, boolean outline, int startX, int startY) {
		int x = startX + this.x;
		int y = startY + this.y;

		CanvasUtils.draw(canvas, x, y, cardCanvas);

		if (outline) {
			CardOutlineRenderer.renderOutside(canvas, x, y, this.width, this.height);
		}
	}

	public int maxX() {
		return this.x + this.width;
	}

	public int maxY() {
		return this.y + this.height;
	}
}
