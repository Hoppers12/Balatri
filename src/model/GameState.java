package model;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class GameState {
	private final Deck deck;
	private final HandLevels handLevels;
	private final List<Blind> blinds;

	private int currentBlindIndex;
	private long currentBlindScore;
	private int handsRemaining;
	private boolean gameWon;
	private boolean gameOver;

// Init une nouvelle partie
	public GameState(List<Blind> blinds) {
		Objects.requireNonNull(blinds);
		if (blinds.isEmpty()) {
			throw new IllegalArgumentException("La liste des blinds ne doit pas être vide.");
		}
		this.deck = new Deck();
		this.handLevels = new HandLevels();
		this.blinds = blinds;
		this.currentBlindIndex = 0;
		this.gameWon = false;
		this.gameOver = false;
		setupNewBlind();
	}

	/**
	 * Prépare les variables pour le blind courant.
	 */
	private void setupNewBlind() {
		this.currentBlindScore = 0;
		this.handsRemaining = 4;
		this.deck.reset();
	}

	public void addScore(long points) {
		this.currentBlindScore += points;
		checkWinCondition();
	}

	private void checkWinCondition() {
		var current = getCurrentBlind();
		if (currentBlindScore >= current.targetScore()) {
			nextBlind();
		} else if (handsRemaining <= 0) {
			this.gameOver = true;
		}
	}

	private void nextBlind() {
		if (currentBlindIndex < blinds.size() - 1) {
			currentBlindIndex++;
			setupNewBlind();
		} else {
			this.gameWon = true;
			this.gameOver = true;
		}
	}

	public void decrementHands() {
		this.handsRemaining--;
		if (handsRemaining <= 0 && currentBlindScore < getCurrentBlind().targetScore()) {
			this.gameOver = true;
		}
	}

	public Blind getCurrentBlind() {
		return blinds.get(currentBlindIndex);
	}

	public Deck getDeck() {
		return deck;
	}

	public HandLevels getHandLevels() {
		return handLevels;
	}

	public long getCurrentBlindScore() {
		return currentBlindScore;
	}

	public int getHandsRemaining() {
		return handsRemaining;
	}

	public boolean isGameWon() {
		return gameWon;
	}

	public boolean isGameOver() {
		return gameOver;
	}
	@Override
	public String toString() {
	    StringJoiner sj = new StringJoiner("\n", "Etat actuel du jeu : \n", "\n");

	    sj.add("Blind actuelle     : " + (currentBlindIndex + 1) + " / " + blinds.size());
	    sj.add("Score accumulé     : " + currentBlindScore + " pts");
	    sj.add("Mains disponibles  : " + handsRemaining);
	    sj.add("Statut victoire    : " + getVictoireStatut());
	    sj.add("Statut partie      : " + (gameOver ? "Terminée" : "En cours"));
	    sj.add("Blinds à affronter : " + blinds);
	    
	    return sj.toString();
	}

	private String getVictoireStatut() {
	    if (gameWon) {
	        return "Gagné";
	    } else if (gameOver) {
	        return "Perdu";
	    } else {
	        return "En cours";
	    }
	}
	
}