package com.ensa.biblio.service;

import com.ensa.biblio.dao.LivreDAO;
import com.ensa.biblio.dao.LivreDAOImpl;
import com.ensa.biblio.model.Livre;

import java.sql.SQLException;
import java.time.Year;
import java.util.List;

/**
 * Couche service : applique les règles métier avant de déléguer la persistance au DAO.
 */
public class LivreService {

    private final LivreDAO livreDAO = new LivreDAOImpl();

    public Livre ajouterLivre(Livre livre) throws SQLException {
        valider(livre);
        return livreDAO.ajouter(livre);
    }

    public void modifierLivre(Livre livre) throws SQLException {
        valider(livre);
        livreDAO.modifier(livre);
    }

    public void supprimerLivre(int idLivre) throws SQLException {
        livreDAO.supprimer(idLivre);
    }

    public List<Livre> listerCatalogue() throws SQLException {
        return livreDAO.listerTous();
    }

    public List<Livre> rechercher(String titre, String auteur, Integer annee, String categorie) throws SQLException {
        return livreDAO.rechercher(titre, auteur, annee, categorie);
    }

    /**
     * Règles de validation métier :
     * - le titre est obligatoire ;
     * - l'année doit être comprise entre 1450 (invention de l'imprimerie) et l'année en cours ;
     * - au moins un auteur doit être associé ;
     * - le nombre d'exemplaires ne peut pas être négatif.
     */
    private void valider(Livre livre) {
        if (livre.getTitre() == null || livre.getTitre().isBlank()) {
            throw new IllegalArgumentException("Le titre du livre est obligatoire.");
        }
        int anneeCourante = Year.now().getValue();
        if (livre.getAnneePublication() < 1450 || livre.getAnneePublication() > anneeCourante) {
            throw new IllegalArgumentException(
                    "L'année de publication doit être comprise entre 1450 et " + anneeCourante + ".");
        }
        if (livre.getAuteurs() == null || livre.getAuteurs().isEmpty()) {
            throw new IllegalArgumentException("Un livre doit avoir au moins un auteur.");
        }
        if (livre.getNbExemplaires() < 0) {
            throw new IllegalArgumentException("Le nombre d'exemplaires ne peut pas être négatif.");
        }
    }
}
