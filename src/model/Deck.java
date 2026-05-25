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

	// Les 52 cartes (13 rangs x 4 couleurs)
	public Deck() {
		for (var color : Color.values()) {
			for (var rank : Rank.values()) {
				drawPile.add(new Card(rank, color));
			}
		}
		Collections.shuffle(drawPile);
	}

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

	public void discard(List<Card> cards) {
		Objects.requireNonNull(cards);
		for (var card : cards) {
			discardPile.add(card);
		}
	}

	public int restDrawPile() {
		return drawPile.size();
	}

	// Mélange la défausse et met dans la pioche
	private void recycleDiscard() {
		Collections.shuffle(discardPile);
		// Mettre les cartes de la défausse dans la pioche
		for (var card : discardPile) {
			drawPile.add(card);
		}
		// Vider la défausse
		while (!discardPile.isEmpty()) {
			discardPile.removeLast();
		}
	}

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