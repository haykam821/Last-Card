package io.github.haykam821.lastcard.card;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DrawTwoCard extends DrawCard {
	public DrawTwoCard(CardColor color) {
		super(color, 2);
	}

	@Override
	public Text getName() {
		return new TranslatableText("text.lastcard.card.draw_two");
	}

	@Override
	public DrawableCanvas getSymbol() {
		return CardTemplates.DRAW_TWO_SYMBOL;
	}
}
