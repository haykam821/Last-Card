package io.github.haykam821.lastcard.card.display;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;

public final class CardOutlineRenderer {
	private static final CanvasColor OUTLINE_COLOR = CanvasColor.WHITE_HIGH;
	private static final byte OUTLINE_RAW_COLOR = OUTLINE_COLOR.getRenderColor();

	private CardOutlineRenderer() {
		return;
	}

	public static void renderOutside(DrawableCanvas canvas, int minX, int minY, int width, int height) {
		CardOutlineRenderer.render(canvas, minX - 1, minY - 1, width + 2, height + 2);
	}

	protected static void render(DrawableCanvas canvas, int minX, int minY, int width, int height) {
		int maxX = minX + width - 1;
		int maxY = minY + height - 1;

		for (int x = minX; x <= maxX; x++) {
			canvas.setRaw(x, minY, OUTLINE_RAW_COLOR);
			canvas.setRaw(x, maxY, OUTLINE_RAW_COLOR);
		}

		for (int y = minY + 1; y < maxY; y++) {
			canvas.setRaw(minX, y, OUTLINE_RAW_COLOR);
			canvas.setRaw(maxX, y, OUTLINE_RAW_COLOR);
		}
	}
}
