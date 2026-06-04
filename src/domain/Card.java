package domain;

import java.util.Objects;

public record Card(Rank rank, Color color) {
	
	/**
	 * Validates that a card is built with a non-null rank and color.
	 *
	 * @throws NullPointerException if {@code rank} or {@code color} is null
	 */
	public Card {
		Objects.requireNonNull(rank);
		Objects.requireNonNull(color);
	}

	/**
	 * Returns the symbol displayed for this rank (ex: {@code "10"}, {@code "K"}, {@code "A"}).
	 *
	 * @return the display symbol of the rank
	 */
	@Override
	public String toString() {
		return " " + rank.symbol() + " " + color.symbol();
	}
}
