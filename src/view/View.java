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

	//Tri une main par hauteur croissante.
	static List<Card> sortByRank(List<Card> cards) {
		return cards.stream()
								.sorted(Comparator.comparingInt(c -> c.rank().value()))
								.toList();
	}
	
	// Affiche l'état courant (blind, score cumulé, mains restantes, niveaux).
	void showState(GameState state);

	// Affiche les 8 cartes piochées.
	void showHand(List<Card> handCards);

	// Demande au joueur de sélectionner 5 cartes parmi les 8 passées.
	SelectionResult askSelection(List<Card> handCards);

	// Affiche le résultat d'une main jouée (combinaison + score gagné).
	void showPlay(HandType type, int score);

	// Planète obtenue après un blind battu.
	void showPlanetWon(Planet planet);

	// Annonce la fin de partie (gagnée ou perdue).
	void showEnd(GameState state, HighScore highScore, boolean record);
	boolean askReplay();
}