package controller;

import java.util.List;
import java.util.Objects;

import domain.Card;
import domain.HandType;
import model.HandLevels;

public final class ScoreController {

	// Classe utilitaire => non instanciable
	private ScoreController() {
	}
	

	public static int getScore(HandType type, HandLevels levels, List<Card> selected) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(levels);
    Objects.requireNonNull(selected);
    
		return (levels.getChipsFor(type) * levels.getMultiplierFor(type)+levels.getChipsForCards(selected));
	}
}