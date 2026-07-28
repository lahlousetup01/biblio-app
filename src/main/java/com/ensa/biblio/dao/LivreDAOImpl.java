package com.ensa.biblio.dao;

import com.ensa.biblio.model.Auteur;
import com.ensa.biblio.model.Categorie;
import com.ensa.biblio.model.Editeur;
import com.ensa.biblio.model.Livre;
import com.ensa.biblio.util.ConnexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC de LivreDAO.
 * Gère en particulier les relations plusieurs-à-plusieurs Livre-Auteur
 * et Livre-Categorie via les tables de jonction ecrit_par / livre_categorie.
 */
public class LivreDAOImpl implements LivreDAO {

    @Override
    public Livre ajouter(Livre livre) throws SQLException {
        String sqlLivre = "INSERT INTO livre (titre, annee_publication, id_editeur, nb_exemplaires) VALUES (?, ?, ?, ?)";

        Connection cn = null;
        try {
            cn = ConnexionBD.getConnexion();
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(sqlLivre, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, livre.getTitre());
                ps.setInt(2, livre.getAnneePublication());
                if (livre.getEditeur() != null) {
                    ps.setInt(3, livre.getEditeur().getIdEditeur());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.setInt(4, livre.getNbExemplaires());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        livre.setIdLivre(rs.getInt(1));
                    }
                }
            }

            insererLiaisonsAuteurs(cn, livre);
            insererLiaisonsCategories(cn, livre);

