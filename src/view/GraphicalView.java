package view;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.Event;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import domain.Card;
import domain.Color;
import domain.HandType;
import domain.Planet;
import domain.SelectionResult;
import model.GameState;
import model.HighScore;

public final class GraphicalView implements View {

	// Tailles des cartes
	private static final int CARD_WIDTH = 110;
	private static final int CARD_HEIGHT = 160;
	private static final int CARD_SPACING = 18;
	private static final int CARDS_BOTTOM_MARGIN = 80;
	private static final int BTN_WIDTH = 140;
	private static final int BTN_HEIGHT = 45;

	private HighScore currentHighScore;
	private boolean newRecord;

	private final ApplicationContext context;
	private GameState currentState;
	private List<Card> currentHand = List.of();
	// Indices des cartes sélectionnées dans currentHand
	private Set<Integer> selectedIndices = Set.of();
	// Combinaison et score de la dernière main
	private HandType currentPlayType;
	private int currentPlayScore;
	// Planète gagnée
	private Planet currentPlanet;
	// Vrai quand la partie est finie (affiche ecran de fin)
	private boolean gameEnded;

	/**
	 * Builds the graphical view bound to the given Zen application context.
	 *
	 * @param context the Zen application context used for rendering and events
	 * @throws NullPointerException if {@code context} is null
	 */
	public GraphicalView(ApplicationContext context) {
		// On affecte la val du paramètre (context) au champ (contexte aussi).
		this.context = Objects.requireNonNull(context);
	}

	/**
	 * Draws the centered game title.
	 *
	 * @param c the graphics context to draw on
	 */
	private void drawHeader(Graphics2D c) {
		var screen = context.getScreenInfo();
		c.setColor(Palette.TEXT);
		c.setFont(Typography.TITLE);

		var title = "BALATRI GAME";
		// Taille de la font
		var titleWidth = c.getFontMetrics().stringWidth(title);
		// Pour centrer
		var titleX = (screen.width() - titleWidth) / 2;

		c.drawString(title, titleX, 60);
	}

	/**
	 * Draws the "Défausser" button in the bottom-right corner, unless there are no cards
	 * or no discards remaining.
	 *
	 * @param c the graphics context to draw on
	 */
	private void drawDiscardButton(Graphics2D c) {
		if (currentHand.isEmpty()) {
			return; // Pas de cartes = pas de bouton
		}
		if (currentState != null && currentState.getDiscardsRemaining() <= 0) {
			return;
		}

		var screen = context.getScreenInfo();
		// On le place en bas à droite
		int btnX = screen.width() - BTN_WIDTH - 30;
		int btnY = screen.height() - BTN_HEIGHT - CARDS_BOTTOM_MARGIN - 20;

		// Dessiner le fond du bouton
		c.setColor(Palette.RED_SUIT); // Utilise du rouge pour bien le voir
		c.fillRoundRect(btnX, btnY, BTN_WIDTH, BTN_HEIGHT, 15, 15);

		// Dessiner le texte
		c.setColor(Palette.TEXT); // Texte clair
		c.setFont(Typography.BODY);
		var text = "Défausser";
		var textWidth = c.getFontMetrics().stringWidth(text);
		// Centrer le texte dans le bouton
		c.drawString(text, btnX + (BTN_WIDTH - textWidth) / 2, btnY + 28);
	}

	/**
	 * Draws the state panel: blind number, score/target, remaining hands, total score
	 * and blinds beaten.
	 *
	 * @param c the graphics context to draw on
	 */
	private void drawStateInfo(Graphics2D c) {
		if (currentState == null) {
			return;
		}
		c.setColor(Palette.TEXT);
		c.setFont(Typography.INFO);

		c.drawString("Blind n°" + currentState.getBlindNumber(), 50, 160);
		c.drawString("Score : " + currentState.getCurrentBlindScore() + " / " + currentState.getCurrentBlind().targetScore(), 50, 200);
		c.drawString("Mains restantes : " + currentState.getHandsRemaining() + " / " + GameState.HANDS_PER_BLIND, 50, 240);
		c.drawString("Score total : " + currentState.getTotalScore(), 50, 280);
		c.drawString("Blinds battus : " + currentState.getBlindsBeaten(), 50, 320);
	}

