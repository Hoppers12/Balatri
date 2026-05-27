# Balatri

Version simplifiée du jeu **Balatro** réalisée en Java dans le cadre du cours de Programmation Orientée Objet (E3 info, ESIEE Paris, 2026).

**Auteurs** : Camélia Antoine et Robin Ciurlik

## Importer le projet dans Eclipse

Le projet est un projet Eclipse classique. Pour l'importer :

1. Dézipper l'archive dans un dossier local.
2. Dans Eclipse : `File` → `Import...` → `General` → `Existing Projects into Workspace` → `Next`.
3. Cliquer sur `Browse...` puis sélectionner le dossier `Balatri/`.
4. Valider avec `Finish`.
5. Le projet apparaît dans le `Package Explorer`.

### Configuration requise

- **Java 25** (version du code du projet).
- **Bibliothèque Zen6** pour la vue graphique : déjà incluse dans `lib/` et ajoutée automatiquement au build path via `.classpath`. Aucune installation n'est requise.

## Lancer le programme

La classe principale est `Main.java`, située à la racine du package (`src/Main.java`).

Dans Eclipse :

1. Ouvrir `src/Main.java`.
2. Clic sur l'icone start (verte) `Run Main`.
3. Au démarrage, choisir la vue dans la console Eclipse :
   - taper `c` puis `Entrée` pour la **vue console** ;
   - taper `g` (ou n'importe quoi d'autre) puis `Entrée` pour la **vue graphique**.

Le joueur affronte 3 blinds à la suite avec 4 mains par blind. À chaque tour, 8 cartes sont piochées et le joueur en sélectionne 5 pour former une combinaison de poker.

**Vue console** : on tape un par un les indices des 5 cartes à jouer.

**Vue graphique** (Zen6) : on clique sur les cartes pour les sélectionner (elles passent en doré), puis on appuie sur `ESPACE` pour valider la main. Le résultat (combinaison + score) s'affiche dans un overlay ; appuyer sur `ESPACE` pour passer au tour suivant. Idem pour l'annonce d'une planète gagnée après un blind battu.

## État actuel de l'implémentation

### Fonctionnalités complètes

- **Cartes et combinaisons** : enums `Rank` (TWO, ..., ACE) et `Color` (CLUBS, DIAMONDS, HEARTS, SPADES), record `Card`.
- **Détection des 9 combinaisons de Balatri** avec `HandDetector`, avec gestion du cas particulier A-2-3-4-5.
- **Pioche et défausse** : classe `Deck` avec mélange (Fisher–Yates shuffle Algorithm : https://www.geeksforgeeks.org/dsa/shuffle-a-given-array-using-fisher-yates-shuffle-algorithm/), recyclage automatique de la défausse quand la pioche est insuffisante.
- **État du jeu** : `GameState` gère le blind courant, le score cumulé, les mains restantes, le statut de la partie (gagné / perdu).
- **Blinds** : record `Blind` (nom + score cible).
- **Calcul du score** : `chips × multiplicateur`, dans `ScoreController`.
- **Planètes** : enum `Planet` (9 planètes, une par combinaison). Une planète aléatoire est attribuée à chaque blind battu et augmente de manière permanente les chips et le multiplicateur de la combinaison ciblée.
- **Niveaux courants** : `HandLevels` stocke les chips et multiplicateurs actuels par combinaison.
- **Règles centralisées** : constantes `Hand.CARDS_DRAWN` (8), `Hand.CARDS_PLAYED` (5), `GameState.HANDS_PER_BLIND` (4) référencées par la vue et le contrôleur (pas de magic numbers).
- **Vue console** (`ConsoleView`) : affichage de l'état du jeu, sélection des cartes par saisie d'indices, affichage du résultat de chaque main et des planètes obtenues.
- **Vue graphique** (`GraphicalView`) avec **Zen6** : rendu des cartes (rang, symbole en haut-gauche et bas-droit, gros symbole central), sélection cliquable avec retour visuel doré, overlays centrés pour les résultats de main, les planètes gagnées et l'écran de fin de partie. Tri des cartes par rang croissant comme en console.
- **Boucle de jeu** : `GameController` gère les tours en dépendant uniquement de l'interface `View` (modèle MVC strict). Choix de la vue (console ou graphique) au lancement.

### À faire pour la phase 2

- Au moins deux extensions parmi : score par carte, discards actifs, jokers, monnaie + boutique, blinds à contraintes, deck personnalisable, sauvegarde, mode infini + high scores.
- Polish supplémentaire de l'interface graphique.

### Architecture

Architecture MVC stricte :

```
src/
|-- Main.java                       (point d'entrée)
|-- domain/                         (objets métier, aucune dépendance externe)
|      |-- Card.java
|      |-- Color.java
|      |-- HandDetector.java
|      |-- HandType.java
|      |-- Planet.java
|       -- Rank.java
|-- model/                          (état mutable du jeu)
|      |-- Blind.java
|      |-- Deck.java
|      |-- GameState.java
|      |-- Hand.java
|       -- HandLevels.java
|-- controller/                     (logique de jeu)
|      |-- GameController.java
|       -- ScoreController.java
 -- view/                           (affichage et interaction)
       |-- View.java                (interface scellée)
       |-- ConsoleView.java
       |-- GraphicalView.java
       |-- Palette.java             (couleurs du thème)
        -- Typography.java          (polices, dont Limelight pour le titre)
```

Le `GameController` dépend uniquement de l'interface `View`, ce qui permet d'utiliser plusieurs vues différentes (console, graphique) sans modifier la logique du jeu.