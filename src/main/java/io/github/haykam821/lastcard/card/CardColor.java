package io.github.haykam821.lastcard.card;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum CardColor {
	RED("red", Items.RED_WOOL, Formatting.RED, BossBar.Color.RED, CardTemplates.RED_FRONT, CanvasColor.RED_NORMAL),
	GREEN("green", Items.GREEN_WOOL, Formatting.GREEN, BossBar.Color.GREEN, CardTemplates.GREEN_FRONT, CanvasColor.GREEN_NORMAL),
	YELLOW("yellow", Items.YELLOW_WOOL, Formatting.YELLOW, BossBar.Color.YELLOW, CardTemplates.YELLOW_FRONT, CanvasColor.YELLOW_NORMAL),
	BLUE("blue", Items.BLUE_WOOL, Formatting.DARK_AQUA, BossBar.Color.BLUE, CardTemplates.BLUE_FRONT, CanvasColor.BLUE_NORMAL);

	public static final CardColor[] VALUES = CardColor.values();

	private final Text name;
	private final Item item;

	private final Formatting formatting;
	private final BossBar.Color bossBarColor;

	private final DrawableCanvas template;
	private final CanvasColor canvasTextColor;

	private CardColor(String key, Item item, Formatting formatting, BossBar.Color bossBarColor, DrawableCanvas template, CanvasColor canvasTextColor) {
		this.name = new TranslatableText("text.lastcard.card.color." + key);
		this.item = item;

		this.formatting = formatting;
		this.bossBarColor = bossBarColor;

		this.template = template;
		this.canvasTextColor = canvasTextColor;
	}

	public Text getName() {
		return this.name;
	}

	public Item getItem() {
		return this.item;
	}

	public Formatting getFormatting() {
		return this.formatting;
	}

	public BossBar.Color getBossBarColor() {
		return this.bossBarColor;
	}

	public DrawableCanvas getTemplate() {
		return this.template;
	}

	public CanvasColor getCanvasTextColor() {
		return this.canvasTextColor;
	}
}
