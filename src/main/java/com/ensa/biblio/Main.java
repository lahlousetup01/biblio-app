package com.ensa.biblio;

import com.ensa.biblio.view.FenetrePrincipale;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Utilise le look and feel natif du système d'exploitation
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Look and feel par défaut de Swing en cas d'échec
        }

        SwingUtilities.invokeLater(() -> {
            FenetrePrincipale fenetre = new FenetrePrincipale();
            fenetre.setVisible(true);
        });
    }
}
