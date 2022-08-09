package io.github.haykam821.lastcard.card.display;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;

public final class CardOutlineRenderer {
	private static final CanvasColor OUTLINE_COLOR = CanvasColor.WHITE_HIGH;
	private static final byte OUTLINE_RAW_COLOR = OUTLINE_COLOR.getRenderColor();

	private CardOutlineRenderer() {
		return;
	}

	protected static void render(DrawableCanvas canvas, int minX, int minY, int width, int height) {
		for (int x = 0; x < width; x++) {
			canvas.setRaw(minX + x, minY, OUTLINE_RAW_COLOR);
			canvas.setRaw(minX + x, minY + height, OUTLINE_RAW_COLOR);
		}

		for (int y = 0; y < height; y++) {
			canvas.setRaw(minX, minY + y, OUTLINE_RAW_COLOR);
			canvas.setRaw(minX + width, minY + y, OUTLINE_RAW_COLOR);
		}
	}
}
