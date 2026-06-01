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
import model.Hand;

public final class GraphicalView implements View {

	// Tailles des cartes
	private static final int CARD_WIDTH = 110;
	private static final int CARD_HEIGHT = 160;
	private static final int CARD_SPACING = 18;
	private static final int CARDS_BOTTOM_MARGIN = 80;
	private static final int BTN_WIDTH = 140;
  private static final int BTN_HEIGHT = 45;
  
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

	public GraphicalView(ApplicationContext context) {
		//On affecte la val du paramètre (context) au champ (contexte aussi).
		this.context = Objects.requireNonNull(context);
	}

	// Affichage du titre (à chaque refresh)
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

	private void drawStateInfo(Graphics2D c) {
		if (currentState == null) {
			return;
		}
		// Style du texte
		c.setColor(Palette.TEXT);
		c.setFont(Typography.INFO);

		// Affichage des infos
		c.drawString("Blind : " + currentState.getCurrentBlind(), 50, 160);
      c.drawString("Score : " + currentState.getCurrentBlindScore() + " / " + currentState.getCurrentBlind().targetScore(), 50, 200);
      c.drawString("Mains restantes : " + currentState.getHandsRemaining()
          + " / " + GameState.HANDS_PER_BLIND, 50, 240);
	}

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
			var text = Planet.forHandType(type).displayName() + " - " + type.displayName()
				+ " : " + levels.getChipsFor(type) + " x " + levels.getMultiplierFor(type);
			var textWidth = c.getFontMetrics().stringWidth(text);
			c.drawString(text, screen.width() - rightMargin - textWidth, y);
			y += 30;
		}
	}

	// Petit rappel en bas à droite : "Q pour quitter"
	private void drawQuitHint(Graphics2D c) {
		var screen = context.getScreenInfo();
		c.setColor(Palette.TEXT);
		c.setFont(Typography.BODY);
		var hint = "Q pour quitter";
		var hintWidth = c.getFontMetrics().stringWidth(hint);
		c.drawString(hint, screen.width() - 30 - hintWidth, screen.height() - 30);
	}

	// Les 8 cartes en bas
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

	// Renvoie l'index de la carte sous le pixel (mx, my), ou -1 si aucune carte
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

	// Texte d'instruction
	private void drawSelectionPrompt(Graphics2D c) {
		if (currentHand.isEmpty()) {
			return;
		}
		c.setColor(Palette.TEXT);
		c.setFont(Typography.INFO);
		var screen = context.getScreenInfo();
		var prompt = selectedIndices.size() + "/" + Hand.CARDS_PLAYED + " cartes - ESPACE pour jouer";
		var promptWidth = c.getFontMetrics().stringWidth(prompt);
		var promptX = (screen.width() - promptWidth) / 2;
		var promptY = screen.height() - CARD_HEIGHT - CARDS_BOTTOM_MARGIN - 20;
		c.drawString(prompt, promptX, promptY);
	}

	// Combinaison jouée et score
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

		var bonusText = currentPlanet.target().displayName() + " : +" + currentPlanet.bonusChips()
				+ " chips, +" + currentPlanet.bonusMult() + " mult";
		var bonusWidth = c.getFontMetrics().stringWidth(bonusText);
		c.drawString(bonusText, boxX + (boxW - bonusWidth) / 2, boxY + 180);

		c.setFont(Typography.BODY);
		var hint = "ESPACE pour continuer";
		var hintWidth = c.getFontMetrics().stringWidth(hint);
		c.drawString(hint, boxX + (boxW - hintWidth) / 2, boxY + 240);
	}

	// Fin de partie (victoire ou défaite)
	private void drawEndOverlay(Graphics2D c) {
		if (!gameEnded || currentState == null) {
			return;
		}
		var screen = context.getScreenInfo();

		c.setColor(Palette.OVERLAY);
		c.fillRect(0, 0, screen.width(), screen.height());

		var boxW = 600;
		var boxH = 220;
		var boxX = (screen.width() - boxW) / 2;
		var boxY = (screen.height() - boxH) / 2;
		c.setColor(Palette.CARD);
		c.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);

		c.setFont(Typography.TITLE);
		String title;
		if (currentState.isGameWon()) {
			c.setColor(Palette.RED_SUIT);
			title = "VICTOIRE !";
		} else {
			c.setColor(Palette.BLACK_SUIT);
			title = "DEFAITE";
		}
		var titleWidth = c.getFontMetrics().stringWidth(title);
		c.drawString(title, boxX + (boxW - titleWidth) / 2, boxY + 90);

		c.setColor(Palette.BLACK_SUIT);
		c.setFont(Typography.INFO);
		var recap = "Score final : " + currentState.getCurrentBlindScore();
		var recapWidth = c.getFontMetrics().stringWidth(recap);
		c.drawString(recap, boxX + (boxW - recapWidth) / 2, boxY + 150);

		c.setFont(Typography.BODY);
		var hint = "Q pour quitter";
		var hintWidth = c.getFontMetrics().stringWidth(hint);
		c.drawString(hint, boxX + (boxW - hintWidth) / 2, boxY + 195);
	}

	// Ferme le jeu avec la touche Q
	// Appeler dans toutes les boucles d'events pour quitter à tout moment
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

	private void waitForSpace() {
		while (true) {
			var event = context.pollOrWaitEvent(Long.MAX_VALUE);
			if (event == null) {
				continue;
			}
			checkQuit(event);
			var spacePressed = switch (event) {
				case PointerEvent pe -> false;
				case KeyboardEvent ke -> ke.action() == KeyboardEvent.Action.KEY_PRESSED
						&& ke.key() == KeyboardEvent.Key.SPACE;
			};
			if (spacePressed) {
				return;
			}
		}
	}

	// Redessine tout l'écran à partir des champs currentState, currentHand
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

	@Override
	public void showState(GameState state) {
		Objects.requireNonNull(state);
		this.currentState = state;
		redraw();
	}

	@Override
	public void showHand(List<Card> handCards) {
		Objects.requireNonNull(handCards);
		this.currentHand = List.copyOf(handCards);
		// Nouvelle main => sélection à zéro
		this.selectedIndices = Set.of();
		redraw();
	}

	@Override
  public SelectionResult askSelection(List<Card> handCards) {
    Objects.requireNonNull(handCards);
    this.currentHand = List.copyOf(handCards);
    
    var selected = new HashSet<Integer>();
    this.selectedIndices = selected;
    redraw();

    // On mémorise si le joueur a cliqué sur défausser
    boolean isDiscard = false;
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

            //  Clic sur une carte
            var idx = findCardIndex(px, py);
            if (idx >= 0) {
              if (selected.contains(idx)) {
                selected.remove(idx);
              } else if (selected.size() < Hand.CARDS_PLAYED) {
                selected.add(idx);
              }
              redraw();
            }
            
            // Click sur le bouton défausser
            var screen = context.getScreenInfo();
            int btnX = screen.width() - BTN_WIDTH - 30;
            int btnY = screen.height() - BTN_HEIGHT - CARDS_BOTTOM_MARGIN - 20;
            
            boolean canDiscard = currentState != null && currentState.getDiscardsRemaining() > 0;
           
            if (canDiscard && px >= btnX && px <= btnX + BTN_WIDTH && py >= btnY && py <= btnY + BTN_HEIGHT) {
              if (!selected.isEmpty() && selected.size() <= Hand.CARDS_PLAYED) {
                isDiscard = true;
                yield true; 
              }
            }
          }
          yield false;
        }
        //  Touche ESPACE (Jouer la main)
        case KeyboardEvent ke -> ke.action() == KeyboardEvent.Action.KEY_PRESSED 
            && ke.key() == KeyboardEvent.Key.SPACE 
            && selected.size() == Hand.CARDS_PLAYED;
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

	@Override
	public void showPlay(HandType type, int score) {
		Objects.requireNonNull(type);
		this.currentPlayType = type;
		this.currentPlayScore = score;
		redraw();
		waitForSpace();
		this.currentPlayType = null;
	}

	@Override
	public void showPlanetWon(Planet planet) {
		Objects.requireNonNull(planet);
		this.currentPlanet = planet;
		redraw();
		waitForSpace();
		this.currentPlanet = null;
	}

	@Override
	public void showEnd(GameState state) {
		Objects.requireNonNull(state);
		this.currentState = state;
		this.gameEnded = true;
		redraw();
		// On reste dans une boucle d'events pour que Q continue à fonctionner.
		while (true) {
			var event = context.pollOrWaitEvent(Long.MAX_VALUE);
			if (event != null) {
				checkQuit(event);
			}
		}
	}

}
