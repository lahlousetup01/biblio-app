package com.ensa.biblio.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fournit une connexion unique (patron Singleton) à la base de données MySQL.
 * Adaptez URL, UTILISATEUR et MOT_DE_PASSE à votre environnement local.
 */
public final class ConnexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/biblio_db?useSSL=false&serverTimezone=UTC";
    private static final String UTILISATEUR = "root";
    private static final String MOT_DE_PASSE = "biblio2026";

    private static Connection connexion;

    private ConnexionBD() {
    }

    public static Connection getConnexion() throws SQLException {
        if (connexion == null || connexion.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Pilote JDBC MySQL introuvable dans le classpath.", e);
            }
            connexion = DriverManager.getConnection(URL, UTILISATEUR, MOT_DE_PASSE);
        }
        return connexion;
    }

    public static void fermerConnexion() {
        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
