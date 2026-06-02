package model;

import domain.Card;
import domain.HandType;
import domain.Planet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// Gère les niveaux et les scores (chips et multiplicateur)
public class HandLevels {

	// Record interne
	private record LevelInfo(int level, int chips, int multiplier) {
		public LevelInfo {
			if (level <= 0 || chips < 0 || multiplier <= 0) {
				throw new IllegalArgumentException("Valeurs invalides");
			}
		}
	}

	private final Map<HandType, LevelInfo> scores = new HashMap<>();

	// Init les niveaux de main avec les valeurs de base.
	public HandLevels() {
		for (var type : HandType.values()) {
			scores.put(type, new LevelInfo(1, type.baseChips(), type.baseMult()));
		}
	}

	// Applique le bonus d'une planète sur une combinaison
	public void upgrade(Planet planet) {
		Objects.requireNonNull(planet);
		var target = planet.target();
		var info = scores.get(target);

		scores.put(target, new LevelInfo(info.level() + 1, info.chips() + planet.bonusChips(),
				info.multiplier() + planet.bonusMult()));
	}

	public int getChipsFor(HandType type) {
	  Objects.requireNonNull(type);
		return scores.get(type).chips();
	}

	public int getMultiplierFor(HandType type) {
	   Objects.requireNonNull(type);
		return scores.get(type).multiplier();
	}

	public int getLevelFor(HandType type) {
	   Objects.requireNonNull(type);
		return scores.get(type).level();
	}
	
	//Donne la valeur des 5 cartes de la combinaison (extension Score par cartes)
	public int getChipsForCards(List<Card> selected) {
	   Objects.requireNonNull(selected);
	   var valChipsCards = selected.stream().mapToInt(val->val.rank().value()).sum();
	   
	   return valChipsCards;
	}
}