package org.devops.projet_pacman.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent;
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
    private Button btnRetour;

    @FXML
    private ImageView pacmanImage;
    private boolean isMouseOpen = true;
    private final Image pacmanOpen = new Image(getClass().getResource("/org/devops/projet_pacman/images/pacman_opened.png").toExternalForm());
    private final Image pacmanClosed = new Image(getClass().getResource("/org/devops/projet_pacman/images/pacman_closed.png").toExternalForm());

    private KeyCode currentDirection = null;  // Direction actuelle de Pacman
    private AnimationTimer movementTimer = null;  // Timer pour gérer le mouvement continu
    private boolean isMoving = false;  // Indicateur si Pacman est en mouvement

    private Map map;
    private Pacman pacman;

    @FXML
    public void initialize() {
        logo.setImage(new Image(getClass().getResource("/org/devops/projet_pacman/images/logo.png").toExternalForm()));
        logo.setFitHeight(300);
        logo.setFitWidth(1000);


        pacmanImage.setLayoutX(gamePane.getPrefWidth() / 2);
        pacmanImage.setLayoutY(gamePane.getPrefHeight() / 2);

        btnRetour.setOnAction(e -> ScreenManager.showMainScreen());

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
                        posX = posX + (cellWidth / 2) - (pacmanWidth / 2);
                        posY = posY + (cellHeight / 2) - (pacmanHeight / 2);

                        pacman.setPosX(x);
                        pacman.setPosY(y);

                        char directionPacman = pacman.getDirection();
                        System.out.println(directionPacman);

                        isMouseOpen = !isMouseOpen; // Alterne entre ouvert et fermé
                        if (isMouseOpen) {
                            pacmanImage.setImage(pacmanOpen);  // Pacman ouvert
                        } else {
                            pacmanImage.setImage(pacmanClosed);  // Pacman fermé
                        }

                        // Met à jour l'orientation de Pacman en fonction de la direction
                        switch (directionPacman) {
                            case 'r':  // Droite
                                pacmanImage.setRotate(0);        // Rotation à 0° (regarde vers la droite)
                                pacmanImage.setScaleX(1);        // Normalement orienté
                                break;
                            case 'l':  // Gauche
                                pacmanImage.setRotate(360);      // Rotation à 180° (regarde vers la gauche)
                                pacmanImage.setScaleX(-1);       // Inverser horizontalement
                                break;
                            case 'd':  // Bas
                                pacmanImage.setRotate(90);       // Rotation à 90° (regarde vers le bas)
                                pacmanImage.setScaleX(1);        // Normalement orienté
                                break;
                            case 'u':  // Haut
                                pacmanImage.setRotate(270);      // Rotation à 270° (regarde vers le haut)
                                pacmanImage.setScaleX(1);        // Normalement orienté
                                break;
                        }
                        pacmanImage.setVisible(true);
                        pacmanImage.setLayoutX(posX);
                        pacmanImage.setLayoutY(posY);

                        //gc.drawImage(pacmanImage.getImage(), posX, posY, pacmanWidth, pacmanHeight);

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
        KeyCode code = event.getCode();

        // Si Pacman n'est pas déjà en mouvement ou si la direction a changé
        if (currentDirection == null || code != currentDirection) {
            currentDirection = code;

            // Démarrer ou redémarrer le mouvement
            startMoving();
        }

        event.consume();  // Empêche la propagation de l'événement
        updateMap();
    }

    private static final double PACMAN_SPEED = 0.2;  // Ralentir Pacman, déplacer toutes les 0.5 secondes
    private double lastMoveTime = 0;  // Temps depuis le dernier mouvement

    // AnimationTimer pour le mouvement continu
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
                    lastMoveTime = elapsedTime;  // Mettre à jour le temps du dernier mouvement
                }
            }
        };
        movementTimer.start();
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

        // Vérifier si Pacman peut se déplacer dans cette direction (pas de mur)
        if (map.isWalkable(newY, newX)) {
            map.updateTile(pacman.getPosY(), pacman.getPosX(), 'S');
            // Si Pacman peut se déplacer, met à jour sa position
            pacman.setPosX(newX);
            pacman.setPosY(newY);

            // Met à jour la carte
            map.updateTile(newY, newX, 'P');  // 'P' pour Pacman
            updateMap();  // Rafraîchir l'affichage
        } else {
            // Si Pacman rencontre un mur, arrête le mouvement
            movementTimer.stop();
        }
    }

}