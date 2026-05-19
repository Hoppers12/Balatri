package domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

//Classe utilitaire => non instaciable
public final class HandDetector {

	private static final int HAND_SIZE = 5;

	private HandDetector() {
	}

	public static HandType detect(List<Card> cards) {
		if (cards == null || cards.size() != HAND_SIZE) {
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
		var maxSameRank = counts.values().stream()
														.mapToInt(Integer::intValue)
														.max()
														.orElse(0);
		// Nombre de paire
		var pairCount = counts.values().stream()
													.filter(c -> c == 2)
													.count();

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

	// == fonctionne car Color est un enum
	private static boolean isFlush(List<Card> cards) {
		var first = cards.get(0).color();
		// Toutes la même couleur ?
		return cards.stream()
								.allMatch(c -> c.color() == first);
	}

	private static boolean isStraight(List<Card> cards) {
		// Recupere les vals triées
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

	private static Map<Rank, Integer> countRanks(List<Card> cards) {
		var counts = new HashMap<Rank, Integer>();
		for (var c : cards) {
			counts.put(c.rank(), counts.getOrDefault(c.rank(), 0) + 1);
		}
		return counts;
	}
}