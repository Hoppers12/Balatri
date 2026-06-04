package domain;

public enum Color {
	CLUBS("♣"),
	DIAMONDS("♦"),
	HEARTS("♥"),
	SPADES("♠");

	private final String symbol;

	Color(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Returns the Unicode symbol of the suit (♣, ♦, ♥ or ♠).
	 *
	 * @return the suit symbol
	 */
	public String symbol() {
		return symbol;
	}
}
