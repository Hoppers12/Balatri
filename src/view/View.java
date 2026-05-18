package view;

import java.util.List;

import domain.Card;
import domain.HandType;
import domain.Planet;
import model.GameState;

public sealed interface View permits ConsoleView, GraphicalView {

	// Affiche l'état courant (blind, score cumulé, mains restantes, niveaux).
	void showState(GameState state);

	// Affiche les 8 cartes piochées.
	void showHand(List<Card> handCards);

	// Demande au joueur de sélectionner 5 cartes parmi les 8 passées.
	List<Card> askSelection(List<Card> handCards);

	// Affiche le résultat d'une main jouée (combinaison + score gagné).
	void showPlay(HandType type, int score);

	// Planète obtenue après un blind battu.
	void showPlanetWon(Planet planet);

	// Annonce la fin de partie (gagnée ou perdue).
	void showEnd(GameState state);
}