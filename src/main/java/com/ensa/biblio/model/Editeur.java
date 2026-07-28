package com.ensa.biblio.model;

import java.util.Objects;

public class Editeur {

    private int idEditeur;
    private String nomEditeur;

    public Editeur() {
    }

    public Editeur(String nomEditeur) {
        this.nomEditeur = nomEditeur;
    }

    public Editeur(int idEditeur, String nomEditeur) {
        this.idEditeur = idEditeur;
        this.nomEditeur = nomEditeur;
    }

    public int getIdEditeur() {
        return idEditeur;
    }

    public void setIdEditeur(int idEditeur) {
        this.idEditeur = idEditeur;
    }

    public String getNomEditeur() {
        return nomEditeur;
    }

    public void setNomEditeur(String nomEditeur) {
        this.nomEditeur = nomEditeur;
    }

    @Override
    public String toString() {
        return nomEditeur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Editeur)) return false;
        Editeur editeur = (Editeur) o;
        return idEditeur == editeur.idEditeur;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEditeur);
    }
}
