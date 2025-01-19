package org.devops.projet_pacman.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.devops.projet_pacman.ScreenManager;

public class MenuController {

    @FXML
    private ImageView logo;

    @FXML
    private StackPane playButton;

    @FXML
    private StackPane multiplayerBtn;

    @FXML
    private StackPane exitButton;

    @FXML
    public void initialize() {
        logo.setImage(new Image(getClass().getResource("/org/devops/projet_pacman/images/logo.png").toExternalForm()));

        logo.setFitHeight(300);
        logo.setFitWidth(1000);

        playButton.setOnMouseClicked(e -> ScreenManager.showPlay());

        multiplayerBtn.setOnMouseClicked(e -> ScreenManager.showJoinOrCreateParty());

        exitButton.setOnMouseClicked(e -> {
            System.exit(0);
        });
    }

}