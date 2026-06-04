package view;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public final class Typography {
	private Typography() {
	}

	private static final Font POKER_BASE = loadPokerFont();

	public static final Font TITLE = POKER_BASE.deriveFont(48f);
	public static final Font BODY = POKER_BASE.deriveFont(20f);
	public static final Font INFO = POKER_BASE.deriveFont(24f);

	public static final Font CARD_RANK = new Font("SansSerif", Font.BOLD, 22);
	public static final Font CARD_SUIT_SMALL = new Font("SansSerif", Font.PLAIN, 20);
	public static final Font CARD_SUIT_LARGE = new Font("SansSerif", Font.PLAIN, 48);

	/**
	 * Loads the custom "Limelight" font bundled in {@code /fonts/Limelight-Regular.ttf}.
	 *
	 * @return the loaded font (in its default size)
	 * @throws AssertionError if the font resource is missing or cannot be read/parsed
	 */
	private static Font loadPokerFont() {
		try (var is = Typography.class.getResourceAsStream("/fonts/Limelight-Regular.ttf")) {
			return Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (IOException | FontFormatException e) {
			throw new AssertionError("Police Limelight introuvable", e);
		}
	}
}