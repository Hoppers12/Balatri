package model;

import java.util.Objects;

public record Blind(String name, int targetScore) {

	/**
	 * Validates a blind: a non-null name and a strictly positive target score.
	 *
	 * @throws NullPointerException     if {@code name} is null
	 * @throws IllegalArgumentException if {@code targetScore} is not strictly positive
	 */
	public Blind {
		Objects.requireNonNull(name);
		if (targetScore <= 0) {
			throw new IllegalArgumentException("Le blind doit être positif.");
		}
	}

	@Override
	public String toString() {
		return name + " (" + targetScore + ")";
	}
}
