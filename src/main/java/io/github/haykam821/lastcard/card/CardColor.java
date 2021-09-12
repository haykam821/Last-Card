package io.github.haykam821.lastcard.card;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum CardColor {
	RED("red", Items.RED_WOOL, Formatting.RED, BossBar.Color.RED),
	GREEN("green", Items.GREEN_WOOL, Formatting.GREEN, BossBar.Color.GREEN),
	YELLOW("yellow", Items.YELLOW_WOOL, Formatting.YELLOW, BossBar.Color.YELLOW),
	BLUE("blue", Items.BLUE_WOOL, Formatting.DARK_AQUA, BossBar.Color.BLUE);

	public static final CardColor[] VALUES = CardColor.values();

	private final Text name;
	private final Item item;
	private final Formatting formatting;
	private final BossBar.Color bossBarColor;

	private CardColor(String key, Item item, Formatting formatting, BossBar.Color bossBarColor) {
		this.name = new TranslatableText("text.lastcard.card.color." + key);
		this.item = item;
		this.formatting = formatting;
		this.bossBarColor = bossBarColor;
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
}
