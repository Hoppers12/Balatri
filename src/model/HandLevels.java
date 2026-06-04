package model;

import domain.Card;
import domain.HandType;
import domain.Planet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// Gère les niveaux et les scores (chips et multiplicateur)
public final class HandLevels {

	/**
	 * Validates a level entry: level and multiplier strictly positive, chips non-negative.
	 *
	 * @throws IllegalArgumentException if any value is out of range
	 */
	private record LevelInfo(int level, int chips, int multiplier) {
		public LevelInfo {
			if (level <= 0 || chips < 0 || multiplier <= 0) {
				throw new IllegalArgumentException("Valeurs invalides");
			}
		}
	}

	private final Map<HandType, LevelInfo> scores = new HashMap<>();

	/**
	 * Initializes every combination at level 1 with its base chips and multiplier.
	 */
	public HandLevels() {
		for (var type : HandType.values()) {
			scores.put(type, new LevelInfo(1, type.baseChips(), type.baseMult()));
		}
	}

	/**
	 * Applies a planet's bonus to its targeted combination, permanently raising its level,
	 * chips and multiplier.
	 *
	 * @param planet the planet whose bonus is applied
	 * @throws NullPointerException if {@code planet} is null
	 */
	public void upgrade(Planet planet) {
		Objects.requireNonNull(planet);
		var target = planet.target();
		var info = scores.get(target);

		scores.put(target,
				new LevelInfo(info.level() + 1, info.chips() + planet.bonusChips(), info.multiplier() + planet.bonusMult()));
	}

	/**
	 * Returns the current chips of the given combination.
	 *
	 * @param type the combination
	 * @return its current chips
	 * @throws NullPointerException if {@code type} is null
	 */
	public int getChipsFor(HandType type) {
		Objects.requireNonNull(type);
		return scores.get(type).chips();
	}

	/**
	 * Returns the current multiplier of the given combination.
	 *
	 * @param type the combination
	 * @return its current multiplier
	 * @throws NullPointerException if {@code type} is null
	 */
	public int getMultiplierFor(HandType type) {
		Objects.requireNonNull(type);
		return scores.get(type).multiplier();
	}

	/**
	 * Returns the current level of the given combination.
	 *
	 * @param type the combination
	 * @return its current level
	 * @throws NullPointerException if {@code type} is null
	 */
	public int getLevelFor(HandType type) {
		Objects.requireNonNull(type);
		return scores.get(type).level();
	}

	/**
	 * Sums the chip value of the played cards (Score-by-cards extension):
	 * each card contributes its rank value.
	 *
	 * @param selected the played cards
	 * @return the total chip value of the cards
	 * @throws NullPointerException if {@code selected} is null
	 */
	public int getChipsForCards(List<Card> selected) {
		Objects.requireNonNull(selected);
		return selected.stream()
									 .mapToInt(c -> c.rank().value())
									 .sum();
	}
}