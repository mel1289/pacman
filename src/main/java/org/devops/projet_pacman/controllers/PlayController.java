package org.devops.projet_pacman.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.devops.projet_pacman.MenuApplication;
import org.devops.projet_pacman.ScreenManager;
import org.devops.projet_pacman.entities.Ghost;
import org.devops.projet_pacman.entities.Map;
import org.devops.projet_pacman.entities.Pacman;

import java.awt.*;
import java.util.Random;

public class PlayController {

    @FXML
    private Pane gamePane; // Un Pane dans votre fichier FXML pour afficher le jeu.

    @FXML
    private StackPane btnRetour;

    @FXML
    private Text scoreText;

    private boolean isMouseOpen = true;
    private final Image pacmanOpen = new Image(getClass().getResource("/org/devops/projet_pacman/images/pacman_opened.png").toExternalForm());
    private final Image pacmanClosed = new Image(getClass().getResource("/org/devops/projet_pacman/images/pacman_closed.png").toExternalForm());

    private KeyCode currentDirection = null;  // Direction actuelle de Pacman
    private AnimationTimer movementTimer = null;  // Timer pour gérer le mouvement continu
    private boolean isMoving = false;  // Indicateur si Pacman est en mouvement

    private Map map;
    private Pacman pacman;

    private Ghost ghost;

    private char ghostDirection = 'R';

    private static final double PACMAN_SPEED = 0.2;  // Ralentir Pacman, déplacer toutes les 0.5 secondes
    private double lastMoveTime = 0;  // Temps depuis le dernier mouvement

    private MenuApplication menuApplication;

    // AnimationTimer pour le mouvement continu

    @FXML
    public void initialize() {
        btnRetour.setOnMouseClicked(e -> ScreenManager.showMainScreen());

        //pacmanImage.setLayoutX(gamePane.getPrefWidth() / 2);
        //pacmanImage.setLayoutY(gamePane.getPrefHeight() / 2);

        btnRetour.setOnMouseClicked(e -> ScreenManager.showMainScreen());

       // VBox.setMargin(btnRetour, new Insets(-10, 0, 0, 0));

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
        pacman = new Pacman(0, 0, "/org/devops/projet_pacman/images/pacman_opened.png");

        ghost = new Ghost(0, 0, "/org/devops/projet_pacman/images/ghost.png");

        gamePane.getChildren().add(pacman.getImage());
        gamePane.getChildren().add(ghost.getImage());

        updateMap();

        gamePane.setOnKeyPressed(this::handleKeyPress);
        gamePane.setFocusTraversable(true);
        gamePane.requestFocus();
    }

