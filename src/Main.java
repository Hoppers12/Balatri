import com.github.forax.zen.Application;
import java.util.List;

import controller.GameController;
import model.Blind;
import model.GameState;
import view.ConsoleView;
import view.GraphicalView;
import view.Palette;

class Main {
	static void main() {
		List<Blind> blinds = List.of(
			new Blind("Petite Blind", 150),
			new Blind("Grosse Blind", 300),
			new Blind("Boss Blind", 800));

		// Choix de la vue au démarrage
		var choice = IO.readln("Choisir une vue - console (c) ou graphique (g) ? ").trim().toLowerCase();

		if (choice.startsWith("c")) {
			// Vue console : pas besoin de Zen, on lance directement
			var view = new ConsoleView();
			var state = new GameState(blinds);
			var controller = new GameController(state, view);
			controller.run();
		} else {
			// Vue graphique (par défaut) : lancement via Zen
			Application.run(Palette.BACKGROUND, context -> {
				var view = new GraphicalView(context);
				var state = new GameState(blinds);
				var controller = new GameController(state, view);
				controller.run();
			});
		}
	}
}
