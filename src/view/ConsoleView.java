package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import domain.Card;
import domain.HandType;
import domain.Planet;
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
		int i = 0 ;
		for (Card carte :handCards) {
			IO.println("Carte " + i + " :"  + carte);
			i++;
		}
		
	}

	@Override
	public List<Card> askSelection(List<Card> handCards) {
    Objects.requireNonNull(handCards);
		var choosen = new ArrayList<Card>(); 
		var alreadyChoosen = new ArrayList<Integer>();
		Scanner scanner = new Scanner(System.in) ;
		
		IO.println("\n Choissiez 5 cartes parmis les 8 en donnant les indices un par un et en appuyant sur entrée \n");
		int i = 0;
    while (i < 5) {
        IO.print("Entrez l'index de la carte n°" + (i + 1) + " : ");
        int index = scanner.nextInt();
        if (index < 0 || index >= handCards.size()) {
            IO.println("Index invalide ! Choisissez entre 0 et 7.");
        } 
        //Vérifier si l'indice a déjà été choisi
        else if (alreadyChoosen.contains(index)) {
            IO.println("Cette carte a déjà été sélectionnée !");
        } 
        else {
          alreadyChoosen.add(index);
          choosen.add(handCards.get(index));
            i++; 
        }
    }
    
		var choosenImmuable = List.copyOf(choosen);
		
		return choosenImmuable;
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
	    IO.println(state);
	   if(state.isGameWon()) {
       IO.println("Tu as gagné la partie en réussissant à battre les blinds !");

	   }else {
       IO.println("Tu as perdu la partie toutes tes mains ont été consommées");
	   }

		
	}
	

}