package com.ensa.biblio.view;

import com.ensa.biblio.model.Livre;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class LivreTableModel extends AbstractTableModel {

    private final String[] colonnes = {"Titre", "Année", "Auteur(s)", "Éditeur", "Catégorie(s)", "Exemplaires"};
    private List<Livre> livres = new ArrayList<>();

    public void setLivres(List<Livre> livres) {
        this.livres = livres;
        fireTableDataChanged();
    }

    public Livre getLivreALaLigne(int ligne) {
        return livres.get(ligne);
    }

    @Override
    public int getRowCount() {
        return livres.size();
    }

    @Override
    public int getColumnCount() {
        return colonnes.length;
    }

    @Override
    public String getColumnName(int col) {
        return colonnes[col];
    }

    @Override
    public Object getValueAt(int ligne, int colonne) {
        Livre livre = livres.get(ligne);
        switch (colonne) {
            case 0: return livre.getTitre();
            case 1: return livre.getAnneePublication();
            case 2: return livre.getAuteursAffiches();
            case 3: return livre.getEditeur() != null ? livre.getEditeur().getNomEditeur() : "";
            case 4: return livre.getCategoriesAffichees();
            case 5: return livre.getNbExemplaires();
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
