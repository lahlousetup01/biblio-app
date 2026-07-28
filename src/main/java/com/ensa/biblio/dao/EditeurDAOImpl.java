package com.ensa.biblio.dao;

import com.ensa.biblio.model.Editeur;
import com.ensa.biblio.util.ConnexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EditeurDAOImpl implements EditeurDAO {

    @Override
    public Editeur ajouter(Editeur editeur) throws SQLException {
        String sql = "INSERT INTO editeur (nom_editeur) VALUES (?)";
        try (Connection cn = ConnexionBD.getConnexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, editeur.getNomEditeur());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    editeur.setIdEditeur(rs.getInt(1));
                }
            }
        }
        return editeur;
    }

    @Override
    public List<Editeur> listerTous() throws SQLException {
        List<Editeur> liste = new ArrayList<>();
        String sql = "SELECT * FROM editeur ORDER BY nom_editeur";
        try (Connection cn = ConnexionBD.getConnexion();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(new Editeur(rs.getInt("id_editeur"), rs.getString("nom_editeur")));
            }
        }
        return liste;
    }
}
