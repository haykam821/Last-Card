package io.github.haykam821.lastcard.card.display.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.display.CardDisplay;

public class CardLayout {
	private static final Iterable<CardColor> NO_OVERRIDES_SET = Collections.singleton(null);

	private final CardDisplay display;
	private final List<LayoutEntry> entries = new ArrayList<>();

	private int totalWidth;
	private int totalHeight;

	public CardLayout(CardDisplay display) {
		this.display = display;
	}

	public List<LayoutEntry> getEntries() {
		return this.entries;
	}

	public int getTotalWidth() {
		return this.totalWidth;
	}

	public int getTotalHeight() {
		return this.totalHeight;
	}

	public void addCards(DrawableCanvas canvas, Iterable<Card> cards) {
		int x = this.display.getHorizontalMargin(0);
		int y = CardSpacing.PADDING_Y;

		int maxX = canvas.getWidth() - x;
		int maxHeight = 0;

		int row = 0;

		for (Card card : cards) {
			for (CardColor overrideColor : this.getOverrideColors(card)) {
				DrawableCanvas cardCanvas = this.display.getCardCanvas(card, overrideColor);

				if (cardCanvas != null) {
					int width = cardCanvas.getWidth();
					int height = cardCanvas.getHeight();

					if (x + width >= maxX) {
						row += 1;
						x = this.display.getHorizontalMargin(row);
						y += this.display.getVerticalSpacing(maxHeight);

						maxX = canvas.getWidth() - x;

						if (y + height >= canvas.getHeight()) {
							break;
						}
					}

					this.entries.add(new LayoutEntry(card, overrideColor, x, y, width, height));

					this.totalWidth = Math.max(x + width, this.totalWidth);
					this.totalHeight = Math.max(y + height, this.totalHeight);

					maxHeight = Math.max(maxHeight, height);
					x += this.display.getHorizontalSpacing(width);
				}
			}
		}

		this.totalWidth += this.display.getHorizontalMargin(0);
		this.totalHeight += CardSpacing.PADDING_Y;
	}

	private Iterable<CardColor> getOverrideColors(Card card) {
		if (card != null && this.display.shouldFlattenSelectors()) {
			Iterable<CardColor> selectableColors = card.getSelector().getSelectableColors();

			if (selectableColors != null) {
				return selectableColors;
			}
		}

		return NO_OVERRIDES_SET;
	}
}
