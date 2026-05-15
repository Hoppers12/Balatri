package model;

import java.util.List;

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
        Blind current = getCurrentBlind();
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

    public Deck getDeck() { return deck; }
    public HandLevels getHandLevels() { return handLevels; }
    public long getCurrentBlindScore() { return currentBlindScore; }
    public int getHandsRemaining() { return handsRemaining; }
    public boolean isGameWon() { return gameWon; }
    public boolean isGameOver() { return gameOver; }
}