package controller;

import java.util.Objects;

import domain.HandType;
import model.HandLevels;

public final class ScoreController {

	// classe utilitaire non instanciable
	private ScoreController() {
	}

	public static int getScore(HandType type, HandLevels levels) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(levels);
		return (levels.getChipsFor(type) * levels.getMultiplierFor(type));
	}
}