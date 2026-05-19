package view;

import java.util.List;

import domain.Card;
import domain.HandType;
import domain.Planet;
import model.GameState;

public final class GraphicalView implements View {

	@Override
	public void showState(GameState state) {
		
	}

	@Override
	public void showHand(List<Card> handCards) {
		
	}

	@Override
	public List<Card> askSelection(List<Card> handCards) {
		return null;
	}

	@Override
	public void showPlay(HandType type, int score) {
		
	}

	@Override
	public void showPlanetWon(Planet planet) {
		
	}

	@Override
	public void showEnd(GameState state) {
		
	}

}
