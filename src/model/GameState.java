package model;

public final class GameState {
	// Règle de jeu : nombre de mains accordées au joueur pour battre chaque blind
	public static final int CARDS_DRAWN = 8;
	public static final int CARDS_PLAYED = 5;
	public static final int HANDS_PER_BLIND = 4;
	private static final int DISCARDS_PER_BLIND = 3;
	private static final int BASE_TARGET = 100; // cible du 1er blind
	private static final double GROWTH = 1.4; // augmenter les blinds à partir de ça

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

	/**
	 * Starts a new game: fresh deck, base hand levels, and the first blind set up.
	 */
	public GameState() {
		this.deck = new Deck();
		this.handLevels = new HandLevels();
		this.blindNumber = 0;
		this.totalScore = 0;
		this.blindsBeaten = 0;
		this.gameOver = false;
		setupNewBlind();
	}

	/**
	 * Prepares the state for the current blind: generates it, resets the blind score,
	 * the remaining hands and discards, and reshuffles the deck.
	 */
	private void setupNewBlind() {
		currentBlind = generateBlind(blindNumber);
		currentBlindScore = 0;
		handsRemaining = HANDS_PER_BLIND;
		discardsRemaining = DISCARDS_PER_BLIND;
		deck.reset();
	}

	/**
	 * Generates the blind for the given index, with a target score growing with the index.
	 *
	 * @param index the zero-based blind index
	 * @return the generated blind
	 */
	private static Blind generateBlind(int index) {
		var target = (int) Math.round(BASE_TARGET * Math.pow(GROWTH, index));
		return new Blind("Blind " + (index + 1), target);
	}

	/**
	 * Adds points to the current blind score and the total score, then checks progression.
	 *
	 * @param points the points scored by the last hand
	 */
	public void addScore(long points) {
		currentBlindScore += points;
		totalScore += points;
		checkWinCondition();
	}

	/**
	 * Checks whether the current blind is cleared (moves to the next one) or lost
	 * (no hands left), updating the game state accordingly.
	 */
	private void checkWinCondition() {
		if (currentBlindScore >= currentBlind.targetScore()) {
			blindsBeaten++;
			nextBlind(); // mode infini : on enchaîne toujours
		} else if (handsRemaining <= 0) {
			gameOver = true;
		}
	}

	/**
	 * Moves on to the next blind (endless mode: there is no final victory).
	 */
	private void nextBlind() {
		blindNumber++;
		setupNewBlind();
	}

	/**
	 * Consumes one of the remaining hands and ends the game if none are left
	 * while the blind target has not been reached.
	 */
	public void decrementHands() {
		handsRemaining--;
		if (handsRemaining <= 0 && currentBlindScore < currentBlind.targetScore()) {
			gameOver = true;
		}
	}

	/**
	 * Consumes one discard if any remain.
	 */
	public void useDiscard() {
		if (discardsRemaining > 0) {
			discardsRemaining--;
		}
	}

	/**
	 * Returns the current blind.
	 *
	 * @return the current blind
	 */
	public Blind getCurrentBlind() {
		return currentBlind;
	}

	/**
	 * Returns the one-based number of the current blind (for display).
	 *
	 * @return the current blind number
	 */
	public int getBlindNumber() {
		return blindNumber + 1;
	}

	/**
	 * Returns the deck used by the game.
	 *
	 * @return the deck
	 */
	public Deck getDeck() {
		return deck;
	}

	/**
	 * Returns the current chips/multiplier levels of every combination.
	 *
	 * @return the hand levels
	 */
	public HandLevels getHandLevels() {
		return handLevels;
	}

	/**
	 * Returns the score accumulated within the current blind.
	 *
	 * @return the current blind score
	 */
	public long getCurrentBlindScore() {
		return currentBlindScore;
	}

	/**
	 * Returns the total score accumulated since the start of the game.
	 *
	 * @return the total score
	 */
	public long getTotalScore() {
		return totalScore;
	}

	/**
	 * Returns the number of blinds beaten so far.
	 *
	 * @return the number of blinds beaten
	 */
	public int getBlindsBeaten() {
		return blindsBeaten;
	}

	/**
	 * Returns the number of hands remaining for the current blind.
	 *
	 * @return the remaining hands
	 */
	public int getHandsRemaining() {
		return handsRemaining;
	}

	/**
	 * Returns the number of discards remaining for the current blind.
	 *
	 * @return the remaining discards
	 */
	public int getDiscardsRemaining() {
		return discardsRemaining;
	}

	/**
	 * Tells whether the game is over.
	 *
	 * @return {@code true} if the game has ended
	 */
	public boolean isGameOver() {
		return gameOver;
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		sb.append("Etat actuel du jeu :\n");
		sb.append("Blind courant     : ").append(getBlindNumber()).append(" (cible ").append(currentBlind.targetScore()).append(")\n");
		sb.append("Score du blind    : ").append(currentBlindScore).append(" pts\n");
		sb.append("Score total       : ").append(totalScore).append(" pts\n");
		sb.append("Blinds battus     : ").append(blindsBeaten).append("\n");
		sb.append("Mains disponibles : ").append(handsRemaining).append("\n");
		sb.append("Défausses dispo   : ").append(discardsRemaining).append("\n");
		sb.append("Statut partie     : ").append(gameOver ? "Terminée" : "En cours").append("\n");
		return sb.toString();
	}
}