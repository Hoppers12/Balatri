package model;

public final class HighScore {
    private long bestScore;
    private int bestBlinds;

    // Enregistre un résultat + true si c'est un nouveau record de score.
    public boolean submit(long score, int blindsBeaten) {
        boolean newRecord = score > bestScore;
        if (score > bestScore) {
            bestScore = score;
        }
        if (blindsBeaten > bestBlinds) {
            bestBlinds = blindsBeaten;
        }
        return newRecord;
    }

    public long getBestScore()  { return bestScore; }
    public int  getBestBlinds() { return bestBlinds; }
}