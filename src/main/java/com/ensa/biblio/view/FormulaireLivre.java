package com.ensa.biblio.view;

import com.ensa.biblio.model.Auteur;
import com.ensa.biblio.model.Categorie;
import com.ensa.biblio.model.Editeur;
import com.ensa.biblio.model.Livre;
import com.ensa.biblio.service.AuteurService;
import com.ensa.biblio.service.CategorieService;
import com.ensa.biblio.service.EditeurService;
import com.ensa.biblio.service.LivreService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulaire modal permettant d'ajouter ou de modifier un livre,
 * y compris la sélection multiple des auteurs et des catégories.
 */
public class FormulaireLivre extends JDialog {

    private final JTextField champTitre = new JTextField(22);
    private final JSpinner champAnnee = new JSpinner(new SpinnerNumberModel(
            java.time.Year.now().getValue(), 1450, java.time.Year.now().getValue(), 1));
    private final JSpinner champExemplaires = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));
    private final JComboBox<Editeur> comboEditeur = new JComboBox<>();
    private final JList<Auteur> listeAuteurs = new JList<>();
    private final JPanel panneauCategories = new JPanel();
    private final List<JCheckBox> casesCategories = new ArrayList<>();

    private final LivreService livreService = new LivreService();
    private final EditeurService editeurService = new EditeurService();
    private final AuteurService auteurService = new AuteurService();
    private final CategorieService categorieService = new CategorieService();

    private Livre livreEnEdition;
    private boolean valide = false;

    public FormulaireLivre(Frame parent, Livre livre) {
        super(parent, livre == null ? "Nouveau livre" : "Modifier le livre", true);
        this.livreEnEdition = livre;
        try {
            construireInterface();
            chargerListesDeroulantes();
            if (livre != null) {
                preremplir(livre);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent, "Erreur de chargement des données : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        setSize(480, 560);
        setLocationRelativeTo(parent);
    }

    private void construireInterface() {
        JPanel formulaire = new JPanel();
        formulaire.setLayout(new BoxLayout(formulaire, BoxLayout.Y_AXIS));
        formulaire.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        formulaire.add(ligne("Titre * :", champTitre));
        formulaire.add(ligne("Année de publication * :", champAnnee));
        formulaire.add(ligne("Nombre d'exemplaires :", champExemplaires));

        JPanel panneauEditeur = new JPanel(new BorderLayout(5, 0));
        panneauEditeur.add(new JLabel("Éditeur :"), BorderLayout.WEST);
        panneauEditeur.add(comboEditeur, BorderLayout.CENTER);
        JButton boutonNouvelEditeur = new JButton("+ Nouvel éditeur");
        boutonNouvelEditeur.addActionListener(e -> ajouterNouvelEditeur());
        panneauEditeur.add(boutonNouvelEditeur, BorderLayout.EAST);
        panneauEditeur.setAlignmentX(Component.LEFT_ALIGNMENT);
        panneauEditeur.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        formulaire.add(Box.createVerticalStrut(8));
        formulaire.add(panneauEditeur);

        formulaire.add(Box.createVerticalStrut(10));
        JLabel labelAuteurs = new JLabel("Auteur(s) * (Ctrl+clic pour sélection multiple) :");
        labelAuteurs.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulaire.add(labelAuteurs);

        listeAuteurs.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollAuteurs = new JScrollPane(listeAuteurs);
        scrollAuteurs.setPreferredSize(new Dimension(400, 100));
        scrollAuteurs.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulaire.add(scrollAuteurs);

        JButton boutonNouvelAuteur = new JButton("+ Nouvel auteur");
        boutonNouvelAuteur.setAlignmentX(Component.LEFT_ALIGNMENT);
        boutonNouvelAuteur.addActionListener(e -> ajouterNouvelAuteur());
        formulaire.add(boutonNouvelAuteur);

        formulaire.add(Box.createVerticalStrut(10));
        JLabel labelCategories = new JLabel("Catégorie(s) :");
        labelCategories.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulaire.add(labelCategories);

        panneauCategories.setLayout(new BoxLayout(panneauCategories, BoxLayout.Y_AXIS));
        panneauCategories.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scrollCategories = new JScrollPane(panneauCategories);
        scrollCategories.setPreferredSize(new Dimension(400, 80));
        scrollCategories.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulaire.add(scrollCategories);

        JButton boutonEnregistrer = new JButton("Enregistrer");
        JButton boutonAnnuler = new JButton("Annuler");
        boutonEnregistrer.addActionListener(e -> enregistrer());
        boutonAnnuler.addActionListener(e -> dispose());

        JPanel panneauBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panneauBoutons.add(boutonAnnuler);
        panneauBoutons.add(boutonEnregistrer);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(formulaire), BorderLayout.CENTER);
        getContentPane().add(panneauBoutons, BorderLayout.SOUTH);
    }

    private JPanel ligne(String libelle, JComponent champ) {
        JPanel panneau = new JPanel(new BorderLayout(5, 0));
        panneau.add(new JLabel(libelle), BorderLayout.WEST);
        panneau.add(champ, BorderLayout.CENTER);
        panneau.setAlignmentX(Component.LEFT_ALIGNMENT);
        panneau.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return panneau;
    }

    private void chargerListesDeroulantes() throws SQLException {
        comboEditeur.removeAllItems();
        comboEditeur.addItem(null); // Aucun éditeur
        for (Editeur editeur : editeurService.listerTous()) {
            comboEditeur.addItem(editeur);
        }

        List<Auteur> tousLesAuteurs = auteurService.listerTous();
        listeAuteurs.setListData(tousLesAuteurs.toArray(new Auteur[0]));

        panneauCategories.removeAll();
        casesCategories.clear();
        for (Categorie categorie : categorieService.listerToutes()) {
            JCheckBox caseACocher = new JCheckBox(categorie.getLibelle());
            caseACocher.putClientProperty("categorie", categorie);
            casesCategories.add(caseACocher);
            panneauCategories.add(caseACocher);
        }
    }

    private void preremplir(Livre livre) {
        champTitre.setText(livre.getTitre());
        champAnnee.setValue(livre.getAnneePublication());
        champExemplaires.setValue(livre.getNbExemplaires());
        comboEditeur.setSelectedItem(livre.getEditeur());

        // Sélection des auteurs déjà associés dans la JList
        List<Integer> indicesSelectionnes = new ArrayList<>();
        for (int i = 0; i < listeAuteurs.getModel().getSize(); i++) {
            Auteur candidat = listeAuteurs.getModel().getElementAt(i);
            if (livre.getAuteurs().contains(candidat)) {
                indicesSelectionnes.add(i);
            }
        }
        int[] indices = indicesSelectionnes.stream().mapToInt(Integer::intValue).toArray();
        listeAuteurs.setSelectedIndices(indices);

        // Cocher les catégories déjà associées
        for (JCheckBox caseACocher : casesCategories) {
            Categorie categorie = (Categorie) caseACocher.getClientProperty("categorie");
            if (livre.getCategories().contains(categorie)) {
                caseACocher.setSelected(true);
            }
        }
    }

    private void ajouterNouvelEditeur() {
        String nom = JOptionPane.showInputDialog(this, "Nom du nouvel éditeur :");
        if (nom != null && !nom.isBlank()) {
            try {
                Editeur nouvel = editeurService.ajouterEditeur(new Editeur(nom.trim()));
                comboEditeur.addItem(nouvel);
                comboEditeur.setSelectedItem(nouvel);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ajouterNouvelAuteur() {
        FormulaireAuteur dialogue = new FormulaireAuteur((Frame) getOwner(), null);
        dialogue.setVisible(true);
        if (dialogue.isValide()) {
            try {
                List<Auteur> tousLesAuteurs = auteurService.listerTous();
                listeAuteurs.setListData(tousLesAuteurs.toArray(new Auteur[0]));
                // Sélectionne automatiquement le nouvel auteur créé
                int index = tousLesAuteurs.indexOf(dialogue.getAuteur());
                if (index >= 0) {
                    listeAuteurs.addSelectionInterval(index, index);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void enregistrer() {
        try {
            Livre livre = (livreEnEdition != null) ? livreEnEdition : new Livre();
            livre.setTitre(champTitre.getText().trim());
            livre.setAnneePublication((Integer) champAnnee.getValue());
            livre.setNbExemplaires((Integer) champExemplaires.getValue());
            livre.setEditeur((Editeur) comboEditeur.getSelectedItem());

            livre.setAuteurs(new ArrayList<>(listeAuteurs.getSelectedValuesList()));

            List<Categorie> categoriesSelectionnees = new ArrayList<>();
            for (JCheckBox caseACocher : casesCategories) {
                if (caseACocher.isSelected()) {
                    categoriesSelectionnees.add((Categorie) caseACocher.getClientProperty("categorie"));
                }
            }
            livre.setCategories(categoriesSelectionnees);

            if (livreEnEdition == null) {
                livreEnEdition = livreService.ajouterLivre(livre);
            } else {
                livreService.modifierLivre(livre);
            }
            valide = true;
            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Saisie invalide", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur base de données : " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isValide() {
        return valide;
    }
}
