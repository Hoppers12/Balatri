package model;

import java.util.Objects;

public record Blind(String name, int targetScore) {
	public Blind {
		Objects.requireNonNull(name);
		if (targetScore <= 0) {
			throw new IllegalArgumentException("Le blind doit être strictement positif.");
		}
	}

	@Override
	public String toString() {
		return name + " (" + targetScore + ")";
	}
}
