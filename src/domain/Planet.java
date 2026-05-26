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

	public HandType target() {
		return target;
	}

	public int bonusChips() {
		return bonusChips;
	}

	public int bonusMult() {
		return bonusMult;
	}

	// Nom lisible affiché à l'utilisateur (en français)
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

	// Renvoie la planète qui cible la combinaison donnée (relation 1-1).
	public static Planet forHandType(HandType type) {
		for (var planet : values()) {
			if (planet.target() == type) {
				return planet;
			}
		}
		throw new IllegalArgumentException("Aucune planète associée à " + type);
	}
}
