# Balatri

Version simplifiée du jeu **Balatro** réalisée en Java dans le cadre du cours de Programmation Orientée Objet (E3 info, ESIEE Paris, 2026).

**Auteurs** : Camélia Antoine et Robin Ciurlik

## Importer le projet dans Eclipse

Le projet est un projet Eclipse classique. Pour l'importer :

1. Dézipper l'archive dans un dossier local.
2. Dans Eclipse : `File` → `Import...` → `General` → `Existing Projects into Workspace` → `Next`.
3. Cliquer sur `Browse...` puis sélectionner le dossier `BalatriProject/`.
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
   - taper `g` puis `Entrée` pour la **vue graphique**.

Les blinds s'enchaînent en mode infini, avec une cible de score croissante et 4 mains par blind. À chaque tour, 8 cartes sont piochées et le joueur en sélectionne 5 pour former une combinaison de poker.

**Vue console** : on tape un par un les indices des 5 cartes à jouer.

**Vue graphique** (Zen6) : on clique sur les cartes pour les sélectionner (elles passent en doré), puis on appuie sur `ESPACE` pour valider la main. Le résultat (combinaison + score) s'affiche dans un overlay ; appuyer sur `ESPACE` pour passer au tour suivant. Idem pour l'annonce d'une planète gagnée après un blind battu.

**Défausse** : avant de jouer, le joueur peut défausser des cartes sélectionnées pour les remplacer par de nouvelles (nombre de défausses limité à 3 par blind). En console, taper `D` à la place d'un indice ; en graphique, cliquer sur le bouton **« Défausser »** (il disparaît quand il n'y a plus de défausses).

**Fin de partie et rejouer** : la partie s'arrête quand le joueur perd une blind. L'écran de fin affiche **le score total, le nombre de blinds battus et le meilleur score de la session** (c'est-à-dire les parties enchaînées tant que la pocessus n'est pas stoppé). On peut alors relancer une partie (console : `o` ; graphique : `ESPACE`) ou quitter (graphique : `Q`).

## État actuel de l'implémentation

### Fonctionnalités complètes

- **Cartes et combinaisons** : enums `Rank` (TWO, ..., ACE) et `Color` (CLUBS, DIAMONDS, HEARTS, SPADES), record `Card`.
- **Détection des 9 combinaisons de Balatri** avec `HandDetector`, avec gestion du cas particulier A-2-3-4-5.
- **Pioche et défausse** : classe `Deck` avec mélange et recyclage automatique de la défausse quand la pioche est insuffisante.
- **État du jeu** : `GameState` gère le blind courant, le score cumulé, les mains restantes, le statut de la partie (gagné / perdu).
- **Blinds** : record `Blind` (nom + score cible).
- **Calcul du score** : `chips × multiplicateur`, dans `ScoreController`.
- **Planètes** : enum `Planet` (9 planètes, une par combinaison). Une planète aléatoire est attribuée à chaque blind battu et augmente de manière permanente les chips et le multiplicateur de la combinaison ciblée.
- **Niveaux courants** : `HandLevels` stocke les chips et multiplicateurs actuels par combinaison.
- **Règles centralisées** : constantes `Hand.CARDS_DRAWN` (8), `Hand.CARDS_PLAYED` (5), `GameState.HANDS_PER_BLIND` (4) et autres référencées par la vue et le contrôleur.
- **Vue console** (`ConsoleView`) : affichage de l'état du jeu, sélection des cartes par saisie d'indices, affichage du résultat de chaque main et des planètes obtenues.
- **Vue graphique** (`GraphicalView`) avec **Zen6** : rendu des cartes (rang, symbole en haut-gauche et bas-droit, gros symbole central), sélection cliquable avec retour visuel doré, overlays centrés pour les résultats de main, les planètes gagnées et l'écran de fin de partie. Tri des cartes par rang croissant comme en console.
- **Boucle de jeu** : `GameController` gère les tours en dépendant uniquement de l'interface `View`. Choix de la vue (console ou graphique) au lancement.

### Extensions implémentées (phase 2)
 
- **Discards actifs (extension B)** : avant de jouer, le joueur peut défausser des cartes sélectionnées et repiocher autant de cartes, dans la limite d'un nombre de défausses à 3 par blind (`GameState.useDiscard` / `getDiscardsRemaining`). Le record `SelectionResult` contient la sélection et l'intention (jouer ou défausser) de la vue vers le contrôleur. Le remplacement se fait *en place* pour que seules les cartes défaussées changent.
- **Score par cartes (extension A)** : en plus de `chips × multiplicateur`, chaque carte jouée ajoute sa valeur en chips au score (`HandLevels.getChipsForCards`, appelé par `ScoreController`).
- **Mode infini & high score (extension H)** : les blinds sont générés à l'infini avec une cible croissante (constantes `BASE_TARGET` et `GROWTH` dans `GameState`). La partie ne se termine que quand il y a une défaite. `GameState` suit le **score total** et le **nombre de blinds battus**, et la classe `HighScore` conserve le meilleur score de la session. À la fin, le joueur peut relancer une partie, le meilleur score est conservé d'une partie à l'autre.

### Architecture

Architecture MVC stricte :

```
src/
|-- Main.java                       (point d'entrée)
|-- domain/                         (objets métier)
|      |-- Card.java
|      |-- Color.java
|      |-- HandDetector.java
|      |-- HandType.java
|      |-- Planet.java
|      |-- Rank.java
		 -- SelectionResult.java     (sélection + intention jouer/défausser)
|-- model/                          (état mutable du jeu)
|      |-- Blind.java
|      |-- Deck.java
|      |-- GameState.java
|      |-- HandLevels.java
		 -- HighScore.java           (meilleur score de la session)
|-- controller/                     (logique de jeu)
|      |-- GameController.java
|       -- ScoreController.java
 -- view/                           (affichage et interaction)
       |-- View.java                (interface scellée)
       |-- ConsoleView.java
       |-- GraphicalView.java
       |-- Palette.java             (couleurs du thème)
        -- Typography.java          (polices)
```

Le `GameController` dépend uniquement de l'interface `View`, ce qui permet d'utiliser plusieurs vues différentes (console, graphique) sans modifier la logique du jeu.