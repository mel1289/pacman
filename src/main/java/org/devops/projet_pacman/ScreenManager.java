package org.devops.projet_pacman;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScreenManager {

    private static Stage primaryStage;
    private static Scene mainScene;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;

        try {
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource("views/menu-view.fxml"));
            Parent root = loader.load();

            mainScene = new Scene(root);

            primaryStage.setScene(mainScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMainScreen() {
        setScreenContent("views/menu-view.fxml");
    }

    public static void showLeaderboardScreen() {
        setScreenContent("views/leaderboard-view.fxml");
    }

    public static void showGameScreen() {
        setScreenContent("views/game-view.fxml");
    }

    public static void showPlay() {
        setScreenContent("views/play-view.fxml");
    }

    private static void setScreenContent(String fxmlFile) {

        System.out.println(fxmlFile);

        try {
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource(fxmlFile));
            Parent root = loader.load();

            mainScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
