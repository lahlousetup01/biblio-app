package com.ensa.biblio.service;

import com.ensa.biblio.dao.CategorieDAO;
import com.ensa.biblio.dao.CategorieDAOImpl;
import com.ensa.biblio.model.Categorie;

import java.sql.SQLException;
import java.util.List;

public class CategorieService {

    private final CategorieDAO categorieDAO = new CategorieDAOImpl();

    public Categorie ajouterCategorie(Categorie categorie) throws SQLException {
        if (categorie.getLibelle() == null || categorie.getLibelle().isBlank()) {
            throw new IllegalArgumentException("Le libellé de la catégorie est obligatoire.");
        }
        return categorieDAO.ajouter(categorie);
    }

    public List<Categorie> listerToutes() throws SQLException {
        return categorieDAO.listerToutes();
    }
}