	/**
	 * Draws, on the right side, the combinations that have been upgraded by planets.
	 *
	 * @param c the graphics context to draw on
	 */
	private void drawActiveBonuses(Graphics2D c) {
		if (currentState == null) {
			return;
		}
		var levels = currentState.getHandLevels();

		var upgraded = new ArrayList<HandType>();
		for (var type : HandType.values()) {
			if (levels.getLevelFor(type) > 1) {
				upgraded.add(type);
			}
		}
		if (upgraded.isEmpty()) {
			return;
		}

		var screen = context.getScreenInfo();
		var rightMargin = 50;

		c.setColor(Palette.TEXT);
		c.setFont(Typography.INFO);
		var header = "Bonus actifs";
		var headerWidth = c.getFontMetrics().stringWidth(header);
		c.drawString(header, screen.width() - rightMargin - headerWidth, 160);

		c.setFont(Typography.BODY);
		var y = 200;
		for (var type : upgraded) {
			var text = Planet.forHandType(type).displayName() + " - " + type.displayName() + " : " + levels.getChipsFor(type) + " x " + levels.getMultiplierFor(type);
			var textWidth = c.getFontMetrics().stringWidth(text);
			c.drawString(text, screen.width() - rightMargin - textWidth, y);
			y += 30;
		}
	}

	/**
	 * Draws the "Q to quit" hint in the bottom-right corner.
	 *
	 * @param c the graphics context to draw on
	 */
	private void drawQuitHint(Graphics2D c) {
		var screen = context.getScreenInfo();
		c.setColor(Palette.TEXT);
		c.setFont(Typography.BODY);
		var hint = "Q pour quitter";
		var hintWidth = c.getFontMetrics().stringWidth(hint);
		c.drawString(hint, screen.width() - 30 - hintWidth, screen.height() - 30);
	}

	/**
	 * Draws the player's cards in a centered row at the bottom of the screen.
	 *
	 * @param c the graphics context to draw on
	 */
	private void drawHand(Graphics2D c) {
		if (currentHand.isEmpty()) {
			return;
		}
		var screen = context.getScreenInfo();
		var n = currentHand.size();
		// Largeur totale = n cartes + (n-1) espaces entre cartes
		var totalWidth = n * CARD_WIDTH + (n - 1) * CARD_SPACING;
		var startX = (screen.width() - totalWidth) / 2;
		var y = screen.height() - CARD_HEIGHT - CARDS_BOTTOM_MARGIN;

		for (var i = 0; i < n; i++) {
			var x = startX + i * (CARD_WIDTH + CARD_SPACING);
			drawCard(c, currentHand.get(i), x, y, selectedIndices.contains(i));
		}
	}

	/**
	 * Draws a single card at the given position, highlighted when selected.
	 *
	 * @param c        the graphics context to draw on
	 * @param card     the card to draw
	 * @param x        the left coordinate of the card
	 * @param y        the top coordinate of the card
	 * @param selected {@code true} to draw the card as selected
	 */
	private static void drawCard(Graphics2D c, Card card, int x, int y, boolean selected) {
		if (selected) {
			c.setColor(Palette.CARD_SELECTED);
		} else {
			c.setColor(Palette.CARD);
		}
		c.fillRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, 20, 20);

		if (card.color() == Color.HEARTS || card.color() == Color.DIAMONDS) {
			c.setColor(Palette.RED_SUIT);
		} else {
			c.setColor(Palette.BLACK_SUIT);
		}

		// En haut à gauche : rang et couleur
		c.setFont(Typography.CARD_RANK);
		c.drawString(card.rank().symbol(), x + 8, y + 26);
		c.setFont(Typography.CARD_SUIT_SMALL);
		c.drawString(card.color().symbol(), x + 8, y + 48);

		// En bas à droite : rang et couleur
		c.setFont(Typography.CARD_SUIT_SMALL);
		var suitWidth = c.getFontMetrics().stringWidth(card.color().symbol());
		c.drawString(card.color().symbol(), x + CARD_WIDTH - 8 - suitWidth, y + CARD_HEIGHT - 30);
		c.setFont(Typography.CARD_RANK);
		var rankWidth = c.getFontMetrics().stringWidth(card.rank().symbol());
		c.drawString(card.rank().symbol(), x + CARD_WIDTH - 8 - rankWidth, y + CARD_HEIGHT - 8);

