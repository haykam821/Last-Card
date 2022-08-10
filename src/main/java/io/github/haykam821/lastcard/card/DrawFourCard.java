package io.github.haykam821.lastcard.card;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DrawFourCard extends DrawCard {
	public DrawFourCard(CardColor color) {
		super(color, 4);
	}

	@Override
	public Text getName() {
		return new TranslatableText("text.lastcard.card.draw_four");
	}

	@Override
	public DrawableCanvas getSymbol() {
		return CardTemplates.DRAW_FOUR_SYMBOL;
	}
}
