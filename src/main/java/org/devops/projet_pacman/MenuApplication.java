package org.devops.projet_pacman;

import javafx.application.Application;
import javafx.stage.Stage;

public class MenuApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        ScreenManager.setPrimaryStage(primaryStage);
        ScreenManager.showMainScreen();

        primaryStage.setTitle("PacMan Reborn");

        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(1000);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}