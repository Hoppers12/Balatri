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
3. Le jeu démarre dans la console Eclipse.

Le joueur affronte 3 blinds à la suite avec 4 mains par blind pour l'instant. Pour chaque main, les 8 cartes piochées sont affichées et on demande au joueur de saisir les indices des 5 cartes à jouer (un par un).

## État actuel de l'implémentation (phase 1)

### Fonctionnalités complètes

- **Cartes et combinaisons** : enums `Rank` (TWO, ..., ACE) et `Color` (CLUBS, DIAMONDS, HEARTS, SPADES), record `Card`.
- **Détection des 9 combinaisons de Balatri** avec `HandDetector`, avec gestion des cas particulier A-2-3-4-5.
- **Pioche et défausse** : classe `Deck` avec mélange (Fisher–Yates shuffle Algorithm : https://www.geeksforgeeks.org/dsa/shuffle-a-given-array-using-fisher-yates-shuffle-algorithm/), recyclage automatique de la défausse quand la pioche est insuffisante.
- **État du jeu** : `GameState` gère le blind courant, le score cumulé, les mains restantes, le statut de la partie (gagné / perdu).
- **Blinds** : record `Blind` (nom + score cible).
- **Calcul du score** : `chips × multiplicateur`, dans `ScoreController`.
- **Planètes** : enum `Planet` (9 planètes, une par combinaison). Une planète aléatoire est donné à chaque blind battu et augmente de manière permanente les chips et le multiplicateur de la combinaison ciblée.
- **Niveaux courants** : `HandLevels` stocke les chips et multiplicateurs actuels par combinaison.
- **Vue console** (`ConsoleView`) : affichage de l'état du jeu, sélection des cartes par saisie d'indices, affichage du résultat de chaque main et des planètes obtenues.
- **Boucle de jeu** : `GameController` gère les tours en dépendant uniquement de l'interface `View` (comme prévu par le modèle MVC).

### Fonctionnalités en cours

- **Vue graphique** (`GraphicalView`) avec la bibliothèque **Zen6** : le squelette de la classe et affichage de l'état du jeu (`showState`) implémenté. L'affichage des cartes et la sélection par click sont en cours de développement et seront finalisés pour la phase 2.
- **Améliorations** à choisir et implémenter pour la phase 2.

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
       |-- View.java                (interface)
       |-- ConsoleView.java
        -- GraphicalView.java       (en cours)
```

Le `GameController` dépend uniquement de l'interface `View`, ce qui permet d'utiliser plusieurs vues différentes (console, graphique) sans modifier la logique du jeu.