package com.ensa.biblio.view;

import com.ensa.biblio.model.Livre;
import com.ensa.biblio.service.LivreService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Fenêtre principale : tableau du catalogue, barre de recherche et actions CRUD.
 */
public class FenetrePrincipale extends JFrame {

    private final LivreService livreService = new LivreService();
    private final LivreTableModel modeleTable = new LivreTableModel();
    private final JTable tableLivres = new JTable(modeleTable);

    private final JTextField champRechercheTitre = new JTextField(12);
    private final JTextField champRechercheAuteur = new JTextField(12);
    private final JTextField champRechercheAnnee = new JTextField(6);
    private final JTextField champRechercheCategorie = new JTextField(10);

    public FenetrePrincipale() {
        super("Gestion d'une Bibliothèque — ENSA de Fès");
        construireInterface();
        chargerCatalogue();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
    }

    private void construireInterface() {
        setLayout(new BorderLayout(8, 8));

        // ----- Barre de menu -----
        JMenuBar barreMenu = new JMenuBar();
        JMenu menuFichier = new JMenu("Fichier");
        JMenuItem itemQuitter = new JMenuItem("Quitter");
        itemQuitter.addActionListener(e -> System.exit(0));
        menuFichier.add(itemQuitter);
        barreMenu.add(menuFichier);
        setJMenuBar(barreMenu);

        // ----- Barre de recherche -----
        JPanel panneauRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panneauRecherche.setBorder(BorderFactory.createTitledBorder("Recherche multicritère"));
        panneauRecherche.add(new JLabel("Titre :"));
        panneauRecherche.add(champRechercheTitre);
        panneauRecherche.add(new JLabel("Auteur :"));
        panneauRecherche.add(champRechercheAuteur);
        panneauRecherche.add(new JLabel("Année :"));
        panneauRecherche.add(champRechercheAnnee);
        panneauRecherche.add(new JLabel("Catégorie :"));
        panneauRecherche.add(champRechercheCategorie);

        JButton boutonRechercher = new JButton("Rechercher");
        JButton boutonReinitialiser = new JButton("Réinitialiser");
        boutonRechercher.addActionListener(e -> rechercher());
        boutonReinitialiser.addActionListener(e -> { viderChampsRecherche(); chargerCatalogue(); });
        panneauRecherche.add(boutonRechercher);
        panneauRecherche.add(boutonReinitialiser);

        add(panneauRecherche, BorderLayout.NORTH);

        // ----- Tableau -----
        tableLivres.setAutoCreateRowSorter(true);
        tableLivres.setRowHeight(24);
        tableLivres.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tableLivres), BorderLayout.CENTER);

        // ----- Barre d'actions -----
        JPanel panneauActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton boutonAjouter = new JButton("Ajouter un livre");
        JButton boutonModifier = new JButton("Modifier");
        JButton boutonSupprimer = new JButton("Supprimer");
        JButton boutonGererAuteurs = new JButton("Gérer les auteurs");

        boutonAjouter.addActionListener(e -> ouvrirFormulaireLivre(null));
        boutonModifier.addActionListener(e -> modifierLivreSelectionne());
        boutonSupprimer.addActionListener(e -> supprimerLivreSelectionne());
        boutonGererAuteurs.addActionListener(e -> new FenetreGestionAuteurs(this).setVisible(true));

        panneauActions.add(boutonGererAuteurs);
        panneauActions.add(boutonModifier);
        panneauActions.add(boutonSupprimer);
        panneauActions.add(boutonAjouter);
        add(panneauActions, BorderLayout.SOUTH);
    }

    private void chargerCatalogue() {
        try {
            List<Livre> livres = livreService.listerCatalogue();
            modeleTable.setLivres(livres);
        } catch (SQLException e) {
            afficherErreurBD(e);
        }
    }

    private void rechercher() {
        try {
            String titre = champRechercheTitre.getText().trim();
            String auteur = champRechercheAuteur.getText().trim();
            String categorie = champRechercheCategorie.getText().trim();
            String texteAnnee = champRechercheAnnee.getText().trim();
            Integer annee = texteAnnee.isEmpty() ? null : Integer.valueOf(texteAnnee);

            List<Livre> resultats = livreService.rechercher(titre, auteur, annee, categorie);
            modeleTable.setLivres(resultats);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "L'année doit être un nombre.", "Saisie invalide", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            afficherErreurBD(e);
        }
    }

    private void viderChampsRecherche() {
        champRechercheTitre.setText("");
        champRechercheAuteur.setText("");
        champRechercheAnnee.setText("");
        champRechercheCategorie.setText("");
    }

    private void ouvrirFormulaireLivre(Livre livre) {
        FormulaireLivre formulaire = new FormulaireLivre(this, livre);
        formulaire.setVisible(true);
        if (formulaire.isValide()) {
            chargerCatalogue();
        }
    }

    private Livre recupererLigneSelectionnee() {
        int ligneVue = tableLivres.getSelectedRow();
        if (ligneVue == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre dans le tableau.",
                    "Aucune sélection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int ligneModele = tableLivres.convertRowIndexToModel(ligneVue);
        return modeleTable.getLivreALaLigne(ligneModele);
    }

    private void modifierLivreSelectionne() {
        Livre livre = recupererLigneSelectionnee();
        if (livre != null) {
            ouvrirFormulaireLivre(livre);
        }
    }

    private void supprimerLivreSelectionne() {
        Livre livre = recupererLigneSelectionnee();
        if (livre == null) return;

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Supprimer définitivement « " + livre.getTitre() + " » ?",
                "Confirmation de suppression", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                livreService.supprimerLivre(livre.getIdLivre());
                chargerCatalogue();
            } catch (SQLException e) {
                afficherErreurBD(e);
            }
        }
    }

    private void afficherErreurBD(SQLException e) {
        JOptionPane.showMessageDialog(this, "Erreur base de données : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
