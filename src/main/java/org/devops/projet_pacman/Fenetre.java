package org.devops.projet_pacman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Fenetre extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Ma Fenêtre JavaFX");

        Label label = new Label("Bienvenue dans JavaFX !");
        StackPane layout = new StackPane(); // Conteneur
        layout.getChildren().add(label);

        Scene scene = new Scene(layout, 400, 300);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
