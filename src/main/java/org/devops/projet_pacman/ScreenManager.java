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
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource("menu-view.fxml"));
            Parent root = loader.load();

            mainScene = new Scene(root);

            primaryStage.setScene(mainScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMainScreen() {
        setScreenContent("menu-view.fxml");
    }

    public static void showLeaderboardScreen() {
        setScreenContent("leaderboard-view.fxml");
    }

    private static void setScreenContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource(fxmlFile));
            Parent root = loader.load();

            mainScene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
