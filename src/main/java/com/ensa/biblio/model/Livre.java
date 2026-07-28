package com.ensa.biblio.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Représente un livre du catalogue.
 * Un livre peut avoir plusieurs auteurs et plusieurs catégories
 * (relations plusieurs-à-plusieurs), et est rattaché à un éditeur unique.
 */
public class Livre {

    private int idLivre;
    private String titre;
    private int anneePublication;
    private Editeur editeur;
    private int nbExemplaires;
    private List<Auteur> auteurs = new ArrayList<>();
    private List<Categorie> categories = new ArrayList<>();

    public Livre() {
    }

    public Livre(String titre, int anneePublication, Editeur editeur, int nbExemplaires) {
        this.titre = titre;
        this.anneePublication = anneePublication;
        this.editeur = editeur;
        this.nbExemplaires = nbExemplaires;
    }

    public int getIdLivre() {
        return idLivre;
    }

    public void setIdLivre(int idLivre) {
        this.idLivre = idLivre;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public int getAnneePublication() {
        return anneePublication;
    }

    public void setAnneePublication(int anneePublication) {
        this.anneePublication = anneePublication;
    }

    public Editeur getEditeur() {
        return editeur;
    }

    public void setEditeur(Editeur editeur) {
        this.editeur = editeur;
    }

    public int getNbExemplaires() {
        return nbExemplaires;
    }

    public void setNbExemplaires(int nbExemplaires) {
        this.nbExemplaires = nbExemplaires;
    }

    public List<Auteur> getAuteurs() {
        return auteurs;
    }

    public void setAuteurs(List<Auteur> auteurs) {
        this.auteurs = auteurs;
    }

    public void ajouterAuteur(Auteur auteur) {
        if (!this.auteurs.contains(auteur)) {
            this.auteurs.add(auteur);
        }
    }

    public List<Categorie> getCategories() {
        return categories;
    }

    public void setCategories(List<Categorie> categories) {
        this.categories = categories;
    }

    public void ajouterCategorie(Categorie categorie) {
        if (!this.categories.contains(categorie)) {
            this.categories.add(categorie);
        }
    }

    /** Concatène les noms des auteurs, utilisé pour l'affichage dans la JTable. */
    public String getAuteursAffiches() {
        return auteurs.stream().map(Auteur::getNomComplet).collect(Collectors.joining(", "));
    }

    /** Concatène les libellés des catégories, utilisé pour l'affichage dans la JTable. */
    public String getCategoriesAffichees() {
        return categories.stream().map(Categorie::getLibelle).collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return titre + " (" + anneePublication + ")";
    }
}
