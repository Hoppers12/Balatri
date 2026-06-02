package controller;

import java.util.ArrayList;
import java.util.Objects;

import domain.HandDetector;
import domain.Planet;
import model.GameState;
import model.Hand;
import model.HighScore;
import view.View;

public final class GameController {

	private final GameState state;
	private final View view;
	private final HighScore highScore;

	public GameController(GameState state, View view, HighScore highScore) {
    this.state = Objects.requireNonNull(state);
    this.view = Objects.requireNonNull(view);
    this.highScore = Objects.requireNonNull(highScore);
	}

	// Boucle principale
	public void run() {
		while (!state.isGameOver()) {
			playOneRound();
		}
		boolean record = highScore.submit(state.getTotalScore(), state.getBlindsBeaten());
    view.showEnd(state, highScore, record);
	}

	// Tour complet : pioche, sélection, score, défausse
	private void playOneRound() {
		view.showState(state);
		// Pioche les cartes du tour (8 par défaut, cf. Hand.CARDS_DRAWN)
		var cards = new ArrayList<>(View.sortByRank(state.getDeck().draw(Hand.CARDS_DRAWN)));
		
		boolean hasPlayed = false;

    // On boucle tant que le joueur décide de défausser (discard)
    while (!hasPlayed) {
    		view.showHand(cards);
    
    		// Sélectionne CARDS_PLAYED cartes parmi celles piochées (5 par défaut)
    		var selectedObject = view.askSelection(cards);
    		//Extraction de la liste de carte sélectionnée de l'objet SelectionResult
    		var selected = selectedObject.cards();
    		
    		if(selectedObject.isDiscardAction()) {
    			if (state.getDiscardsRemaining() <= 0 || selected.isEmpty()) {
            continue; // défausse refusée -> on re-demande
        }
        state.useDiscard();
        
        // On pioche AVANT de défausser (évite de repiocher les cartes jetées)
        var replacements = state.getDeck().draw(selected.size());
        state.getDeck().discard(selected);

        // Remplacement = les cartes défaussées changent
        int r = 0;
        for (var card : selected) {
            int pos = cards.indexOf(card);
            if (pos >= 0) {
                cards.set(pos, replacements.get(r++));
            }
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
  	    var blindWon = state.getCurrentBlind() != blindBefore;
  
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