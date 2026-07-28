package com.ensa.biblio.view;

import com.ensa.biblio.model.Auteur;
import com.ensa.biblio.service.AuteurService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Fenêtre modale listant les auteurs et permettant d'en ajouter, modifier ou supprimer.
 */
public class FenetreGestionAuteurs extends JDialog {

    private final AuteurService auteurService = new AuteurService();
    private final DefaultTableModel modeleTable =
            new DefaultTableModel(new Object[]{"ID", "Nom", "Prénom", "Nationalité", "Date de naissance"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable table = new JTable(modeleTable);

    public FenetreGestionAuteurs(Frame parent) {
        super(parent, "Gestion des auteurs", true);
        construireInterface();
        chargerAuteurs();
        setSize(560, 420);
        setLocationRelativeTo(parent);
    }

    private void construireInterface() {
        setLayout(new BorderLayout(8, 8));
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panneauActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton boutonAjouter = new JButton("Ajouter");
        JButton boutonModifier = new JButton("Modifier");
        JButton boutonSupprimer = new JButton("Supprimer");
        JButton boutonFermer = new JButton("Fermer");

        boutonAjouter.addActionListener(e -> ouvrirFormulaire(null));
        boutonModifier.addActionListener(e -> modifierSelection());
        boutonSupprimer.addActionListener(e -> supprimerSelection());
        boutonFermer.addActionListener(e -> dispose());

        panneauActions.add(boutonFermer);
        panneauActions.add(boutonModifier);
        panneauActions.add(boutonSupprimer);
        panneauActions.add(boutonAjouter);
        add(panneauActions, BorderLayout.SOUTH);
    }

    private void chargerAuteurs() {
        try {
            modeleTable.setRowCount(0);
            List<Auteur> auteurs = auteurService.listerTous();
            for (Auteur a : auteurs) {
                modeleTable.addRow(new Object[]{
                        a.getIdAuteur(), a.getNom(), a.getPrenom(), a.getNationalite(),
                        a.getDateNaissance() != null ? a.getDateNaissance().toString() : ""
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur base de données : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ouvrirFormulaire(Auteur auteur) {
        FormulaireAuteur formulaire = new FormulaireAuteur((Frame) getOwner(), auteur);
        formulaire.setVisible(true);
        if (formulaire.isValide()) {
            chargerAuteurs();
        }
    }

    private Integer idSelectionne() {
        int ligneVue = table.getSelectedRow();
        if (ligneVue == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un auteur.",
                    "Aucune sélection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int ligneModele = table.convertRowIndexToModel(ligneVue);
        return (Integer) modeleTable.getValueAt(ligneModele, 0);
    }

    private void modifierSelection() {
        Integer id = idSelectionne();
        if (id == null) return;
        try {
            for (Auteur a : auteurService.listerTous()) {
                if (a.getIdAuteur() == id) {
                    ouvrirFormulaire(a);
                    return;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur base de données : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerSelection() {
        Integer id = idSelectionne();
        if (id == null) return;

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Supprimer cet auteur ?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmation != JOptionPane.YES_OPTION) return;

        try {
            auteurService.supprimerAuteur(id);
            chargerAuteurs();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Suppression impossible", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur base de données : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
