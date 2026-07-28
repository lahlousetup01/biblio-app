package com.ensa.biblio.dao;

import com.ensa.biblio.model.Categorie;
import com.ensa.biblio.util.ConnexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieDAOImpl implements CategorieDAO {

    @Override
    public Categorie ajouter(Categorie categorie) throws SQLException {
        String sql = "INSERT INTO categorie (libelle) VALUES (?)";
        try (Connection cn = ConnexionBD.getConnexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categorie.getLibelle());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    categorie.setIdCategorie(rs.getInt(1));
                }
            }
        }
        return categorie;
    }

    @Override
    public List<Categorie> listerToutes() throws SQLException {
        List<Categorie> liste = new ArrayList<>();
        String sql = "SELECT * FROM categorie ORDER BY libelle";
        try (Connection cn = ConnexionBD.getConnexion();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(new Categorie(rs.getInt("id_categorie"), rs.getString("libelle")));
            }
        }
        return liste;
    }
}
