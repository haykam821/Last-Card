package io.github.haykam821.lastcard.card.color;

/**
 * A selector for a {@link CardColor} when playing a card.
 */
public interface ColorSelector extends ColorRepresentation {
	/**
	 * @param x the relative X of the clicked card region between 0 and 1
	 * @param y the relative Y of the clicked card region between 0 and 1
	 */
	public CardColor select(double x, double y);

	/**
	 * {@return the colors that can be selected when playing this card, or {@code null} if {@link #select default selection behavior} should be used
	 */
	public Iterable<CardColor> getSelectableColors();

	public boolean isMatching(CardColor color);

	public static ColorSelector of(CardColor color) {
		return SimpleColorSelector.INSTANCES.get(color);
	}

	public static ColorSelector wild() {
		return WildColorSelector.INSTANCE;
	}
}
