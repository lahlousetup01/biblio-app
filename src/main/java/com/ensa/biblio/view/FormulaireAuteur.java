package com.ensa.biblio.view;

import com.ensa.biblio.model.Auteur;
import com.ensa.biblio.service.AuteurService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Formulaire modal permettant d'ajouter ou de modifier un auteur.
 */
public class FormulaireAuteur extends JDialog {

    private final JTextField champNom = new JTextField(20);
    private final JTextField champPrenom = new JTextField(20);
    private final JTextField champNationalite = new JTextField(20);
    private final JTextField champDateNaissance = new JTextField(20); // format AAAA-MM-JJ

    private final AuteurService auteurService = new AuteurService();
    private Auteur auteurEnEdition;
    private boolean valide = false;

    public FormulaireAuteur(Frame parent, Auteur auteur) {
        super(parent, auteur == null ? "Nouvel auteur" : "Modifier l'auteur", true);
        this.auteurEnEdition = auteur;
        construireInterface();
        if (auteur != null) {
            preremplir(auteur);
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void construireInterface() {
        JPanel formulaire = new JPanel(new GridBagLayout());
        formulaire.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;

        ajouterLigne(formulaire, c, 0, "Nom * :", champNom);
        ajouterLigne(formulaire, c, 1, "Prénom * :", champPrenom);
        ajouterLigne(formulaire, c, 2, "Nationalité :", champNationalite);
        ajouterLigne(formulaire, c, 3, "Date de naissance (AAAA-MM-JJ) :", champDateNaissance);

        JButton boutonEnregistrer = new JButton("Enregistrer");
        JButton boutonAnnuler = new JButton("Annuler");
        boutonEnregistrer.addActionListener(e -> enregistrer());
        boutonAnnuler.addActionListener(e -> dispose());

        JPanel panneauBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panneauBoutons.add(boutonAnnuler);
        panneauBoutons.add(boutonEnregistrer);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(formulaire, BorderLayout.CENTER);
        getContentPane().add(panneauBoutons, BorderLayout.SOUTH);
    }

    private void ajouterLigne(JPanel panneau, GridBagConstraints c, int ligne, String libelle, JComponent champ) {
        c.gridx = 0; c.gridy = ligne; c.weightx = 0;
        panneau.add(new JLabel(libelle), c);
        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        panneau.add(champ, c);
    }

    private void preremplir(Auteur auteur) {
        champNom.setText(auteur.getNom());
        champPrenom.setText(auteur.getPrenom());
        champNationalite.setText(auteur.getNationalite());
        if (auteur.getDateNaissance() != null) {
            champDateNaissance.setText(auteur.getDateNaissance().toString());
        }
    }

    private void enregistrer() {
        try {
            Auteur auteur = (auteurEnEdition != null) ? auteurEnEdition : new Auteur();
            auteur.setNom(champNom.getText().trim());
            auteur.setPrenom(champPrenom.getText().trim());
            auteur.setNationalite(champNationalite.getText().trim());

            String texteDate = champDateNaissance.getText().trim();
            if (!texteDate.isEmpty()) {
                auteur.setDateNaissance(LocalDate.parse(texteDate));
            }

            if (auteurEnEdition == null) {
                auteurEnEdition = auteurService.ajouterAuteur(auteur);
            } else {
                auteurService.modifierAuteur(auteur);
            }
            valide = true;
            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Saisie invalide", JOptionPane.WARNING_MESSAGE);
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez AAAA-MM-JJ.",
                    "Saisie invalide", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur base de données : " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isValide() {
        return valide;
    }

    public Auteur getAuteur() {
        return auteurEnEdition;
    }
}
