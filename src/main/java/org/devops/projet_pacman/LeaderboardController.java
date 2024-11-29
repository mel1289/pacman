package org.devops.projet_pacman;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class LeaderboardController {

    @FXML
    private ImageView logo;

    @FXML
    private StackPane btnRetour;

    @FXML
    private ListView<String> scoreList;

    @FXML
    public void initialize() {
        logo.setImage(new Image(getClass().getResource("/org/devops/projet_pacman/logo.png").toExternalForm()));

        scoreList.getItems().addAll(
                "1. Alice - 1500",
                "2. Bob - 1400",
                "3. Charlie - 1300",
                "4. Diana - 1200",
                "5. Eve - 1100"
        );

        // Configurer le bouton "Retour"
        btnRetour.setOnMouseClicked(e -> ScreenManager.showMainScreen());
    }
}
