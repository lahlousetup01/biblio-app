package com.ensa.biblio.dao;

import com.ensa.biblio.model.Auteur;

import java.sql.SQLException;
import java.util.List;

public interface AuteurDAO {

    Auteur ajouter(Auteur auteur) throws SQLException;

    void modifier(Auteur auteur) throws SQLException;

    void supprimer(int idAuteur) throws SQLException;

    Auteur trouverParId(int idAuteur) throws SQLException;

    List<Auteur> listerTous() throws SQLException;

    /** Vrai si l'auteur est encore référencé par au moins un livre (contrainte d'intégrité). */
    boolean estUtiliseParUnLivre(int idAuteur) throws SQLException;
}
