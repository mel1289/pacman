package org.devops.projet_pacman;

import javafx.application.Application;
import javafx.stage.Stage;

public class MenuApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            ScreenManager.setPrimaryStage(primaryStage);
            ScreenManager.showMainScreen();

            primaryStage.setTitle("PacMan Reborn");
            primaryStage.setMinHeight(700);
            primaryStage.setMinWidth(1000);
            primaryStage.setFullScreen(true);
            primaryStage.setResizable(false);

            primaryStage.fullScreenExitHintProperty().setValue("");
            primaryStage.setFullScreenExitKeyCombination(null);

            primaryStage.fullScreenProperty().addListener((obs, wasFullScreen, isFullScreen) -> {
                if (!isFullScreen) {
                    primaryStage.setFullScreen(true);
                }
            });


            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
