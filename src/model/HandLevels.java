package model;

import domain.HandType;
import domain.Planet;
import java.util.HashMap; 
import java.util.Map;

/**
 * Gère les niveaux et les scores (chips et multiplicateur)
 */
public class HandLevels {

    private static class LevelInfo {
        int level = 1; 
        long chips;
        int multiplier;

        LevelInfo(long chips, int multiplier) {
            this.chips = chips;
            this.multiplier = multiplier;
        }
    }

    // Utilisation d'une HashMap à la place de l'EnumMap
    private final Map<HandType, LevelInfo> scores = new HashMap<>();

    /**
     * Initialise les niveaux de main avec les valeurs de base du projet.
     */
    public HandLevels() {
        scores.put(HandType.HIGH_CARD,       new LevelInfo(5, 1));   
        scores.put(HandType.PAIR,            new LevelInfo(10, 2));  
        scores.put(HandType.TWO_PAIR,        new LevelInfo(20, 2));  
        scores.put(HandType.THREE_OF_KIND,   new LevelInfo(30, 3));   
        scores.put(HandType.STRAIGHT,        new LevelInfo(30, 4));   
        scores.put(HandType.FLUSH,           new LevelInfo(35, 4));   
        scores.put(HandType.FULL_HOUSE,      new LevelInfo(40, 4));   
        scores.put(HandType.FOUR_OF_A_KIND,  new LevelInfo(60, 7));   
        scores.put(HandType.STRAIGHT_FLUSH,  new LevelInfo(100, 8));  
    }

    /**
     * Applique les bonus d'une planète de manière permanente[cite: 105].
     */
    public void upgrade(Planet planet) {
        HandType target = planet.target();
        LevelInfo info = scores.get(target);

        if (info != null) {
            info.level++; 
            info.chips += planet.bonusChips(); 
            info.multiplier += planet.bonusMult();  
        }
    }

    public long getChipsFor(HandType type) {
        return scores.get(type).chips;
    }

    public int getMultiplierFor(HandType type) {
        return scores.get(type).multiplier;
    }

    public int getLevelFor(HandType type) {
        return scores.get(type).level;
    }
}