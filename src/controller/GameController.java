package controller;

import java.util.Objects;

import domain.HandDetector;
import domain.Planet;
import domain.SelectionResult;
import model.GameState;
import model.Hand;
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

		// Pioche les cartes du tour (8 par défaut, cf. Hand.CARDS_DRAWN)
		var cards = state.getDeck().draw(Hand.CARDS_DRAWN);
		boolean hasPlayed = false;

    // 2. On boucle tant que le joueur décide de défausser (discard)
    while (!hasPlayed) {
    		view.showHand(cards);
    
    		// Sélectionne CARDS_PLAYED cartes parmi celles piochées (5 par défaut)
    		var selectedObject = view.askSelection(cards);
    		//Extraction de la liste de carte sélectionnée de l'objet SelectionResult
    		var selected = selectedObject.cards();
    		
    		if(selectedObject.isDiscardAction()) {
        state.getDeck().discard(selected);
        cards.removeAll(selected);
        int nbCartesAPiocher = selected.size();
        if (nbCartesAPiocher > 0) {
            var newlyDrawnCards = state.getDeck().draw(nbCartesAPiocher);  
            //AJout des cartes nouvellement piochées pour remplacer
            cards.addAll(newlyDrawnCards);
        }
  		}else {
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
  	    hasPlayed = true;
  		}
    }
	}

	// Tirage planète
	private static Planet randomPlanet() {
		var planets = Planet.values();
		var index = (int) (Math.random() * planets.length);
		return planets[index];
	}
}