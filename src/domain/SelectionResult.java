package domain;

import java.util.List;

/* Record représentant les cartes sélectionnées par l'utilisateur
 * permet de savoir si l'action de les discard à été choisie par le user
 */
public record SelectionResult(List<Card> cards, boolean isDiscardAction) {
  
}
