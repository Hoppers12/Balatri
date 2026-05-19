package model;

import java.util.List;
import java.util.Objects;

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

	// Prépare les variables pour le blind courant.
	private void setupNewBlind() {
		currentBlindScore = 0;
		handsRemaining = 4; // A augmenter aléatoirement ?
		deck.reset();
	}

	public void addScore(long points) {
		currentBlindScore += points;
		checkWinCondition();
	}

	private void checkWinCondition() {
		var current = getCurrentBlind();
		if (currentBlindScore >= current.targetScore()) {
			nextBlind();
		} else if (handsRemaining <= 0) {
			gameOver = true;
		}
	}

	private void nextBlind() {
		if (currentBlindIndex < blinds.size() - 1) {
			currentBlindIndex++;
			setupNewBlind();
		} else {
			gameWon = true;
			gameOver = true;
		}
	}

	public void decrementHands() {
		handsRemaining--;
		if (handsRemaining <= 0 && currentBlindScore < getCurrentBlind().targetScore()) {
			gameOver = true;
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
		var sb = new StringBuilder();
		sb.append("Etat actuel du jeu : \n");

		sb.append("Blind actuelle     : ").append(currentBlindIndex + 1).append(" / ").append(blinds.size()).append("\n");
		sb.append("Score accumulé     : ").append(currentBlindScore).append(" pts\n");
		sb.append("Mains disponibles  : ").append(handsRemaining).append("\n");
		sb.append("Statut victoire    : ").append(getVictoireStatut()).append("\n");
		sb.append("Statut partie      : ").append(gameOver ? "Terminée" : "En cours").append("\n");
		sb.append("Blinds à affronter : ").append(blinds).append("\n");

		return sb.toString();
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