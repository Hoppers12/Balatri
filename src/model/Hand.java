package model;

import domain.Card;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente les cartes détenues par le joueur durant un tour de jeu.
 */
public class Hand {
	private final List<Card> cardsInHand;
	private final List<Card> selectedCards;
	private static final int MAX_SELECTION = 5;

	/**
	 * Crée une nouvelle main avec les cartes piochées.
	 */
	public Hand(List<Card> initialCards) {
		this.cardsInHand = new ArrayList<>(initialCards);
		this.selectedCards = new ArrayList<>();
	}

	/**
	 * Sélectionne une carte de la main pour la placer dans la combinaison à jouer.
	 * 
	 */
	public boolean selectCard(Card card) {
		if (selectedCards.size() < MAX_SELECTION && cardsInHand.contains(card) && !selectedCards.contains(card)) {
			return selectedCards.add(card);
		}
		return false;
	}

	/**
	 * Retire une carte de la sélection actuelle.
	 */
	public void deselectCard(Card card) {
		selectedCards.remove(card);
	}

	/**
	 * Vérifie si la sélection est prête à être jouée (exactement 5 cartes).
	 */
	public boolean isSelectionValid() {
		return selectedCards.size() == MAX_SELECTION;
	}

	/**
	 * @return Une vue non modifiable des 8 cartes en main pour l'affichage.
	 */
	public List<Card> getCardsInHand() {
		return List.copyOf(cardsInHand);
	}

	/**
	 * @return La liste des 5 cartes sélectionnées pour le calcul du score.
	 */
	public List<Card> getSelectedCards() {
		return List.copyOf(selectedCards);
	}

	/**
	 * Calcule les cartes qui n'ont pas été choisies pour les envoyer à la défausse.
	 */
	public List<Card> getUnselectedCards() {
		List<Card> unselected = new ArrayList<>(cardsInHand);
		unselected.removeAll(selectedCards);
		return unselected;
	}

	/**
	 * Vide la sélection actuelle.
	 */
	public void clearSelection() {
		selectedCards.clear();
	}
}