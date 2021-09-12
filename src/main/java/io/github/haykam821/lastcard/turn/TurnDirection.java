package io.github.haykam821.lastcard.turn;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum TurnDirection {
	CLOCKWISE("clockwise", 1),
	COUNTERCLOCKWISE("counterclockwise", -1);

	private final Text name;
	private final int multiplier;

	private TurnDirection(String key, int multiplier) {
		this.name = new TranslatableText("text.lastcard.turn.direction." + key);
		this.multiplier = multiplier;
	}

	public Text getName() {
		return this.name;
	}

	public int multiply(int value) {
		return value * this.multiplier;
	}

	public TurnDirection getOpposite() {
		return this == CLOCKWISE ? COUNTERCLOCKWISE : CLOCKWISE;
	}
}
