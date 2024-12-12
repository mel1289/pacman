package org.devops.projet_pacman.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import org.devops.projet_pacman.ScreenManager;
import org.devops.projet_pacman.entities.Map;
import org.devops.projet_pacman.entities.Pacman;

import java.awt.*;

public class PlayController {

    @FXML
    private ImageView logo;

    @FXML
    private Pane gamePane; // Un Pane dans votre fichier FXML pour afficher le jeu.

    @FXML
    private StackPane btnRetour;

    @FXML
    private Text scoreText;

    private Map map;
    private Pacman pacman;

    @FXML
    public void initialize() {
        btnRetour.setOnMouseClicked(e -> ScreenManager.showMainScreen());

        VBox.setMargin(btnRetour, new Insets(70, 0, 0, 0));

        String[] base_map = {
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

        map = new Map(base_map);
        pacman = new Pacman(0, 0); // Position initiale de Pac-Man

        updateMap();

        gamePane.setOnKeyPressed(this::handleKeyPress);
        gamePane.setFocusTraversable(true);
        gamePane.requestFocus();
    }

    public void updateMap() {
        int height = map.getHeight();
        int width = map.getWidth();

        double canvasWidth = 700;
        double canvasHeight = 600;

        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        scoreText.setText(String.valueOf(pacman.getScore()));

        double cellWidth = canvasWidth / width;
        double cellHeight = canvasHeight / height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char tile = map.getTile(y, x);
                double posX = x * cellWidth;
                double posY = y * cellHeight;

                switch (tile) {
                    case '/':
                        gc.setFill(Color.BLUE);
                        gc.fillRect(posX, posY, cellWidth, cellHeight);
                        break;
                    case 'o':
                        double dotWidth = cellWidth / 4;
                        double dotHeight = cellHeight / 4;
                        gc.setFill(Color.WHITE);
                        gc.fillOval(posX + (cellWidth / 2) - (dotWidth / 2), posY + (cellHeight / 2) - (dotHeight / 2), dotWidth, dotHeight);
                        break;
                    case 'O':
                        double bigDotWidth = cellWidth / 2;
                        double bigDotHeight = cellHeight / 2;
                        gc.setFill(Color.WHITE);
                        gc.fillOval(posX + (cellWidth / 2) - (bigDotWidth / 2), posY + (cellHeight / 2) - (bigDotHeight / 2), bigDotWidth, bigDotHeight);
                        break;
                    case 'P':
                        double pacmanWidth = cellWidth;
                        double pacmanHeight = cellHeight;
                        gc.setFill(Color.YELLOW);
                        gc.fillOval(posX + (cellWidth / 2) - (pacmanWidth / 2), posY + (cellHeight / 2) - (pacmanHeight / 2), pacmanWidth, pacmanHeight);
                        pacman.setPosX(x);
                        pacman.setPosY(y);
                        break;
                }
            }
        }

        double screenX = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double canvasOffsetWidth = screenX - ((canvasWidth + screenX) / 2);
        canvas.setLayoutX(canvasOffsetWidth);

        gamePane.getChildren().clear();
        gamePane.getChildren().add(canvas);
    }

    private void handleKeyPress(KeyEvent event) {
        int newX = pacman.getPosX();
        int newY = pacman.getPosY();
        int oldX = pacman.getPosX();
        int oldY = pacman.getPosY();

        KeyCode code = event.getCode();

        switch (code) {
            case UP -> newY -= 1;
            case DOWN -> newY += 1;
            case LEFT -> newX -= 1;
            case RIGHT -> newX += 1;
        }

        if (map.isWalkable(newY, newX)) {
            switch (code) {
                case UP -> pacman.moveUp(map);
                case DOWN -> pacman.moveDown(map);
                case LEFT -> pacman.moveLeft(map);
                case RIGHT -> pacman.moveRight(map);
            }
            map.updateTile(oldY, oldX, 'S');
            map.updateTile(newY, newX, 'P');
        }
        event.consume();
        updateMap();
    }
}
