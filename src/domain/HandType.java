package domain;

public enum HandType {
	// carte haute, paire, double paire, brelan, suite, couleur, full, carré, quinte, flush
	HIGH_CARD(5, 1),
	PAIR(10, 2),
	TWO_PAIR(20, 2),
	THREE_OF_KIND(30, 3),
	STRAIGHT(30, 4),
	FLUSH(35, 4),
	FULL_HOUSE(40, 4),
	FOUR_OF_A_KIND(60, 7),
	STRAIGHT_FLUSH(100, 8);

	private final int baseChips;
	private final int baseMult;

	HandType(int baseChips, int baseMult) {
		this.baseChips = baseChips;
		this.baseMult = baseMult;
	}

	/**
	 * Returns the base number of chips for this combination, before any upgrade.
	 *
	 * @return the base chips of the combination
	 */
	public int baseChips() {
		return baseChips;
	}

	/**
	 * Returns the base multiplier of this combination, before any upgrade.
	 *
	 * @return the base multiplier of the combination
	 */
	public int baseMult() {
		return baseMult;
	}

	/**
	 * Returns the human-readable French name of the combination.
	 *
	 * @return the display name of the combination
	 */
	public String displayName() {
		return switch (this) {
			case HIGH_CARD -> "Carte Haute";
			case PAIR -> "Paire";
			case TWO_PAIR -> "Double Paire";
			case THREE_OF_KIND -> "Brelan";
			case STRAIGHT -> "Suite";
			case FLUSH -> "Couleur";
			case FULL_HOUSE -> "Full";
			case FOUR_OF_A_KIND -> "Carré";
			case STRAIGHT_FLUSH -> "Quinte Flush";
		};
	}
}