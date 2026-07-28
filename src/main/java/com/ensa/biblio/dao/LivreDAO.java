package com.ensa.biblio.dao;

import com.ensa.biblio.model.Livre;

import java.sql.SQLException;
import java.util.List;

public interface LivreDAO {

    Livre ajouter(Livre livre) throws SQLException;

    void modifier(Livre livre) throws SQLException;

    void supprimer(int idLivre) throws SQLException;

    Livre trouverParId(int idLivre) throws SQLException;

    List<Livre> listerTous() throws SQLException;

    /** Recherche multicritère : chaque paramètre nul ou vide est ignoré. */
    List<Livre> rechercher(String titre, String nomAuteur, Integer annee, String categorie) throws SQLException;
}
