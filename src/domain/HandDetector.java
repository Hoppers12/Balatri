package domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;
import java.util.stream.Collectors;

//Classe utilitaire => non instaciable
public final class HandDetector {

	private static final int HAND_SIZE = 5;

	private HandDetector() {
	}

	/**
	 * Detects the best poker combination formed by exactly five cards.
	 * Handles the special low straight A-2-3-4-5.
	 *
	 * @param cards the five cards to evaluate
	 * @return the detected combination
	 * @throws IllegalArgumentException if {@code cards} is null or does not contain exactly five cards
	 */
	public static HandType detect(List<Card> cards) {
		Objects.requireNonNull(cards);
		if (cards.size() != HAND_SIZE) {
			throw new IllegalArgumentException("Une main doit contenir exactement " + HAND_SIZE + " cartes.");
		}

		var flush = isFlush(cards);
		var straight = isStraight(cards);

		if (flush && straight) {
			return HandType.STRAIGHT_FLUSH;
		}

		// Détecter les combinaisons à partir du rang (paire, double paire, brelan, full, carré)
		var counts = countRanks(cards);
		// Nombre max de cartes de mêmes rang
		var maxSameRank = counts.values().stream().mapToLong(Long::longValue).max().orElse(0);
		// Nombre de paire
		var pairCount = counts.values().stream().filter(c -> c == 2).count();

		if (maxSameRank == 4) {
			return HandType.FOUR_OF_A_KIND;
		}
		if (maxSameRank == 3 && pairCount == 1) {
			return HandType.FULL_HOUSE;
		}
		if (flush) {
			return HandType.FLUSH;
		}
		if (straight) {
			return HandType.STRAIGHT;
		}
		if (maxSameRank == 3) {
			return HandType.THREE_OF_KIND;
		}
		if (pairCount == 2) {
			return HandType.TWO_PAIR;
		}
		if (pairCount == 1) {
			return HandType.PAIR;
		}
		return HandType.HIGH_CARD;
	}

	/**
	 * Indicates whether all five cards share the same suit.
	 *
	 * @param cards the cards to test
	 * @return {@code true} if the cards form a flush
	 */
	private static boolean isFlush(List<Card> cards) {
		var first = cards.get(0).color();
		return cards.stream()
								.allMatch(c -> c.color() == first);
	}

	/**
	 * Indicates whether the five cards form a straight (consecutive values),
	 * including the special A-2-3-4-5 case.
	 *
	 * @param cards the cards to test
	 * @return {@code true} if the cards form a straight
	 */
	private static boolean isStraight(List<Card> cards) {
		var values = cards.stream()
											.mapToInt(c -> c.rank().value())
											.sorted()
											.toArray();

		// Cas A-2-3-4-5 : l'as (14) comme 1
		if (Arrays.equals(values, new int[] { 2, 3, 4, 5, 14 })) {
			return true;
		}

		// Cas 10-V-D-R-A est auto (l'as vaut 14 cest consécutif après le roi)
		for (var i = 1; i < values.length; i++) {
			if (values[i] != values[i - 1] + 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Counts how many cards share each rank.
	 *
	 * @param cards the cards to count
	 * @return a map from each rank to the number of cards of that rank
	 */
	private static Map<Rank, Long> countRanks(List<Card> cards) {
		return cards.stream()
        				.collect(Collectors.groupingBy(Card::rank, Collectors.counting()));
	}
}