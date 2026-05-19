package controller;

import java.util.Objects;

import domain.HandDetector;
import domain.Planet;
import model.GameState;
import view.View;

public final class GameController {

	private final GameState state;
	private final View view;

	public GameController(GameState state, View view) {
		Objects.requireNonNull(state);
		Objects.requireNonNull(view);
		this.state = state;
		this.view = view;
	}

	// Boucle principale
	public void run() {
		while (!state.isGameOver()) {
			playOneRound();
		}
		view.showEnd(state);
	}

	// Tour complet : pioche, sélection, score, défausse
	private void playOneRound() {
		view.showState(state);

		// Pioche 8 cartes
		var cards = state.getDeck().draw(8);
		view.showHand(cards);

		// Selectionne 5 cartes parmi les 8
		var selected = view.askSelection(cards);

		// Détecte la combinaison et calcule le score
		var type = HandDetector.detect(selected);
		var score = ScoreController.getScore(type, state.getHandLevels());
		view.showPlay(type, score);

		// Gestion du score (peut déclencher nextBlind)
		var blindBefore = state.getCurrentBlind();
		state.addScore(score);
		// Blind battu si le blind a changé OU si la partie a été gagnée (dernier blind)
		var blindWon = state.getCurrentBlind() != blindBefore || state.isGameWon();

		if (blindWon) {
			// Planète aléatoire
			var planet = randomPlanet();
			state.getHandLevels().upgrade(planet);
			view.showPlanetWon(planet);
		} else {
			// Blind pas battu : les 8 cartes vont à la défausse et on perd une main
			state.getDeck().discard(cards);
			state.decrementHands();
		}
	}

	// Tirage planète
	private static Planet randomPlanet() {
		var planets = Planet.values();
		var index = (int) (Math.random() * planets.length);
		return planets[index];
	}
}