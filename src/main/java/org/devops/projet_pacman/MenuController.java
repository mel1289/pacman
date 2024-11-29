package org.devops.projet_pacman;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class MenuController {

    @FXML
    private ImageView logo;

    @FXML
    private StackPane playButton;

    @FXML
    private StackPane leaderboardButton;

    @FXML
    private StackPane exitButton;

    @FXML
    public void initialize() {
        logo.setImage(new Image(getClass().getResource("/org/devops/projet_pacman/logo.png").toExternalForm()));

        logo.setFitHeight(300);
        logo.setFitWidth(1000);

        playButton.setOnMouseClicked(e -> ScreenManager.showPlay());

        leaderboardButton.setOnMouseClicked(e -> ScreenManager.showLeaderboardScreen());

        exitButton.setOnMouseClicked(e -> {
            System.exit(0);
        });
    }

}