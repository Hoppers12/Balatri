package model;

public final class HighScore {
	private long bestScore;
	private int bestBlinds;

	/**
	 * Records a finished game's result and keeps the best score and best blind count
	 * of the session.
	 *
	 * @param score        the total score of the finished game
	 * @param blindsBeaten the number of blinds beaten in the finished game
	 * @return {@code true} if {@code score} is a new best score
	 */
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

	/**
	 * Returns the best total score recorded this session.
	 *
	 * @return the best score
	 */
	public long getBestScore() {
		return bestScore;
	}

	/**
	 * Returns the highest number of blinds beaten this session.
	 *
	 * @return the best blind count
	 */
	public int getBestBlinds() {
		return bestBlinds;
	}
}