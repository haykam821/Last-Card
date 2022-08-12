package io.github.haykam821.lastcard.card.color;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface ColorRepresentation {
	public Text getName();

	public Item getItem();

	public Formatting getFormatting();

	public BossBar.Color getBossBarColor();

	public DrawableCanvas getTemplate();

	public CanvasColor getCanvasTextColor();
}
