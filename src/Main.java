import java.util.List;

import domain.Card;
import domain.Color;
import domain.HandDetector;
import domain.Rank;
import model.Deck;

class Main {

    static void main() {
        IO.println("---------- Tests de HandDetector ----------");

        var highCard = List.of(
                new Card(Rank.ACE, Color.SPADES),
                new Card(Rank.KING, Color.HEARTS),
                new Card(Rank.JACK, Color.DIAMONDS),
                new Card(Rank.NINE, Color.CLUBS),
                new Card(Rank.SEVEN, Color.HEARTS));
        IO.println("Carte haute (attendu HIGH_CARD) : " + HandDetector.detect(highCard));

        var pair = List.of(
                new Card(Rank.SEVEN, Color.SPADES),
                new Card(Rank.SEVEN, Color.HEARTS),
                new Card(Rank.JACK, Color.DIAMONDS),
                new Card(Rank.NINE, Color.CLUBS),
                new Card(Rank.TWO, Color.HEARTS));
        IO.println("Paire (attendu PAIR) : " + HandDetector.detect(pair));

        var twoPair = List.of(
                new Card(Rank.SEVEN, Color.SPADES),
                new Card(Rank.SEVEN, Color.HEARTS),
                new Card(Rank.KING, Color.DIAMONDS),
                new Card(Rank.KING, Color.CLUBS),
                new Card(Rank.TWO, Color.HEARTS));
        IO.println("Double paire (attendu TWO_PAIR) : " + HandDetector.detect(twoPair));

        var threeOfKind = List.of(
                new Card(Rank.SEVEN, Color.SPADES),
                new Card(Rank.SEVEN, Color.HEARTS),
                new Card(Rank.SEVEN, Color.DIAMONDS),
                new Card(Rank.KING, Color.CLUBS),
                new Card(Rank.TWO, Color.HEARTS));
        IO.println("Brelan (attendu THREE_OF_KIND) : " + HandDetector.detect(threeOfKind));

        var straight = List.of(
                new Card(Rank.FIVE, Color.SPADES),
                new Card(Rank.SIX, Color.HEARTS),
                new Card(Rank.SEVEN, Color.DIAMONDS),
                new Card(Rank.EIGHT, Color.CLUBS),
                new Card(Rank.NINE, Color.HEARTS));
        IO.println("Suite (attendu STRAIGHT) : " + HandDetector.detect(straight));

        var lowStraight = List.of(
                new Card(Rank.ACE, Color.SPADES),
                new Card(Rank.TWO, Color.HEARTS),
                new Card(Rank.THREE, Color.DIAMONDS),
                new Card(Rank.FOUR, Color.CLUBS),
                new Card(Rank.FIVE, Color.HEARTS));
        IO.println("Suite basse A-2-3-4-5 (attendu STRAIGHT) : " + HandDetector.detect(lowStraight));

        var highStraight = List.of(
                new Card(Rank.TEN, Color.SPADES),
                new Card(Rank.JACK, Color.HEARTS),
                new Card(Rank.QUEEN, Color.DIAMONDS),
                new Card(Rank.KING, Color.CLUBS),
                new Card(Rank.ACE, Color.HEARTS));
        IO.println("Suite haute 10-J-Q-K-A (attendu STRAIGHT) : " + HandDetector.detect(highStraight));

        var flush = List.of(
                new Card(Rank.TWO, Color.SPADES),
                new Card(Rank.SIX, Color.SPADES),
                new Card(Rank.NINE, Color.SPADES),
                new Card(Rank.JACK, Color.SPADES),
                new Card(Rank.KING, Color.SPADES));
        IO.println("Couleur (attendu FLUSH) : " + HandDetector.detect(flush));

        var fullHouse = List.of(
                new Card(Rank.SEVEN, Color.SPADES),
                new Card(Rank.SEVEN, Color.HEARTS),
                new Card(Rank.SEVEN, Color.DIAMONDS),
                new Card(Rank.KING, Color.CLUBS),
                new Card(Rank.KING, Color.HEARTS));
        IO.println("Full (attendu FULL_HOUSE) : " + HandDetector.detect(fullHouse));

        var fourOfKind = List.of(
                new Card(Rank.SEVEN, Color.SPADES),
                new Card(Rank.SEVEN, Color.HEARTS),
                new Card(Rank.SEVEN, Color.DIAMONDS),
                new Card(Rank.SEVEN, Color.CLUBS),
                new Card(Rank.KING, Color.HEARTS));
        IO.println("Carre (attendu FOUR_OF_A_KIND) : " + HandDetector.detect(fourOfKind));

        var straightFlush = List.of(
                new Card(Rank.FIVE, Color.SPADES),
                new Card(Rank.SIX, Color.SPADES),
                new Card(Rank.SEVEN, Color.SPADES),
                new Card(Rank.EIGHT, Color.SPADES),
                new Card(Rank.NINE, Color.SPADES));
        IO.println("Quinte flush (attendu STRAIGHT_FLUSH) : " + HandDetector.detect(straightFlush));

        var lowStraightFlush = List.of(
                new Card(Rank.ACE, Color.SPADES),
                new Card(Rank.TWO, Color.SPADES),
                new Card(Rank.THREE, Color.SPADES),
                new Card(Rank.FOUR, Color.SPADES),
                new Card(Rank.FIVE, Color.SPADES));
        IO.println("Quinte flush basse A-2-3-4-5 (attendu STRAIGHT_FLUSH) : " + HandDetector.detect(lowStraightFlush));
        
        IO.println("----------------------------------------");
        IO.println("---------- Tests de Deck ----------");

        var deck = new Deck();
        IO.println("Cartes restantes au départ : " + deck.restDrawPile()); // attendu 52

        var hand = deck.draw(8);
        IO.println("Pioche de 8 cartes : " + hand);
        IO.println("Cartes restantes : " + deck.restDrawPile()); // attendu 44

        deck.discard(hand);
        IO.println("Apres defausse, cartes restantes en pioche : " + deck.restDrawPile()); // toujours 44

        // Test du recyclage
        deck.draw(40); // il en reste 4 dans la pioche
        IO.println("Cartes restantes apres draw(40) : " + deck.restDrawPile()); // attendu 4
        deck.discard(hand); // on remet 8 dans la défausse
        var big = deck.draw(8); // doit déclencher le recyclage car 4 < 8
        IO.println("Pioche de 8 apres recyclage : " + big.size()); // attendu 8
    }
}