            cn.commit();
        } catch (SQLException e) {
            if (cn != null) cn.rollback();
            throw e;
        } finally {
            if (cn != null) cn.setAutoCommit(true);
        }
        return livre;
    }

    @Override
    public void modifier(Livre livre) throws SQLException {
        String sqlLivre = "UPDATE livre SET titre = ?, annee_publication = ?, id_editeur = ?, nb_exemplaires = ? WHERE id_livre = ?";

        Connection cn = null;
        try {
            cn = ConnexionBD.getConnexion();
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(sqlLivre)) {
                ps.setString(1, livre.getTitre());
                ps.setInt(2, livre.getAnneePublication());
                if (livre.getEditeur() != null) {
                    ps.setInt(3, livre.getEditeur().getIdEditeur());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.setInt(4, livre.getNbExemplaires());
                ps.setInt(5, livre.getIdLivre());
                ps.executeUpdate();
            }

            // On repart d'une liste vierge de liaisons pour refléter fidèlement la sélection de l'utilisateur.
            try (PreparedStatement ps = cn.prepareStatement("DELETE FROM ecrit_par WHERE id_livre = ?")) {
                ps.setInt(1, livre.getIdLivre());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = cn.prepareStatement("DELETE FROM livre_categorie WHERE id_livre = ?")) {
                ps.setInt(1, livre.getIdLivre());
                ps.executeUpdate();
            }

            insererLiaisonsAuteurs(cn, livre);
            insererLiaisonsCategories(cn, livre);

            cn.commit();
        } catch (SQLException e) {
            if (cn != null) cn.rollback();
            throw e;
        } finally {
            if (cn != null) cn.setAutoCommit(true);
        }
    }

    @Override
    public void supprimer(int idLivre) throws SQLException {
        // Les suppressions dans ecrit_par et livre_categorie sont automatiques (ON DELETE CASCADE).
        String sql = "DELETE FROM livre WHERE id_livre = ?";
        try (Connection cn = ConnexionBD.getConnexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idLivre);
            ps.executeUpdate();
        }
    }

    @Override
    public Livre trouverParId(int idLivre) throws SQLException {
        String sql = "SELECT l.*, e.nom_editeur FROM livre l " +
                "LEFT JOIN editeur e ON l.id_editeur = e.id_editeur WHERE l.id_livre = ?";
        try (Connection cn = ConnexionBD.getConnexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idLivre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Livre livre = mapperLivre(rs);
                    chargerAuteurs(cn, livre);
                    chargerCategories(cn, livre);
                    return livre;
                }
            }
        }
        return null;
    }

    @Override
    public List<Livre> listerTous() throws SQLException {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT l.*, e.nom_editeur FROM livre l " +
                "LEFT JOIN editeur e ON l.id_editeur = e.id_editeur ORDER BY l.titre";

        try (Connection cn = ConnexionBD.getConnexion();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                livres.add(mapperLivre(rs));
            }
            for (Livre livre : livres) {
                chargerAuteurs(cn, livre);
                chargerCategories(cn, livre);
            }
        }
        return livres;
    }

    @Override
    public List<Livre> rechercher(String titre, String nomAuteur, Integer annee, String categorie) throws SQLException {
        // Construction dynamique de la requête selon les critères fournis.
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT l.*, ed.nom_editeur FROM livre l " +
                "LEFT JOIN editeur ed ON l.id_editeur = ed.id_editeur " +
                "LEFT JOIN ecrit_par ep ON l.id_livre = ep.id_livre " +
                "LEFT JOIN auteur a ON ep.id_auteur = a.id_auteur " +
                "LEFT JOIN livre_categorie lc ON l.id_livre = lc.id_livre " +
                "LEFT JOIN categorie c ON lc.id_categorie = c.id_categorie " +
                "WHERE 1 = 1 ");

        List<Object> parametres = new ArrayList<>();

        if (titre != null && !titre.isBlank()) {
            sql.append("AND l.titre LIKE ? ");
            parametres.add("%" + titre + "%");
        }
        if (nomAuteur != null && !nomAuteur.isBlank()) {
            sql.append("AND (a.nom LIKE ? OR a.prenom LIKE ?) ");
            parametres.add("%" + nomAuteur + "%");
            parametres.add("%" + nomAuteur + "%");
        }
        if (annee != null) {
            sql.append("AND l.annee_publication = ? ");
            parametres.add(annee);
        }
        if (categorie != null && !categorie.isBlank()) {
            sql.append("AND c.libelle LIKE ? ");
            parametres.add("%" + categorie + "%");
        }
        sql.append("ORDER BY l.titre");

        List<Livre> resultats = new ArrayList<>();
        try (Connection cn = ConnexionBD.getConnexion();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametres.size(); i++) {
                ps.setObject(i + 1, parametres.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultats.add(mapperLivre(rs));
                }
            }
            for (Livre livre : resultats) {
                chargerAuteurs(cn, livre);
                chargerCategories(cn, livre);
            }
        }
        return resultats;
    }

    // ------------------------------------------------------------------
    // Méthodes utilitaires privées
    // ------------------------------------------------------------------

    private void insererLiaisonsAuteurs(Connection cn, Livre livre) throws SQLException {
        String sql = "INSERT INTO ecrit_par (id_livre, id_auteur) VALUES (?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            for (Auteur auteur : livre.getAuteurs()) {
                ps.setInt(1, livre.getIdLivre());
                ps.setInt(2, auteur.getIdAuteur());
                ps.addBatch();
            }
            if (!livre.getAuteurs().isEmpty()) {
                ps.executeBatch();
            }
        }
    }

    private void insererLiaisonsCategories(Connection cn, Livre livre) throws SQLException {
        String sql = "INSERT INTO livre_categorie (id_livre, id_categorie) VALUES (?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            for (Categorie categorie : livre.getCategories()) {
                ps.setInt(1, livre.getIdLivre());
                ps.setInt(2, categorie.getIdCategorie());
                ps.addBatch();
            }
            if (!livre.getCategories().isEmpty()) {
                ps.executeBatch();
            }
        }
    }

    private void chargerAuteurs(Connection cn, Livre livre) throws SQLException {
        String sql = "SELECT a.* FROM auteur a " +
                "JOIN ecrit_par ep ON a.id_auteur = ep.id_auteur WHERE ep.id_livre = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, livre.getIdLivre());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date date = rs.getDate("date_naissance");
                    LocalDate dn = date != null ? date.toLocalDate() : null;
                    livre.ajouterAuteur(new Auteur(
                            rs.getInt("id_auteur"), rs.getString("nom"),
                            rs.getString("prenom"), rs.getString("nationalite"), dn));
                }
            }
        }
    }

    private void chargerCategories(Connection cn, Livre livre) throws SQLException {
        String sql = "SELECT c.* FROM categorie c " +
                "JOIN livre_categorie lc ON c.id_categorie = lc.id_categorie WHERE lc.id_livre = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, livre.getIdLivre());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    livre.ajouterCategorie(new Categorie(rs.getInt("id_categorie"), rs.getString("libelle")));
                }
            }
        }
    }

    private Livre mapperLivre(ResultSet rs) throws SQLException {
        Livre livre = new Livre();
        livre.setIdLivre(rs.getInt("id_livre"));
        livre.setTitre(rs.getString("titre"));
        livre.setAnneePublication(rs.getInt("annee_publication"));
        livre.setNbExemplaires(rs.getInt("nb_exemplaires"));

        int idEditeur = rs.getInt("id_editeur");
        if (!rs.wasNull()) {
            livre.setEditeur(new Editeur(idEditeur, rs.getString("nom_editeur")));
        }
        return livre;
    }
}
