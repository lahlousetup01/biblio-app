package com.ensa.biblio.dao;

import com.ensa.biblio.model.Auteur;
import com.ensa.biblio.util.ConnexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AuteurDAOImpl implements AuteurDAO {

    @Override
    public Auteur ajouter(Auteur auteur) throws SQLException {
        String sql = "INSERT INTO auteur (nom, prenom, nationalite, date_naissance) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConnexionBD.getConnexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, auteur.getNom());
            ps.setString(2, auteur.getPrenom());
            ps.setString(3, auteur.getNationalite());
            ps.setDate(4, auteur.getDateNaissance() != null ? Date.valueOf(auteur.getDateNaissance()) : null);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    auteur.setIdAuteur(rs.getInt(1));
                }
            }
        }
        return auteur;
    }

    @Override
    public void modifier(Auteur auteur) throws SQLException {
        String sql = "UPDATE auteur SET nom = ?, prenom = ?, nationalite = ?, date_naissance = ? WHERE id_auteur = ?";
        try (Connection cn = ConnexionBD.getConnexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, auteur.getNom());
            ps.setString(2, auteur.getPrenom());
            ps.setString(3, auteur.getNationalite());
            ps.setDate(4, auteur.getDateNaissance() != null ? Date.valueOf(auteur.getDateNaissance()) : null);
            ps.setInt(5, auteur.getIdAuteur());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int idAuteur) throws SQLException {
        String sql = "DELETE FROM auteur WHERE id_auteur = ?";
        try (Connection cn = ConnexionBD.getConnexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAuteur);
            ps.executeUpdate();
        }
    }

    @Override
    public Auteur trouverParId(int idAuteur) throws SQLException {
        String sql = "SELECT * FROM auteur WHERE id_auteur = ?";
        try (Connection cn = ConnexionBD.getConnexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAuteur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Auteur> listerTous() throws SQLException {
        List<Auteur> liste = new ArrayList<>();
        String sql = "SELECT * FROM auteur ORDER BY nom, prenom";
        try (Connection cn = ConnexionBD.getConnexion();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(mapper(rs));
            }
        }
        return liste;
    }

    @Override
    public boolean estUtiliseParUnLivre(int idAuteur) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ecrit_par WHERE id_auteur = ?";
        try (Connection cn = ConnexionBD.getConnexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAuteur);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private Auteur mapper(ResultSet rs) throws SQLException {
        Date date = rs.getDate("date_naissance");
        LocalDate dateNaissance = date != null ? date.toLocalDate() : null;
        return new Auteur(
                rs.getInt("id_auteur"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("nationalite"),
                dateNaissance
        );
    }
}
