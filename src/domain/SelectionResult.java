package domain;

import java.util.List;

/**
 * Carries the outcome of a selection made in a view: the chosen cards and whether
 * the player asked to discard them (instead of playing them).
 *
 * @param cards          the cards selected by the player
 * @param isDiscardAction {@code true} if the player chose to discard the selection, {@code false} to play it
 */
public record SelectionResult(List<Card> cards, boolean isDiscardAction) {

}
