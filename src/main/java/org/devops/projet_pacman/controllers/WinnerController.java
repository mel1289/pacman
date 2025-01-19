package org.devops.projet_pacman.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.devops.projet_pacman.ScreenManager;

public class WinnerController {

    @FXML
    private StackPane btnRetour;

    @FXML
    public void initialize() {
        btnRetour.setOnMouseClicked(e -> ScreenManager.showMainScreen());
    }
}
