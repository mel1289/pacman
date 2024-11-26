package org.devops.projet_pacman;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScreenManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void showMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource("menu-view.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showLeaderboardScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource("leaderboard-view.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
