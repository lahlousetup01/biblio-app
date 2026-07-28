package com.ensa.biblio.dao;

import com.ensa.biblio.model.Editeur;

import java.sql.SQLException;
import java.util.List;

public interface EditeurDAO {

    Editeur ajouter(Editeur editeur) throws SQLException;

    List<Editeur> listerTous() throws SQLException;
}