    public void updateMap() {
        int height = map.getHeight();
        int width = map.getWidth();

        double canvasHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.7;

        double aspectRatio = (double) width / height;
        double canvasWidth = canvasHeight * aspectRatio;

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

                // System.out.println(x + " * " + cellWidth + " = " + posX);

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
                    case 'b':
                        double ghostWidth = cellWidth;
                        double ghostHeight = cellHeight;

                        ghost.getImage().setFitWidth(ghostWidth);
                        ghost.getImage().setFitHeight(ghostHeight);

                        double paneWidth2 = Toolkit.getDefaultToolkit().getScreenSize().width; //gamePane.getWidth();

                        posX = posX + ((paneWidth2 - canvasWidth) / 2);

                        ghost.setPosX(x);
                        ghost.setPosY(y);

                        ghost.getImage().setVisible(true);
                        ghost.getImage().setLayoutX(posX);
                        ghost.getImage().setLayoutY(posY);
                        break;
                    case 'P':
                        double pacmanWidth = cellWidth;
                        double pacmanHeight = cellHeight;

                        pacman.getImage().setFitWidth(pacmanWidth);
                        pacman.getImage().setFitHeight(pacmanHeight);

                        double paneWidth = Toolkit.getDefaultToolkit().getScreenSize().width; //gamePane.getWidth();

                        posX = posX + ((paneWidth - canvasWidth) / 2);

                        //posY = posY + (cellHeight / 2) - (pacmanHeight / 2);

                        pacman.setPosX(x);
                        pacman.setPosY(y);

                        isMouseOpen = !isMouseOpen; // Alterne entre ouvert et fermé
                        if (isMouseOpen) {
                            pacman.getImage().setImage(pacmanOpen);  // Pacman ouvert
                        } else {
                            pacman.getImage().setImage(pacmanClosed);  // Pacman fermé
                        }

                        char directionPacman = pacman.getDirection();

                        // Met à jour l'orientation de Pacman en fonction de la direction
                        switch (directionPacman) {
                            case 'r':  // Droite
                                pacman.getImage().setRotate(0);        // Rotation à 0° (regarde vers la droite)
                                pacman.getImage().setScaleX(1);        // Normalement orienté
                                break;
                            case 'l':  // Gauche
                                pacman.getImage().setRotate(360);      // Rotation à 180° (regarde vers la gauche)
                                pacman.getImage().setScaleX(-1);       // Inverser horizontalement
                                break;
                            case 'd':  // Bas
                                pacman.getImage().setRotate(90);       // Rotation à 90° (regarde vers le bas)
                                pacman.getImage().setScaleX(1);        // Normalement orienté
                                break;
                            case 'u':  // Haut
                                pacman.getImage().setRotate(270);      // Rotation à 270° (regarde vers le haut)
                                pacman.getImage().setScaleX(1);        // Normalement orienté
                                break;
                        }

                        pacman.getImage().setVisible(true);
                        pacman.getImage().setLayoutX(posX);
                        pacman.getImage().setLayoutY(posY);

                        //gc.drawImage(pacmanImage.getImage(), posX, posY, pacmanWidth, pacmanHeight);
                }
            }
        }

        double screenX = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double canvasOffsetWidth = screenX - ((canvasWidth + screenX) / 2);
        canvas.setLayoutX(canvasOffsetWidth);

        gamePane.getChildren().clear();
        gamePane.getChildren().add(canvas);

        gamePane.getChildren().add(pacman.getImage());
        gamePane.getChildren().add(ghost.getImage());
    }

    private void startMoving() {
        if (movementTimer != null) {
            movementTimer.stop();
        }

        movementTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double elapsedTime = now / 1e9;  // Convertir le temps en secondes

                // Vérifier si suffisamment de temps s'est écoulé depuis le dernier mouvement
                if (elapsedTime - lastMoveTime >= PACMAN_SPEED) {
                    movePacman();
                    moveGhost();
                    lastMoveTime = elapsedTime;  // Mettre à jour le temps du dernier mouvement
                }
            }
        };
        movementTimer.start();
    }

    private void moveGhost() {
        int newX = ghost.getPosX();
        int newY = ghost.getPosY();

        //System.out.println(map.getTile(newX, newY));

        Random rand = new Random();

        char[] directions = {'U', 'D', 'R', 'L'};

        this.ghostDirection = directions[rand.nextInt(directions.length)];

        switch (this.ghostDirection) {
            case 'U':
                newY -= 1;
                break;
            case 'D':
                newY += 1;
                break;
            case 'L':
                newX -= 1;
                break;
            case 'R':
                newX += 1;
                break;
        }

        //System.out.println("Nouveau : " + map.getTile(newX, newY) + "\n\n");

        if (map.isWalkable(newY, newX)) {

            if (this.ghostDirection == 'L' || this.ghostDirection == 'U') {
                System.out.println("zeeeeee");
            }

            map.updateTile(ghost.getPosY(), ghost.getPosX(), map.getTile(ghost.getPosY(), ghost.getPosX()) != 'b' ? map.getTile(ghost.getPosY(), ghost.getPosX()) : 'S');

            if (map.getTile(newY, newX) == 'b') {

            }

            ghost.setPosX(newX);
            ghost.setPosY(newY);

            System.out.println(ghost.getPosX() + " / " + ghost.getPosY());

            map.updateTile(newY, newX, 'b');

            System.out.println("============");
            map.displayMap();
            System.out.println("============");

            updateMap();
        } else {
            this.ghostDirection = directions[rand.nextInt(directions.length)];
       }
    }

    private void movePacman() {
        int newX = pacman.getPosX();
        int newY = pacman.getPosY();

        // Calculer la nouvelle position en fonction de la direction actuelle
        switch (currentDirection) {
            case UP:
                newY -= 1;
                pacman.setDirection('u');
                break;
            case DOWN:
                newY += 1;
                pacman.setDirection('d');
                break;
            case LEFT:
                newX -= 1;
                pacman.setDirection('l');
                break;
            case RIGHT:
                newX += 1;
                pacman.setDirection('r');
                break;
        }

        if (map.isWalkable(newY, newX)) {
            map.updateTile(pacman.getPosY(), pacman.getPosX(), 'S');
            // Si Pacman peut se déplacer, met à jour sa position
            pacman.setPosX(newX);
            pacman.setPosY(newY);

            // Met à jour la carte
            pacman.collectPellet(map.getTile(newY, newX));
            map.updateTile(newY, newX, 'P');  // 'P' pour Pacman
            updateMap();  // Rafraîchir l'affichage
        } else {
            movementTimer.stop();
        }
    }

    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        if (currentDirection == null || code != currentDirection) {
            currentDirection = code;

            startMoving();
          
        /*switch (code) {
            case UP -> newY -= 1;
            case DOWN -> newY += 1;
            case LEFT -> newX -= 1;
            case RIGHT -> newX += 1;
        }*/

            event.consume();  // Empêche la propagation de l'événement
            updateMap();
        }
    }
}