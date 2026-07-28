package com.ensa.biblio.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Représente un auteur pouvant être associé à plusieurs livres.
 */
public class Auteur {

    private int idAuteur;
    private String nom;
    private String prenom;
    private String nationalite;
    private LocalDate dateNaissance;

    public Auteur() {
    }

    public Auteur(String nom, String prenom, String nationalite, LocalDate dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.nationalite = nationalite;
        this.dateNaissance = dateNaissance;
    }

    public Auteur(int idAuteur, String nom, String prenom, String nationalite, LocalDate dateNaissance) {
        this(nom, prenom, nationalite, dateNaissance);
        this.idAuteur = idAuteur;
    }

    public int getIdAuteur() {
        return idAuteur;
    }

    public void setIdAuteur(int idAuteur) {
        this.idAuteur = idAuteur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        // Utilisé directement dans les JComboBox / JList
        return getNomComplet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Auteur)) return false;
        Auteur auteur = (Auteur) o;
        return idAuteur == auteur.idAuteur;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAuteur);
    }
}
