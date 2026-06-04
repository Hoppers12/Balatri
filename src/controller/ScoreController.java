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

	/**
	 * Computes the score of a played hand: {@code chips × multiplier} of the combination,
	 * plus the chip value of the played cards (Score-by-cards extension).
	 *
	 * @param type     the detected combination
	 * @param levels   the current chips/multiplier levels
	 * @param selected the played cards
	 * @return the score of the hand
	 * @throws NullPointerException if any argument is null
	 */
	public static int getScore(HandType type, HandLevels levels, List<Card> selected) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(levels);
		Objects.requireNonNull(selected);

		return (levels.getChipsFor(type) * levels.getMultiplierFor(type) + levels.getChipsForCards(selected));
	}
}