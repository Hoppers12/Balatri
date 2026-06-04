package view;

import java.util.List;
import java.util.Comparator;

import domain.Card;
import domain.HandType;
import domain.Planet;
import domain.SelectionResult;
import model.GameState;
import model.HighScore;

public sealed interface View permits ConsoleView, GraphicalView {

	/**
	 * Returns a copy of the given cards sorted by ascending rank value.
	 *
	 * @param cards the cards to sort
	 * @return a new list sorted by rank
	 */
	static List<Card> sortByRank(List<Card> cards) {
		return cards.stream()
								.sorted(Comparator.comparingInt(c -> c.rank().value()))
								.toList();
	}

	/**
	 * Displays the current game state (blind, score, remaining hands, levels).
	 *
	 * @param state the state to display
	 */
	void showState(GameState state);

	/**
	 * Displays the cards drawn this turn.
	 *
	 * @param handCards the drawn cards
	 */
	void showHand(List<Card> handCards);

	/**
	 * Asks the player to select cards, returning the selection and whether it is a discard.
	 *
	 * @param handCards the cards available for selection
	 * @return the player's selection and intent (play or discard)
	 */
	SelectionResult askSelection(List<Card> handCards);

	/**
	 * Displays the result of a played hand.
	 *
	 * @param type  the combination formed
	 * @param score the score gained
	 */
	void showPlay(HandType type, int score);

	/**
	 * Announces the planet obtained after beating a blind.
	 *
	 * @param planet the planet won
	 */
	void showPlanetWon(Planet planet);

	/**
	 * Shows the end-of-game screen with the final result and the session high score.
	 *
	 * @param state     the final game state
	 * @param highScore the session high score
	 * @param record    {@code true} if this game set a new record
	 */
	void showEnd(GameState state, HighScore highScore, boolean record);

	/**
	 * Asks the player whether they want to play another game.
	 *
	 * @return {@code true} to play again
	 */
	boolean askReplay();
}