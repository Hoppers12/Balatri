package domain;

public enum Color {
	// trèfle, carreau, coeur, pique
	CLUBS("♣"),
	DIAMONDS("♦"),
	HEARTS("♥"),
	SPADES("♠");

	private final String symbol;
	
	Color(String symbol) {
		this.symbol = symbol;
	}
	
	public String symbol() {
		return symbol;
	}
}
