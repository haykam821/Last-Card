package io.github.haykam821.lastcard.card.color;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class WildColorSelector implements ColorSelector {
	protected static final ColorSelector INSTANCE = new WildColorSelector();

	@Override
	public CardColor select(double x, double y) {
		if (x > 0.5) {
			if (y > 0.5) {
				return CardColor.YELLOW;
			} else {
				return CardColor.BLUE;
			}
		} else {
			if (y > 0.5) {
				return CardColor.GREEN;
			} else {
				return CardColor.RED;
			}
		}
	}

	@Override
	public boolean isMatching(CardColor color) {
		return true;
	}

	@Override
	public Text getName() {
		return new TranslatableText("text.lastcard.card.color.selector.wild");
	}

	@Override
	public Item getItem() {
		return Items.WHITE_WOOL;
	}

	@Override
	public Formatting getFormatting() {
		return Formatting.WHITE;
	}

	@Override
	public BossBar.Color getBossBarColor() {
		return BossBar.Color.WHITE;
	}

	@Override
	public DrawableCanvas getTemplate() {
		return CardTemplates.WILD_FRONT;
	}

	@Override
	public CanvasColor getCanvasTextColor() {
		return CanvasColor.BLACK_NORMAL;
	}
}
