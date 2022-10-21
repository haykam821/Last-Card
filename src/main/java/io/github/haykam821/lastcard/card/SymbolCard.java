package io.github.haykam821.lastcard.card;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.color.ColorSelector;
import io.github.haykam821.lastcard.card.display.layout.CardSpacing;

public abstract class SymbolCard extends Card {
	public SymbolCard(ColorSelector selector) {
		super(selector);
	}

	@Override
	public final void renderOverlay(DrawableCanvas canvas, CanvasColor textColor) {
		DrawableCanvas symbol = this.getSymbol();

		if (symbol != null) {
			int x = canvas.getWidth() - CardSpacing.OVERLAY_PADDING - symbol.getWidth();
			SymbolCard.drawWithColor(canvas, x, CardSpacing.OVERLAY_PADDING, symbol, textColor);
		}
	}

	public abstract DrawableCanvas getSymbol();

	private static void drawWithColor(DrawableCanvas canvas, int offsetX, int offsetY, DrawableCanvas source, CanvasColor color) {
		int width = source.getWidth();
		int height = source.getHeight();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (source.getRaw(x, y) != 0) {
					canvas.set(offsetX + x, offsetY + y, color);
				}
			}
		}
	}
}
