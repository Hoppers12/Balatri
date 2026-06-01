package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import domain.Card;
import domain.HandType;
import domain.Planet;
import domain.SelectionResult;
import model.GameState;

public final class ConsoleView implements View {

	@Override
	public void showState(GameState state) {
		Objects.requireNonNull(state);
		IO.println(state);

	}

	@Override
	public void showHand(List<Card> handCards) {
		Objects.requireNonNull(handCards);
		var sorted = View.sortByRank(handCards);
		int i = 0;
		for (Card carte : sorted) {
			IO.println("Carte " + i + " :" + carte);
			i++;
		}
	}

	@Override
	public SelectionResult askSelection(List<Card> handCards) {
		Objects.requireNonNull(handCards);

		var choosen = new ArrayList<Card>();
		var alreadyChoosen = new ArrayList<Integer>();

		IO.println("\nChoisissez 5 cartes parmi les 8 (un indice à la fois, entrée après chaque).");
		IO.println("Tapez 'D' pour défausser les cartes déjà sélectionnées et repiocher.\n");

		while (choosen.size() < 5) {
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

	@Override
	public void showPlay(HandType type, int score) {
		Objects.requireNonNull(type);
		IO.println("Main réalisée " + type + " \n score : " + score);

	}

	@Override
	public void showPlanetWon(Planet planet) {
		Objects.requireNonNull(planet);
		IO.println("\n Félicitations vous avez terminés une blind \n");
		IO.println("Vous avez gagnés la planète : " + planet);
	}

	@Override
	public void showEnd(GameState state) {
		Objects.requireNonNull(state);
		if (state.isGameWon()) {
			IO.println("Tu as gagné la partie en réussissant à battre les blinds !");

		} else {
			IO.println("Tu as perdu la partie toutes tes mains ont été consommées");
		}

	}

}