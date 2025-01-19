package org.devops.projet_pacman;

import javafx.application.Application;
import javafx.stage.Stage;

public class MenuApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Définir le stage dans le ScreenManager
        ScreenManager.setPrimaryStage(primaryStage);

        // Afficher l'écran principal (menu)
        ScreenManager.showMainScreen();

        // Paramètres de la fenêtre (ou plein écran) selon tes besoins
        primaryStage.setTitle("PacMan Reborn");
        primaryStage.setFullScreen(true);
        primaryStage.setResizable(false);

        // Pour empêcher la sortie du mode fullscreen par ESC ou autre :
        primaryStage.fullScreenExitHintProperty().setValue("");
        primaryStage.setFullScreenExitKeyCombination(null);

        // Optionnel : forcer le plein écran en permanence
        primaryStage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                primaryStage.setFullScreen(true);
            }
        });

        // Affichage de la fenêtre (déjà fait dans ScreenManager, mais on peut le rappeler ici si besoin)
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
