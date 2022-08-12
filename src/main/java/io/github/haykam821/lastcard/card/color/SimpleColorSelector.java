package io.github.haykam821.lastcard.card.color;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class SimpleColorSelector implements ColorSelector {
	protected static final Map<CardColor, ColorSelector> INSTANCES = Util.make(() -> {
		Map<CardColor, ColorSelector> map = new EnumMap<>(CardColor.class);
		
		for (CardColor color : CardColor.VALUES) {
			map.put(color, new SimpleColorSelector(color));
		}

		return map;
	});

	private final CardColor color;

	private SimpleColorSelector(CardColor color) {
		this.color = Objects.requireNonNull(color);
	}

	@Override
	public CardColor select(double x, double y) {
		return this.color;
	}

	@Override
	public boolean isMatching(CardColor color) {
		return this.color == color;
	}

	@Override
	public Text getName() {
		return this.color.getName();
	}

	@Override
	public Item getItem() {
		return this.color.getItem();
	}

	@Override
	public Formatting getFormatting() {
		return this.color.getFormatting();
	}

	@Override
	public BossBar.Color getBossBarColor() {
		return this.color.getBossBarColor();
	}

	@Override
	public DrawableCanvas getTemplate() {
		return this.color.getTemplate();
	}

	@Override
	public CanvasColor getCanvasTextColor() {
		return this.color.getCanvasTextColor();
	}

	@Override
	public String toString() {
		return "SimpleColorSelector{color=" + this.color + "}";
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof SimpleColorSelector) {
			SimpleColorSelector other = (SimpleColorSelector) object;
			return this.color == other.color;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.color.hashCode();
	}
}
