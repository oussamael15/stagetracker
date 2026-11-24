# StageTracker

**StageTracker** est une application desktop développée en Java permettant de gérer et de suivre ses candidatures de stage depuis une interface centralisée.

## Fonctionnalités

- Gestion des candidatures : création et suivi des candidatures.
- Gestion des entreprises : centralisation des informations des entreprises.
- Gestion des contacts : association de contacts aux entreprises et candidatures.
- Gestion des relances : enregistrement et suivi des relances.
- Tableau de bord : visualisation des statistiques et des candidatures à relancer.
- Gestion des statuts : brouillon, envoyée, entretien, acceptée et refusée.
- Persistance des données avec SQLite.

## Technologies utilisées

- **Java 21** – Langage de programmation
- **JavaFX** – Interface graphique
- **Maven** – Gestion des dépendances et construction du projet
- **SQLite** – Base de données
- **AtlantaFX** – Thème graphique

## Architecture du projet

Le projet est organisé en plusieurs packages :

- `model` : classes métier et logique applicative.
- `view` : interfaces graphiques JavaFX.
- `database` : gestion de la connexion et des opérations avec SQLite.

## Installation et lancement

### Prérequis

- JDK 21
- Maven

### Lancement

1. Cloner le dépôt :

   ```bash
   git clone https://github.com/oussamael15/stagetracker.git
   ```

2. Accéder au dossier :

   ```bash
   cd stagetracker
   ```

3. Lancer l'application :

   ```bash
   mvn javafx:run
   ```

## Auteur

**Oussama El Moussaoui**

Projet personnel réalisé dans le cadre de mon parcours en Bachelor Concepteur Développeur.