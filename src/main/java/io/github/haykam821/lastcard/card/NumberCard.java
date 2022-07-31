package io.github.haykam821.lastcard.card;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import io.github.haykam821.lastcard.card.display.CardSpacing;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class NumberCard extends Card {
	private static final CanvasFont FONT = DefaultFonts.VANILLA;
	private static final int FONT_SIZE = 16;

	private int value;

	public NumberCard(CardColor color, int value) {
		super(color);
		this.value = value;
	}

	@Override
	public Text getName() {
		return new TranslatableText("text.lastcard.card.number", this.value);
	}

	@Override
	public boolean isMatching(Card card) {
		// Allow value to match in addition to color
		if (card instanceof NumberCard) {
			NumberCard numberCard = (NumberCard) card;
			if (this.value == numberCard.value) return true;
		}

		return super.isMatching(card);
	}

	@Override
	public void renderOverlay(DrawableCanvas canvas, CanvasColor textColor) {
		String text = this.value + "";

		int width = FONT.getTextWidth(text, FONT_SIZE);
		int x = canvas.getWidth() - CardSpacing.OVERLAY_PADDING - 2 - width;

		FONT.drawText(canvas, text, x, CardSpacing.OVERLAY_PADDING, FONT_SIZE, textColor);
	}
}
