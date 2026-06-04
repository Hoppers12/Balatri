package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import domain.Card;
import domain.HandType;
import domain.Planet;
import domain.SelectionResult;
import model.GameState;
import model.HighScore;

public final class ConsoleView implements View {

	/**
	 * {@inheritDoc}
	 * Prints the textual state to the console.
	 */
	@Override
	public void showState(GameState state) {
		Objects.requireNonNull(state);
		IO.println(state);

	}

	/**
	 * {@inheritDoc}
	 * Prints each drawn card with its index, sorted by ascending rank.
	 */
	@Override
	public void showHand(List<Card> handCards) {
		Objects.requireNonNull(handCards);
		var sorted = View.sortByRank(handCards);
		var i = 0;
		for (var carte : sorted) {
			IO.println("Carte " + i + " :" + carte);
			i++;
		}
	}

	/**
	 * {@inheritDoc}
	 * Reads card indices one by one; typing {@code "D"} discards the cards selected so far.
	 */
	@Override
	public SelectionResult askSelection(List<Card> handCards) {
		Objects.requireNonNull(handCards);

		var choosen = new ArrayList<Card>();
		var alreadyChoosen = new ArrayList<Integer>();

		IO.println("\nChoisissez 5 cartes parmi les 8 (un indice à la fois, entrée après chaque).");
		IO.println("Tapez 'D' pour défausser les cartes déjà sélectionnées et repiocher.\n");

		while (choosen.size() < GameState.CARDS_PLAYED) {
			var line = IO.readln("Carte n°" + (choosen.size() + 1) + " : ").trim();

			// Action de défausse (testée AVANT le parse numérique)
			if (line.equalsIgnoreCase("D")) {
				if (choosen.isEmpty()) {
					IO.println("Aucune carte sélectionnée à défausser.");
					continue;
				}
				IO.println("Cartes défaussées et repiochées : " + choosen);
				return new SelectionResult(List.copyOf(choosen), true);
			}

			// À partir d'ici : uniquement du numérique
			if (!line.matches("\\d+")) {
				IO.println("Entrée invalide ! Un nombre entier, ou D pour défausser.");
				continue;
			}
			var index = Integer.parseInt(line);
			if (index < 0 || index >= handCards.size()) {
				IO.println("Index invalide ! Entre 0 et " + (handCards.size() - 1) + ".");
			} else if (alreadyChoosen.contains(index)) {
				IO.println("Carte déjà sélectionnée !");
			} else {
				alreadyChoosen.add(index);
				choosen.add(handCards.get(index));
			}
		}

		return new SelectionResult(List.copyOf(choosen), false);
	}

	/**
	 * {@inheritDoc}
	 * Prints the combination and the score obtained.
	 */
	@Override
	public void showPlay(HandType type, int score) {
		Objects.requireNonNull(type);
		IO.println("Main réalisée " + type + " \n score : " + score);

	}

	/**
	 * {@inheritDoc}
	 * Prints the won planet.
	 */
	@Override
	public void showPlanetWon(Planet planet) {
		Objects.requireNonNull(planet);
		IO.println("\n Félicitations vous avez terminés une blind \n");
		IO.println("Vous avez gagnés la planète : " + planet);
	}

	/**
	 * {@inheritDoc}
	 * Prints the total score, blinds beaten and best score, plus a record notice.
	 */
	@Override
	public void showEnd(GameState state, HighScore highScore, boolean newRecord) {
		Objects.requireNonNull(state);
		IO.println("Partie terminée");
		IO.println("Score total    : " + state.getTotalScore() + " pts");
		IO.println("Blinds battus  : " + state.getBlindsBeaten());
		if (newRecord) {
			IO.println("Nouveau record de score !");
		}
		IO.println("Meilleur score : " + highScore.getBestScore() + " pts (" + highScore.getBestBlinds() + " blinds)");
	}

	/**
	 * {@inheritDoc}
	 * Reads a yes/no answer from the console.
	 */

	@Override
	public boolean askReplay() {
		var answer = IO.readln("Souhaitez-vous rejouer ? (o/n) : ");
		return answer.startsWith("o");
	}

}