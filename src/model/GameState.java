package model;

public class GameState {
	// Règle de jeu : nombre de mains accordées au joueur pour battre chaque blind
	public static final int HANDS_PER_BLIND = 4;
	private static final int DISCARDS_PER_BLIND = 3;
	private static final int BASE_TARGET = 100; // cible du 1er blind
	private static final double GROWTH = 1.3; // augmenter les blinds à partir de ça

	private final Deck deck;
	private final HandLevels handLevels;

	private int blindNumber;
	private Blind currentBlind;
	private long currentBlindScore;
	private long totalScore;
	private int blindsBeaten;
	private int handsRemaining;
	private int discardsRemaining;
	private boolean gameOver;

	// Init une nouvelle partie
	public GameState() {
		this.deck = new Deck();
		this.handLevels = new HandLevels();
		this.blindNumber = 0;
		this.totalScore = 0;
		this.blindsBeaten = 0;
		this.gameOver = false;
		setupNewBlind();
	}

	// Prépare les variables pour le blind courant.
	private void setupNewBlind() {
		currentBlind = generateBlind(blindNumber);
		currentBlindScore = 0;
		handsRemaining = HANDS_PER_BLIND;
		discardsRemaining = DISCARDS_PER_BLIND;
		deck.reset();
	}

	private static Blind generateBlind(int index) {
		int target = (int) Math.round(BASE_TARGET * Math.pow(GROWTH, index));
		return new Blind("Blind " + (index + 1), target);
	}

	public void addScore(long points) {
		currentBlindScore += points;
		totalScore += points;
		checkWinCondition();
	}

	private void checkWinCondition() {
		if (currentBlindScore >= currentBlind.targetScore()) {
			blindsBeaten++;
			nextBlind(); // mode infini : on enchaîne toujours
		} else if (handsRemaining <= 0) {
			gameOver = true;
		}
	}

	private void nextBlind() { // pas de victoire : ça continue
		blindNumber++;
		setupNewBlind();
	}

	public void decrementHands() {
		handsRemaining--;
		if (handsRemaining <= 0 && currentBlindScore < currentBlind.targetScore()) {
			gameOver = true;
		}
	}

	public void useDiscard() {
		if (discardsRemaining > 0) {
			discardsRemaining--;
		}
	}

	public Blind getCurrentBlind() {
		return currentBlind;
	}

	public int getBlindNumber() {
		return blindNumber + 1;
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

	public long getTotalScore() {
		return totalScore;
	}

	public int getBlindsBeaten() {
		return blindsBeaten;
	}

	public int getHandsRemaining() {
		return handsRemaining;
	}

	public int getDiscardsRemaining() {
		return discardsRemaining;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		sb.append("Etat actuel du jeu :\n");
		sb.append("Blind courant     : ").append(getBlindNumber()).append(" (cible ").append(currentBlind.targetScore())
				.append(")\n");
		sb.append("Score du blind    : ").append(currentBlindScore).append(" pts\n");
		sb.append("Score total       : ").append(totalScore).append(" pts\n");
		sb.append("Blinds battus     : ").append(blindsBeaten).append("\n");
		sb.append("Mains disponibles : ").append(handsRemaining).append("\n");
		sb.append("Défausses dispo   : ").append(discardsRemaining).append("\n");
		sb.append("Statut partie     : ").append(gameOver ? "Terminée" : "En cours").append("\n");
		return sb.toString();
	}
}