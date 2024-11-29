package org.devops.projet_pacman;

import javafx.application.Application;
import javafx.stage.Stage;

public class MenuApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Configurer le gestionnaire d'écran
            ScreenManager.setPrimaryStage(primaryStage);
            ScreenManager.showMainScreen();

            // Configuration de la fenêtre principale
            primaryStage.setTitle("PacMan Reborn");
            primaryStage.setMinHeight(700);
            primaryStage.setMinWidth(1000);
            primaryStage.setFullScreen(true);

            // Supprime l'indication par défaut du plein écran
            primaryStage.fullScreenExitHintProperty().setValue("");
            primaryStage.setFullScreenExitKeyCombination(null);

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
