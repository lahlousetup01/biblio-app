package com.ensa.biblio.dao;

import com.ensa.biblio.model.Categorie;

import java.sql.SQLException;
import java.util.List;

public interface CategorieDAO {

    Categorie ajouter(Categorie categorie) throws SQLException;

    List<Categorie> listerToutes() throws SQLException;
}
