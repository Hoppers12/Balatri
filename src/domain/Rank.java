package domain;

public enum Rank {
	TWO(2, "2"),
  THREE(3, "3"),
  FOUR(4, "4"),
  FIVE(5, "5"),
  SIX(6, "6"),
  SEVEN(7, "7"),
  EIGHT(8, "8"),
  NINE(9, "9"),
  TEN(10, "10"),
  JACK(11, "J"),
  QUEEN(12, "Q"),
  KING(13, "K"),
  ACE(14, "A");

	private final int value;
	private final String symbol;

	Rank(int value, String symbol) {
		this.value = value;
		this.symbol = symbol;
	}

	/**
	 * Returns the numeric value of the rank, used for scoring and straight detection
	 * (2..10 for number cards, 11 = Jack, 12 = Queen, 13 = King, 14 = Ace).
	 *
	 * @return the rank value
	 */
	public int value() {
		return value;
	}

	/**
	 * Returns the symbol displayed for this rank (ex: {@code "10"}, {@code "K"}, {@code "A"}).
	 *
	 * @return the display symbol of the rank
	 */
	public String symbol() {
		return symbol;
	}
}
