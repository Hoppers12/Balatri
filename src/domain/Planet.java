package domain;

public enum Planet {
	PLUTO(HandType.HIGH_CARD, 10, 1),
	MERCURY(HandType.PAIR, 15, 1),
	URANUS(HandType.TWO_PAIR, 20, 1),
	VENUS(HandType.THREE_OF_KIND, 20, 2),
	SATURN(HandType.STRAIGHT, 30, 3),
	JUPITER(HandType.FLUSH, 15, 2),
	EARTH(HandType.FULL_HOUSE, 25, 2),
	MARS(HandType.FOUR_OF_A_KIND, 30, 3),
	NEPTUNE(HandType.STRAIGHT_FLUSH, 40, 4);

	private final HandType target;
	private final int bonusChips;
	private final int bonusMult;

	Planet(HandType target, int bonusChips, int bonusMult) {
		this.target = target;
		this.bonusChips = bonusChips;
		this.bonusMult = bonusMult;
	}

	/**
	 * Returns the combination this planet upgrades.
	 *
	 * @return the targeted hand type
	 */
	public HandType target() {
		return target;
	}

	/**
	 * Returns the bonus chips this planet adds to its targeted combination on each use.
	 *
	 * @return the chips bonus
	 */
	public int bonusChips() {
		return bonusChips;
	}

	/**
	 * Returns the bonus multiplier this planet adds to its targeted combination on each use.
	 *
	 * @return the multiplier bonus
	 */
	public int bonusMult() {
		return bonusMult;
	}

	/**
	 * Returns the human-readable French name of the planet (ex: {@code "Vénus"}).
	 *
	 * @return the display name of the planet
	 */
	public String displayName() {
		return switch (this) {
			case PLUTO -> "Pluton";
			case MERCURY -> "Mercure";
			case URANUS -> "Uranus";
			case VENUS -> "Vénus";
			case SATURN -> "Saturne";
			case JUPITER -> "Jupiter";
			case EARTH -> "Terre";
			case MARS -> "Mars";
			case NEPTUNE -> "Neptune";
		};
	}

	/**
	 * Returns the planet associated with the given combination (one-to-one mapping).
	 *
	 * @param type the hand type to look up
	 * @return the planet that upgrades {@code type}
	 * @throws IllegalArgumentException if no planet targets {@code type}
	 */
	public static Planet forHandType(HandType type) {
		for (var planet : values()) {
			if (planet.target() == type) {
				return planet;
			}
		}
		throw new IllegalArgumentException("Aucune planète associée à " + type);
	}
}
