package com.ensa.biblio.service;

import com.ensa.biblio.dao.EditeurDAO;
import com.ensa.biblio.dao.EditeurDAOImpl;
import com.ensa.biblio.model.Editeur;

import java.sql.SQLException;
import java.util.List;

public class EditeurService {

    private final EditeurDAO editeurDAO = new EditeurDAOImpl();

    public Editeur ajouterEditeur(Editeur editeur) throws SQLException {
        if (editeur.getNomEditeur() == null || editeur.getNomEditeur().isBlank()) {
            throw new IllegalArgumentException("Le nom de l'éditeur est obligatoire.");
        }
        return editeurDAO.ajouter(editeur);
    }

    public List<Editeur> listerTous() throws SQLException {
        return editeurDAO.listerTous();
    }
}
