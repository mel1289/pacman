package org.devops.projet_pacman;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.devops.projet_pacman.controllers.GameWaitOpponentController;

import java.io.IOException;

public class ScreenManager {

    private static Stage primaryStage;
    private static Scene mainScene;
    private static FXMLLoader lastLoader;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        // stage.setWidth(1280);
        // stage.setHeight(720);
        // stage.setFullScreen(true);
        // stage.setResizable(false);
        // etc.

        if (stage.getScene() == null) {
            mainScene = new Scene(new Parent() {});
            stage.setScene(mainScene);
        } else {
            mainScene = stage.getScene();
        }

        primaryStage.show();
    }

    private static Parent loadRoot(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource(fxmlFile));
        Parent root = loader.load();
        lastLoader = loader;
        return root;
    }

    private static void setScreenContent(String fxmlFile) {
        try {
            Parent newRoot = loadRoot(fxmlFile);
            mainScene.setRoot(newRoot);
            primaryStage.setScene(mainScene);
            primaryStage.show();
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

    public static void showPlayMultiplayer() {
        setScreenContent("views/play-multiplayer-view.fxml");
    }

    public static void showJoinOrCreateParty() {
        setScreenContent("views/join-create-party-view.fxml");
    }

    public static void showGameWaitOpponent(String code) {
        setScreenContent("views/game-wait-opponent-view.fxml");
        GameWaitOpponentController controller = lastLoader.getController();
        controller.displayCode(code);
    }

    public static void showGameOver() {
        setScreenContent("views/loser-view.fxml");
    }

    public static void showWin() {
        setScreenContent("views/winner-view.fxml");
    }
}
