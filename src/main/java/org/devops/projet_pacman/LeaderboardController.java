package org.devops.projet_pacman;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LeaderboardController {

    @FXML
    private ImageView logo;

    @FXML
    private Button btnRetour;

    @FXML
    public void initialize() {
        logo.setImage(new Image(getClass().getResource("/org/devops/projet_pacman/logo.png").toExternalForm()));

        logo.setFitHeight(300);
        logo.setFitWidth(1000);

        btnRetour.setOnAction(e -> ScreenManager.showMainScreen());
    }
}
