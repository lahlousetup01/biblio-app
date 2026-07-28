# Gestion d'une Bibliothèque — Application Java (Swing + JDBC)

Implémentation pratique du **Projet 1** (ENSA de Fès — Programmation Objet Java).

## Structure du projet

```
biblio-app/
├── pom.xml
├── sql/
│   └── schema.sql              # Script de création de la base + données d'exemple
└── src/main/java/com/ensa/biblio/
    ├── Main.java                # Point d'entrée
    ├── model/                   # Livre, Auteur, Editeur, Categorie
    ├── dao/                     # Interfaces + implémentations JDBC (CRUD)
    ├── service/                 # Règles métier / validations
    ├── view/                    # Interface graphique Swing
    └── util/ConnexionBD.java    # Singleton de connexion JDBC
```

## Prérequis

- JDK 17 ou supérieur
- Maven 3.8+
- MySQL 8 (ou MariaDB) démarré localement

## 1. Créer la base de données

```bash
mysql -u root -p < sql/schema.sql
```

## 2. Configurer la connexion

Adaptez si besoin les identifiants dans
`src/main/java/com/ensa/biblio/util/ConnexionBD.java` :

```java
private static final String URL = "jdbc:mysql://localhost:3306/biblio_db?useSSL=false&serverTimezone=UTC";
private static final String UTILISATEUR = "root";
private static final String MOT_DE_PASSE = "root";
```

## 3. Compiler et lancer

```bash
mvn clean package
java -jar target/biblio-app.jar
```

Ou directement depuis votre IDE (IntelliJ / Eclipse) en exécutant `Main.java`
après avoir importé le projet comme projet Maven.

## Fonctionnalités implémentées

- CRUD complet sur les livres (ajout / modification / suppression / affichage)
- Gestion de la relation plusieurs-à-plusieurs Livre ↔ Auteur (JList à sélection
  multiple + tables de jonction `ecrit_par`)
- Gestion de la relation plusieurs-à-plusieurs Livre ↔ Catégorie (cases à cocher
  + table de jonction `livre_categorie`)
- Ajout à la volée d'un nouvel auteur ou d'un nouvel éditeur depuis le formulaire
- Recherche multicritère (titre, auteur, année, catégorie) avec requête SQL
  dynamique
- Gestion des auteurs (CRUD) dans une fenêtre dédiée, avec vérification
  d'intégrité avant suppression (un auteur associé à un livre ne peut pas être
  supprimé)
- Validation métier côté service (titre obligatoire, année plausible, au moins
  un auteur, exemplaires ≥ 0)
- Transactions JDBC (commit/rollback) lors de l'ajout et de la modification
  d'un livre, pour garantir la cohérence entre `livre` et ses tables de
  jonction

## Remarque sur la compilation

Ce code a été rédigé et relu attentivement mais n'a pas pu être compilé dans
cet environnement (JDK absent du bac à sable). Compilez-le avec `mvn package`
dans votre environnement local avant de le rendre ; corrigez si besoin de
petites erreurs de syntaxe résiduelles.
