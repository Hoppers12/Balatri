import com.github.forax.zen.Application;

import controller.GameController;
import model.GameState;
import model.HighScore;
import view.ConsoleView;
import view.GraphicalView;
import view.Palette;

class Main {
	static void main() {
		var highScore = new HighScore();

		// Choix de la vue au démarrage
		var choice = IO.readln("Choisir une vue - console (c) ou graphique (g) ? ").trim().toLowerCase();

		if (choice.startsWith("c")) {
			// Vue console
			boolean again = true;
			while (again) {
				var view = new ConsoleView();
				var state = new GameState();
				new GameController(state, view, highScore).run();
				again = view.askReplay();
			}
		} else {
			// Vue graphique
			Application.run(Palette.BACKGROUND, context -> {
				boolean again = true;
				while (again) {
					var view = new GraphicalView(context);
					var state = new GameState();
					new GameController(state, view, highScore).run();
					again = view.askReplay();
				}
			});
		}
	}
}
