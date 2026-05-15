package model;

import domain.HandType;
import domain.Planet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Gère les niveaux et les scores (chips et multiplicateur)
 */
public class HandLevels {

	// record interne
	private record LevelInfo(int level, int chips, int multiplier) {
		public LevelInfo {
			if (level <= 0 || chips < 0 || multiplier <= 0) {
				throw new IllegalArgumentException("Valeurs invalides");
			}
		}
	}

	// Utilisation d'une HashMap à la place de l'EnumMap
	private final Map<HandType, LevelInfo> scores = new HashMap<>();

	/**
	 * Initialise les niveaux de main avec les valeurs de base du projet.
	 */
	public HandLevels() {
		for (var type : HandType.values()) {
			scores.put(type, new LevelInfo(1, type.baseChips(), type.baseMult()));
		}
	}

//Applique le bonus d'une planète sur une combinaison
	public void upgrade(Planet planet) {
		Objects.requireNonNull(planet);
		HandType target = planet.target();
		LevelInfo info = scores.get(target);

		scores.put(target, new LevelInfo(info.level() + 1, info.chips() + planet.bonusChips(),
				info.multiplier() + planet.bonusMult()));
	}

	public long getChipsFor(HandType type) {
		return scores.get(type).chips;
	}

	public int getMultiplierFor(HandType type) {
		return scores.get(type).multiplier;
	}

	public int getLevelFor(HandType type) {
		return scores.get(type).level;
	}
}