		// Gros symbole au milieu
		c.setFont(Typography.CARD_SUIT_LARGE);
		var fm = c.getFontMetrics();
		var symbol = card.color().symbol();
		var sw = fm.stringWidth(symbol);
		c.drawString(symbol, x + (CARD_WIDTH - sw) / 2, y + CARD_HEIGHT / 2 + 22);
	}

	/**
	 * Returns the index of the card located under the given pixel, or -1 if none.
	 *
	 * @param mx the x coordinate of the pixel
	 * @param my the y coordinate of the pixel
	 * @return the card index, or -1 if no card is at that position
	 */
	private int findCardIndex(int mx, int my) {
		if (currentHand.isEmpty()) {
			return -1;
		}
		var screen = context.getScreenInfo();
		var n = currentHand.size();
		var totalWidth = n * CARD_WIDTH + (n - 1) * CARD_SPACING;
		var startX = (screen.width() - totalWidth) / 2;
		var y = screen.height() - CARD_HEIGHT - CARDS_BOTTOM_MARGIN;

		if (my < y || my > y + CARD_HEIGHT) {
			return -1;
		}
		for (var i = 0; i < n; i++) {
			var cardX = startX + i * (CARD_WIDTH + CARD_SPACING);
			if (mx >= cardX && mx <= cardX + CARD_WIDTH) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Draws the selection prompt (number of selected cards and how to play).
	 *
	 * @param c the graphics context to draw on
	 */
	private void drawSelectionPrompt(Graphics2D c) {
		if (currentHand.isEmpty()) {
			return;
		}
		c.setColor(Palette.TEXT);
		c.setFont(Typography.INFO);
		var screen = context.getScreenInfo();
		var prompt = selectedIndices.size() + "/" + GameState.CARDS_PLAYED + " cartes - ESPACE pour jouer";
		var promptWidth = c.getFontMetrics().stringWidth(prompt);
		var promptX = (screen.width() - promptWidth) / 2;
		var promptY = screen.height() - CARD_HEIGHT - CARDS_BOTTOM_MARGIN - 20;
		c.drawString(prompt, promptX, promptY);
	}

	/**
	 * Draws the overlay showing the last played combination and its score.
	 *
	 * @param c the graphics context to draw on
	 */
	private void drawPlayOverlay(Graphics2D c) {
		if (currentPlayType == null) {
			return;
		}
		var screen = context.getScreenInfo();

		c.setColor(Palette.OVERLAY);
		c.fillRect(0, 0, screen.width(), screen.height());

		var boxW = 600;
		var boxH = 240;
		var boxX = (screen.width() - boxW) / 2;
		var boxY = (screen.height() - boxH) / 2;
		c.setColor(Palette.CARD);
		c.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);

		c.setColor(Palette.BLACK_SUIT);
		c.setFont(Typography.TITLE);
		var typeText = currentPlayType.displayName();
		var typeWidth = c.getFontMetrics().stringWidth(typeText);
		c.drawString(typeText, boxX + (boxW - typeWidth) / 2, boxY + 80);

		c.setFont(Typography.INFO);
		var scoreText = "+ " + currentPlayScore + " points";
		var scoreWidth = c.getFontMetrics().stringWidth(scoreText);
		c.drawString(scoreText, boxX + (boxW - scoreWidth) / 2, boxY + 140);

		c.setFont(Typography.BODY);
		var hint = "ESPACE pour continuer";
		var hintWidth = c.getFontMetrics().stringWidth(hint);
		c.drawString(hint, boxX + (boxW - hintWidth) / 2, boxY + 210);
	}

	/**
	 * Draws the overlay announcing a beaten blind and the planet won.
	 *
	 * @param c the graphics context to draw on
	 */
	private void drawPlanetOverlay(Graphics2D c) {
		if (currentPlanet == null) {
			return;
		}
		var screen = context.getScreenInfo();

		c.setColor(Palette.OVERLAY);
		c.fillRect(0, 0, screen.width(), screen.height());

		var boxW = 640;
		var boxH = 280;
		var boxX = (screen.width() - boxW) / 2;
		var boxY = (screen.height() - boxH) / 2;
		c.setColor(Palette.CARD);
		c.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);

		c.setColor(Palette.BLACK_SUIT);
		c.setFont(Typography.TITLE);
		var title = "BLIND BATTU !";
		var titleWidth = c.getFontMetrics().stringWidth(title);
		c.drawString(title, boxX + (boxW - titleWidth) / 2, boxY + 70);

		c.setFont(Typography.INFO);
		var planetText = "Planète : " + currentPlanet.displayName();
		var planetWidth = c.getFontMetrics().stringWidth(planetText);
		c.drawString(planetText, boxX + (boxW - planetWidth) / 2, boxY + 130);

		var bonusText = currentPlanet.target().displayName() + " : +" + currentPlanet.bonusChips() + " chips, +"
				+ currentPlanet.bonusMult() + " mult";
		var bonusWidth = c.getFontMetrics().stringWidth(bonusText);
		c.drawString(bonusText, boxX + (boxW - bonusWidth) / 2, boxY + 180);

		c.setFont(Typography.BODY);
		var hint = "ESPACE pour continuer";
		var hintWidth = c.getFontMetrics().stringWidth(hint);
		c.drawString(hint, boxX + (boxW - hintWidth) / 2, boxY + 240);
	}

	/**
	 * Draws the end-of-game overlay: total score, blinds beaten, best score and a record
	 * notice, with the replay/quit hint.
	 *
	 * @param c the graphics context to draw on
	 */
	private void drawEndOverlay(Graphics2D c) {
		if (!gameEnded || currentState == null) {
			return;
		}
		var screen = context.getScreenInfo();

		c.setColor(Palette.OVERLAY);
		c.fillRect(0, 0, screen.width(), screen.height());

		var boxW = 600;
		var boxH = 320;
		var boxX = (screen.width() - boxW) / 2;
		var boxY = (screen.height() - boxH) / 2;
		c.setColor(Palette.CARD);
		c.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);

		c.setColor(Palette.BLACK_SUIT);
		c.setFont(Typography.TITLE);
		var title = "PARTIE TERMINEE";
		var titleWidth = c.getFontMetrics().stringWidth(title);
		c.drawString(title, boxX + (boxW - titleWidth) / 2, boxY + 70);

		c.setFont(Typography.INFO);
		var total = "Score total : " + currentState.getTotalScore();
		var totalWidth = c.getFontMetrics().stringWidth(total);
		c.drawString(total, boxX + (boxW - totalWidth) / 2, boxY + 130);

		var blinds = "Blinds battus : " + currentState.getBlindsBeaten();
		var blindsWidth = c.getFontMetrics().stringWidth(blinds);
		c.drawString(blinds, boxX + (boxW - blindsWidth) / 2, boxY + 170);

		if (currentHighScore != null) {
			var best = "Meilleur : " + currentHighScore.getBestScore() + " pts (" + currentHighScore.getBestBlinds()
					+ " blinds)";
			var bestWidth = c.getFontMetrics().stringWidth(best);
			c.drawString(best, boxX + (boxW - bestWidth) / 2, boxY + 210);
		}
		if (newRecord) {
			c.setColor(Palette.RED_SUIT);
			var rec = "Nouveau record !";
			var recWidth = c.getFontMetrics().stringWidth(rec);
			c.drawString(rec, boxX + (boxW - recWidth) / 2, boxY + 245);
			c.setColor(Palette.BLACK_SUIT);
		}

		c.setFont(Typography.BODY);
		var hint = "ESPACE pour rejouer - Q pour quitter";
		var hintWidth = c.getFontMetrics().stringWidth(hint);
		c.drawString(hint, boxX + (boxW - hintWidth) / 2, boxY + 295);
	}

	/**
	 * Quits the application when the Q key is pressed; meant to be called from every event loop.
	 *
	 * @param event the event to inspect
	 */
	private void checkQuit(Event event) {
		var shouldQuit = switch (event) {
		case PointerEvent pe -> false;
		case KeyboardEvent ke -> ke.action() == KeyboardEvent.Action.KEY_PRESSED && ke.key() == KeyboardEvent.Key.Q;
		};
		if (shouldQuit) {
			context.dispose();
			System.exit(0);
		}
	}

	/**
	 * Blocks until the player presses the SPACE key (Q still quits).
	 */
	private void waitForSpace() {
		while (true) {
			var event = context.pollOrWaitEvent(Long.MAX_VALUE);
			if (event == null) {
				continue;
			}
			checkQuit(event);
			var spacePressed = switch (event) {
			case PointerEvent pe -> false;
			case KeyboardEvent ke -> ke.action() == KeyboardEvent.Action.KEY_PRESSED && ke.key() == KeyboardEvent.Key.SPACE;
			};
			if (spacePressed) {
				return;
			}
		}
	}

	/**
	 * Redraws the whole screen (background, panels, hand, buttons and overlays).
	 */
	private void redraw() {
		context.renderFrame(c -> {
			// Effacer l'écran
			var screen = context.getScreenInfo();

			c.setColor(Palette.BACKGROUND);
			c.fillRect(0, 0, screen.width(), screen.height());

			drawHeader(c);
			drawStateInfo(c);
			drawActiveBonuses(c);
			drawSelectionPrompt(c);
			drawHand(c);
			drawDiscardButton(c);
			drawQuitHint(c);
			// Les overlays passent par-dessus le reste
			drawPlayOverlay(c);
			drawPlanetOverlay(c);
			drawEndOverlay(c);
		});
	}

	/**
	 * {@inheritDoc}
	 * Stores the state and redraws the screen.
	 */
	@Override
	public void showState(GameState state) {
		Objects.requireNonNull(state);
		this.currentState = state;
		redraw();
	}

	/**
	 * {@inheritDoc}
	 * Stores the hand, clears the selection and redraws.
	 */
	@Override
	public void showHand(List<Card> handCards) {
		Objects.requireNonNull(handCards);
		this.currentHand = List.copyOf(handCards);
		// Nouvelle main => sélection à zéro
		this.selectedIndices = Set.of();
		redraw();
	}

	/**
	 * {@inheritDoc}
	 * Lets the player click cards to (de)select them, click "Défausser" to discard,
	 * or press SPACE to play once five cards are selected.
	 */
	@Override
	public SelectionResult askSelection(List<Card> handCards) {
		Objects.requireNonNull(handCards);
		this.currentHand = List.copyOf(handCards);

		var selected = new HashSet<Integer>();
		this.selectedIndices = selected;
		redraw();

		// On mémorise si le joueur a cliqué sur défausser
		var isDiscard = false;
		while (true) {
			var event = context.pollOrWaitEvent(Long.MAX_VALUE);
			if (event == null) {
				continue;
			}
			checkQuit(event);

			var shouldExit = switch (event) {
			case PointerEvent pe -> {
				if (pe.action() == PointerEvent.Action.POINTER_DOWN) {
					var px = pe.location().x();
					var py = pe.location().y();

					// Clic sur une carte
					var idx = findCardIndex(px, py);
					if (idx >= 0) {
						if (selected.contains(idx)) {
							selected.remove(idx);
						} else if (selected.size() < GameState.CARDS_PLAYED) {
							selected.add(idx);
						}
						redraw();
					}

					// Click sur le bouton défausser
					var screen = context.getScreenInfo();
					var btnX = screen.width() - BTN_WIDTH - 30;
					var btnY = screen.height() - BTN_HEIGHT - CARDS_BOTTOM_MARGIN - 20;

					boolean canDiscard = currentState != null && currentState.getDiscardsRemaining() > 0;

					if (canDiscard && px >= btnX && px <= btnX + BTN_WIDTH && py >= btnY && py <= btnY + BTN_HEIGHT) {
						if (!selected.isEmpty() && selected.size() <= GameState.CARDS_PLAYED) {
							isDiscard = true;
							yield true;
						}
					}
				}
				yield false;
			}
			// Touche ESPACE (Jouer la main)
			case KeyboardEvent ke -> ke.action() == KeyboardEvent.Action.KEY_PRESSED && ke.key() == KeyboardEvent.Key.SPACE
					&& selected.size() == GameState.CARDS_PLAYED;
			};
			if (shouldExit) {
				break;
			}
		}

		// Liste résultat triée par ordre d'apparition dans la main
		var result = new ArrayList<Card>();
		for (var idx : new TreeSet<>(selected)) {
			result.add(currentHand.get(idx));
		}
		return new SelectionResult(List.copyOf(result), isDiscard);
	}

	/**
	 * {@inheritDoc}
	 * Shows the play overlay and waits for SPACE before continuing.
	 */
	@Override
	public void showPlay(HandType type, int score) {
		Objects.requireNonNull(type);
		this.currentPlayType = type;
		this.currentPlayScore = score;
		redraw();
		waitForSpace();
		this.currentPlayType = null;
	}

	/**
	 * {@inheritDoc}
	 * Shows the planet overlay and waits for SPACE before continuing.
	 */
	@Override
	public void showPlanetWon(Planet planet) {
		Objects.requireNonNull(planet);
		this.currentPlanet = planet;
		redraw();
		waitForSpace();
		this.currentPlanet = null;
	}

	/**
	 * {@inheritDoc}
	 * Stores the final result and draws the end overlay (waiting is handled by {@link #askReplay()}).
	 */
	@Override
	public void showEnd(GameState state, HighScore highScore, boolean newRecord) {
		Objects.requireNonNull(state);
		this.currentState = state;
		this.currentHighScore = highScore;
		this.newRecord = newRecord;
		this.gameEnded = true;
		redraw();
		// On n'attend pas car askReplay() gère rejouer / quitter.
	}

	/**
	 * {@inheritDoc}
	 * Returns {@code true} when SPACE is pressed; Q quits the application.
	 */
	@Override
	public boolean askReplay() {
		while (true) {
			var event = context.pollOrWaitEvent(Long.MAX_VALUE);
			if (event == null) {
				continue;
			}
			checkQuit(event); // Q ferme le jeu
			var replay = switch (event) {
			case PointerEvent pe -> false;
			case KeyboardEvent ke -> ke.action() == KeyboardEvent.Action.KEY_PRESSED && ke.key() == KeyboardEvent.Key.SPACE;
			};
			if (replay) {
				return true;
			}
		}
	}

}
