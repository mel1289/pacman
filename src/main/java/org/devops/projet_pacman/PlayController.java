package org.devops.projet_pacman;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.awt.*;

public class PlayController {

    @FXML
    private ImageView logo;

    @FXML
    private Pane gamePane; // Un Pane dans votre fichier FXML pour afficher le jeu.

    @FXML
    private Button btnRetour;

    @FXML
    public void initialize() {
        logo.setImage(new Image(getClass().getResource("/org/devops/projet_pacman/logo.png").toExternalForm()));

        logo.setFitHeight(300);
        logo.setFitWidth(1000);

        btnRetour.setOnAction(e -> ScreenManager.showMainScreen());

        showGame();
    }

    public void showGame() {
        String[] map = {
                "/////////////////////",
                "/ooooooooo/ooooooooo/",
                "/O///o///o/o///o///O/",
                "/o///o///o/o///o///o/",
                "/ooooooooooooooooooo/",
                "/o///o/o/////o/o///o/",
                "/ooooo/ooo/ooo/ooooo/",
                "/////o///S/S///o/////",
                "SSSS/o/SSSrSSS/o/SSSS",
                "/////o/S//B//S/o/////",
                "SSSSSoSS/bjp/SSoSSSSS",
                "/////o/S/////S/o/////",
                "SSSS/o/SSSSSSS/o/SSSS",
                "/////o/S/////S/o/////",
                "/ooooooooo/ooooooooo/",
                "/O///o///o/o///o///O/",
                "/ooo/oooooPooooo/ooo/",
                "///o/o/o/////o/o/o///",
                "/ooooo/ooo/ooo/ooooo/",
                "/o///////o/o///////o/",
                "/ooooooooooooooooooo/",
                "/////////////////////"
        };

        int rows = map.length;
        // System.out.println("Rows : " + rows);
        int cols = map[0].length();
        // System.out.println("Cols : " + cols);

        // Créer un Canvas
        double canvasWidth = 800; // Largeur souhaitée
        double canvasHeight = 600; // Hauteur souhaitée

        double screenX = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double screenY = Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2;

        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Calculer la taille de chaque carré
        double cellOffsetWidth = (canvasWidth + screenX) / 2;
        System.out.println(canvasHeight + " + " + screenX + " / 2 = " + cellOffsetWidth);

        double cellSizeWidth = canvasWidth / cols;
        double cellSizeHeight = canvasHeight / rows;

        // Dessiner la carte
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                char bloc = map[y].charAt(x);
                if (bloc == '/') {
                    // Dessiner un mur
                    gc.setFill(Color.BLUE); // Couleur des murs
                    double posX = x * cellSizeWidth;
                    double posY = y * cellSizeHeight;
                    System.out.println("Pos x = " + posX + "Pos y = " + posY);
                    System.out.println("Taille bloc x = " + cellSizeWidth + " y = " + cellSizeHeight);
                    gc.fillRect(posX, posY, cellSizeWidth, cellSizeHeight);
                }
            }
        }
        canvas.setLayoutX(screenX - cellOffsetWidth);

        // Ajouter le Canvas au gamePane
        gamePane.getChildren().clear();
        gamePane.getChildren().add(canvas);

    }
}
