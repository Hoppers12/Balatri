package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

import domain.Card;
import domain.Color;
import domain.Rank;

public final class Deck {

	private final List<Card> drawPile = new ArrayList<>(); // pioche
	private final List<Card> discardPile = new ArrayList<>(); // défausse

	/**
	 * Builds a full 52-card deck (13 ranks × 4 suits) and shuffles it.
	 */
	public Deck() {
		for (var color : Color.values()) {
			for (var rank : Rank.values()) {
				drawPile.add(new Card(rank, color));
			}
		}
		Collections.shuffle(drawPile);
	}

	/**
	 * Draws {@code n} cards from the draw pile. If the pile holds fewer than {@code n}
	 * cards, the discard pile is shuffled back into it beforehand.
	 *
	 * @param n the number of cards to draw
	 * @return the list of drawn cards
	 * @throws IllegalArgumentException if {@code n} is not strictly positive
	 * @throws IllegalStateException    if not enough cards are available even after recycling
	 */
	public List<Card> draw(int n) {
		if (n <= 0) {
			throw new IllegalArgumentException("Le nombre de cartes à piocher doit être positif.");
		}
		// Recycle la défausse si pas assez de carte dans la pioche
		if (drawPile.size() < n) {
			recycleDiscard();
		}
		// Test mais ça ne devrait pas arriver
		if (drawPile.size() < n) {
			throw new IllegalStateException("Pas assez de cartes disponibles dans le deck.");
		}
		// Pioche les dernières cartes
		var drawn = new ArrayList<Card>(n);
		for (var i = 0; i < n; i++) {
			var lastIndex = drawPile.size() - 1;
			var card = drawPile.get(lastIndex);
			drawPile.remove(lastIndex);
			drawn.add(card);
		}
		return drawn;
	}

	/**
	 * Adds the given cards to the discard pile.
	 *
	 * @param cards the cards to discard
	 * @throws NullPointerException if {@code cards} is null
	 */
	public void discard(List<Card> cards) {
		Objects.requireNonNull(cards);

		discardPile.addAll(cards);
	}

	/**
	 * Returns the number of cards remaining in the draw pile.
	 *
	 * @return the size of the draw pile
	 */
	public int restDrawPile() {
		return drawPile.size();
	}

	/**
	 * Shuffles the discard pile back into the draw pile and empties the discard pile.
	 */
	private void recycleDiscard() {
		Collections.shuffle(discardPile);
		// Mettre les cartes de la défausse dans la pioche
		drawPile.addAll(discardPile);
		// Vider la défausse
		discardPile.clear();
	}

	/**
	 * Resets the deck to a freshly shuffled full 52-card draw pile and clears the discard pile.
	 */
	public void reset() {
		drawPile.clear();
		discardPile.clear();

		for (var color : Color.values()) {
			for (var rank : Rank.values()) {
				drawPile.add(new Card(rank, color));
			}
		}

		Collections.shuffle(drawPile);
	}

}