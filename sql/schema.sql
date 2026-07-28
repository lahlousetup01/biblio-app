-- =========================================================
-- Base de données : Gestion d'une Bibliothèque
-- ENSA de Fès - Programmation Objet Java
-- =========================================================

CREATE DATABASE IF NOT EXISTS biblio_db CHARACTER SET utf8mb4;
USE biblio_db;

-- ---------------------------------------------------------
-- Table EDITEUR
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS editeur (
    id_editeur   INT AUTO_INCREMENT PRIMARY KEY,
    nom_editeur  VARCHAR(120) NOT NULL
);

-- ---------------------------------------------------------
-- Table AUTEUR
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS auteur (
    id_auteur       INT AUTO_INCREMENT PRIMARY KEY,
    nom             VARCHAR(80)  NOT NULL,
    prenom          VARCHAR(80)  NOT NULL,
    nationalite     VARCHAR(60),
    date_naissance  DATE
);

-- ---------------------------------------------------------
-- Table CATEGORIE
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS categorie (
    id_categorie INT AUTO_INCREMENT PRIMARY KEY,
    libelle      VARCHAR(80) NOT NULL
);

-- ---------------------------------------------------------
-- Table LIVRE
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS livre (
    id_livre           INT AUTO_INCREMENT PRIMARY KEY,
    titre              VARCHAR(150) NOT NULL,
    annee_publication  INT NOT NULL,
    id_editeur         INT,
    nb_exemplaires     INT DEFAULT 1,
    CONSTRAINT fk_livre_editeur FOREIGN KEY (id_editeur)
        REFERENCES editeur(id_editeur) ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Table de jonction LIVRE <-> AUTEUR (plusieurs-à-plusieurs)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS ecrit_par (
    id_livre  INT NOT NULL,
    id_auteur INT NOT NULL,
    PRIMARY KEY (id_livre, id_auteur),
    CONSTRAINT fk_ep_livre  FOREIGN KEY (id_livre)  REFERENCES livre(id_livre)   ON DELETE CASCADE,
    CONSTRAINT fk_ep_auteur FOREIGN KEY (id_auteur) REFERENCES auteur(id_auteur) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Table de jonction LIVRE <-> CATEGORIE (plusieurs-à-plusieurs)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS livre_categorie (
    id_livre     INT NOT NULL,
    id_categorie INT NOT NULL,
    PRIMARY KEY (id_livre, id_categorie),
    CONSTRAINT fk_lc_livre     FOREIGN KEY (id_livre)     REFERENCES livre(id_livre)         ON DELETE CASCADE,
    CONSTRAINT fk_lc_categorie FOREIGN KEY (id_categorie) REFERENCES categorie(id_categorie) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Données d'exemple
-- ---------------------------------------------------------
INSERT INTO editeur (nom_editeur) VALUES ('Gallimard'), ('O''Reilly Media'), ('Dunod');

INSERT INTO auteur (nom, prenom, nationalite, date_naissance) VALUES
('Hugo', 'Victor', 'Française', '1802-02-26'),
('Bloch', 'Joshua', 'Américaine', '1961-08-28'),
('Gamma', 'Erich', 'Suisse', '1961-03-13');

INSERT INTO categorie (libelle) VALUES ('Roman'), ('Informatique'), ('Génie logiciel');

INSERT INTO livre (titre, annee_publication, id_editeur, nb_exemplaires) VALUES
('Les Misérables', 1862, 1, 4),
('Effective Java', 2018, 2, 6),
('Design Patterns', 1994, 3, 3);

INSERT INTO ecrit_par (id_livre, id_auteur) VALUES
(1, 1),  -- Les Misérables -> Victor Hugo
(2, 2),  -- Effective Java -> Joshua Bloch
(3, 3);  -- Design Patterns -> Erich Gamma

INSERT INTO livre_categorie (id_livre, id_categorie) VALUES
(1, 1),
(2, 2),
(3, 3), (3, 2);
