package domain;

import java.util.Objects;

public record Card(Rank rank, Color color) {
	public Card {
		Objects.requireNonNull(rank);
		Objects.requireNonNull(color);
	}

	@Override
	public String toString() {
		return rank + " of " + color;
	}
}
