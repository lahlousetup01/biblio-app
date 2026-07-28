package com.ensa.biblio.service;

import com.ensa.biblio.dao.AuteurDAO;
import com.ensa.biblio.dao.AuteurDAOImpl;
import com.ensa.biblio.model.Auteur;

import java.sql.SQLException;
import java.util.List;

public class AuteurService {

    private final AuteurDAO auteurDAO = new AuteurDAOImpl();

    public Auteur ajouterAuteur(Auteur auteur) throws SQLException {
        valider(auteur);
        return auteurDAO.ajouter(auteur);
    }

    public void modifierAuteur(Auteur auteur) throws SQLException {
        valider(auteur);
        auteurDAO.modifier(auteur);
    }

    /**
     * Supprime un auteur après vérification qu'il n'est associé à aucun livre,
     * afin de préserver l'intégrité référentielle du catalogue.
     */
    public void supprimerAuteur(int idAuteur) throws SQLException {
        if (auteurDAO.estUtiliseParUnLivre(idAuteur)) {
            throw new IllegalStateException(
                    "Impossible de supprimer cet auteur : il est associé à au moins un livre du catalogue.");
        }
        auteurDAO.supprimer(idAuteur);
    }

    public List<Auteur> listerTous() throws SQLException {
        return auteurDAO.listerTous();
    }

    private void valider(Auteur auteur) {
        if (auteur.getNom() == null || auteur.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de l'auteur est obligatoire.");
        }
        if (auteur.getPrenom() == null || auteur.getPrenom().isBlank()) {
            throw new IllegalArgumentException("Le prénom de l'auteur est obligatoire.");
        }
    }
}
