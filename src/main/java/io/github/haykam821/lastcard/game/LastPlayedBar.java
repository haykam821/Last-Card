package io.github.haykam821.lastcard.game;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.widget.BossBarWidget;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;

public class LastPlayedBar {
	private static final BossBar.Style STYLE = BossBar.Style.PROGRESS;

	private final LastCardActivePhase phase;
	private final BossBarWidget bar;

	public LastPlayedBar(LastCardActivePhase phase, GlobalWidgets widgets) {
		this.phase = phase;
		this.bar = widgets.addBossBar(this.getTitle(), this.getColor(), STYLE);
	}

	public void update() {
		this.bar.setTitle(this.getTitle());
		this.bar.setStyle(this.getColor(), STYLE);
	}

	private Text getTitle() {
		Card previousCard = this.phase.getDeck().getPreviousCard();
		if (previousCard == null) {
			return new TranslatableText("text.lastcard.last_played.none");
		}

		return new TranslatableText("text.lastcard.last_played", previousCard.getFullName()).formatted(Formatting.GOLD);
	}

	private BossBar.Color getColor() {
		Card previousCard = this.phase.getDeck().getPreviousCard();
		if (previousCard == null) {
			return BossBar.Color.WHITE;
		}

		return previousCard.getBossBarColor();
	}
}
