package org.devops.projet_pacman.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.devops.projet_pacman.ScreenManager;

public class GameOverController {

    @FXML
    private StackPane retryButton;

    @FXML
    private StackPane menuButton;

    @FXML
    public void initialize() {
        // Associer des actions aux boutons
        retryButton.setOnMouseClicked(event -> {
            System.out.println("Relancer la partie");
            ScreenManager.showPlay(); // Redirige vers la page de jeu
        });

        menuButton.setOnMouseClicked(event -> {
            System.out.println("Retour au menu principal");
            ScreenManager.showMainScreen(); // Redirige vers le menu principal
        });
    }
}
