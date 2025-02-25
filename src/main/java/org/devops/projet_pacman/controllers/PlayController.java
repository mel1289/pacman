package org.devops.projet_pacman.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.devops.projet_pacman.MenuApplication;
import org.devops.projet_pacman.ScreenManager;
import org.devops.projet_pacman.entities.Ghost;
import org.devops.projet_pacman.entities.Map;
import org.devops.projet_pacman.entities.Pacman;

import java.awt.*;
import java.util.Random;

public class PlayController {

    @FXML
    private Pane gamePane;

    @FXML
    private StackPane btnRetour;

    @FXML
    private Text scoreText;


    private boolean isMouseOpen = true;
    private final Image pacmanOpen = new Image(getClass().getResource("/org/devops/projet_pacman/images/pacman_opened.png").toExternalForm());
    private final Image pacmanClosed = new Image(getClass().getResource("/org/devops/projet_pacman/images/pacman_closed.png").toExternalForm());

    private KeyCode currentDirection = null;
    private AnimationTimer movementTimer = null;

    private Map map;
    private Pacman pacman;

    private Ghost ghost;

    private char ghostDirection = 'R';

    private static final double PACMAN_SPEED = 0.2;
    private double lastMoveTime = 0;

    private MenuApplication menuApplication;

    @FXML
    public void initialize() {
        btnRetour.setOnMouseClicked(e -> ScreenManager.showMainScreen());
        btnRetour.setOnMouseClicked(e -> ScreenManager.showMainScreen());

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

                boolean isPelletInMap = map.containsPelletPosition(x, y);
                boolean isBigPelletInMap = map.containsBigPelletPosition(x, y);

                if (tile == '/') {
                    gc.setFill(Color.BLUE);
                    gc.fillRect(posX, posY, cellWidth, cellHeight);
                } if (tile == 'o' || isPelletInMap) {
                    double dotWidth = cellWidth / 4;
                    double dotHeight = cellHeight / 4;
                    gc.setFill(Color.WHITE);
                    gc.fillOval(posX + (cellWidth / 2) - (dotWidth / 2), posY + (cellHeight / 2) - (dotHeight / 2), dotWidth, dotHeight);
                    map.addPelletPosition(x, y);
                } if (tile == 'O' || isBigPelletInMap) {
                    double bigDotWidth = cellWidth / 2;
                    double bigDotHeight = cellHeight / 2;
                    gc.setFill(Color.WHITE);
                    gc.fillOval(posX + (cellWidth / 2) - (bigDotWidth / 2), posY + (cellHeight / 2) - (bigDotHeight / 2), bigDotWidth, bigDotHeight);
                    map.addBigPelletPosition(x, y);
                } if (tile == 'b') {
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
                } if (tile == 'P') {
                    double pacmanWidth = cellWidth;
                    double pacmanHeight = cellHeight;

                    pacman.getImage().setFitWidth(pacmanWidth);
                    pacman.getImage().setFitHeight(pacmanHeight);

                    double paneWidth = Toolkit.getDefaultToolkit().getScreenSize().width; //gamePane.getWidth();

                    posX = posX + ((paneWidth - canvasWidth) / 2);

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
                    if (directionPacman == 'r') {  // Droite
                        pacman.getImage().setRotate(0);        // Rotation à 0° (regarde vers la droite)
                        pacman.getImage().setScaleX(1);        // Normalement orienté
                    } else if (directionPacman == 'l') {  // Gauche
                        pacman.getImage().setRotate(360);      // Rotation à 180° (regarde vers la gauche)
                        pacman.getImage().setScaleX(-1);       // Inverser horizontalement
                    } else if (directionPacman == 'd') {  // Bas
                        pacman.getImage().setRotate(90);       // Rotation à 90° (regarde vers le bas)
                        pacman.getImage().setScaleX(1);        // Normalement orienté
                    } else if (directionPacman == 'u') {  // Haut
                        pacman.getImage().setRotate(270);      // Rotation à 270° (regarde vers le haut)
                        pacman.getImage().setScaleX(1);        // Normalement orienté
                    }

                    pacman.getImage().setVisible(true);
                    pacman.getImage().setLayoutX(posX);
                    pacman.getImage().setLayoutY(posY);
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

                    // Regarde si la parti est perdu, si oui, on arrete le jeu et on affiche une nouvelle fenetre pour rejouer ou aller au menu principal
                    int pos_x_pacman = pacman.getPosX();
                    int pos_y_pacman = pacman.getPosY();

                    int pos_x_ghost = ghost.getPosX();
                    int pos_y_ghost = ghost.getPosY();

                    if (pos_x_pacman == pos_x_ghost && pos_y_pacman == pos_y_ghost && !pacman.isPoweredUp()){
                        System.out.println("perdu");
                        movementTimer.stop();
                        ScreenManager.showGameOver();
                        return;
                    }

                    if (pos_x_pacman == pos_x_ghost && pos_y_pacman == pos_y_ghost && pacman.isPoweredUp()){
                        ghost.setPosX(10);
                        ghost.setPosY(8);
                        pacman.activatePowerUp(0);
                        pacman.addScore(100);
                        ghost.setImage("/org/devops/projet_pacman/images/ghost.png");
                    }

                    if (pacman.isPoweredUp()) {
                        ghost.setImage("/org/devops/projet_pacman/images/ghost_scared.png");
                    } else {
                        ghost.setImage("/org/devops/projet_pacman/images/ghost.png");
                    }

                    movePacman();
                    moveGhost();
                    updateMap();
                    lastMoveTime = elapsedTime;  // Mettre à jour le temps du dernier mouvement
                }
            }
        };
        movementTimer.start();
    }

    private boolean allPelletsEaten() {
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                char tile = map.getTile(y, x);
                if (tile == 'o' || tile == 'O') {
                    return false;
                }
            }
        }
        return true;
    }

    private void moveGhost() {
        Random rand = new Random();
        char[] directions = {'U', 'D', 'R', 'L'};

        // Répéter tant que la case suivante n'est pas walkable
        boolean moved = false;
        while (!moved) {
            int newX = ghost.getPosX();
            int newY = ghost.getPosY();
            this.ghostDirection = directions[rand.nextInt(directions.length)];

            // Regarde si la parti est perdu, si oui, on arrete le jeu et on affiche une nouvelle fenetre pour rejouer ou aller au menu principal
            int pos_x_pacman = pacman.getPosX();
            int pos_y_pacman = pacman.getPosY();

            int pos_x_ghost = ghost.getPosX();
            int pos_y_ghost = ghost.getPosY();

            if (pos_x_pacman == pos_x_ghost && pos_y_pacman == pos_y_ghost && !pacman.isPoweredUp()){
                System.out.println("perdu");
                movementTimer.stop();
                ScreenManager.showGameOver();
                return;
            }

            if (pos_x_pacman == pos_x_ghost && pos_y_pacman == pos_y_ghost && pacman.isPoweredUp()){
                ghost.setPosX(10);
                ghost.setPosY(8);
                pacman.activatePowerUp(0);
                pacman.addScore(100);
                ghost.setImage("/org/devops/projet_pacman/images/ghost.png");
            }

            // Calculer la nouvelle position en fonction de la direction
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

            // Vérifier si la case est walkable
            if (map.isWalkable(newY, newX)) {
                // Si oui, on marque le mouvement comme e ffectué
                moved = true;

                // Mise à jour de l'ancienne case du fantôme
                map.updateTile(ghost.getPosY(), ghost.getPosX(), map.getTile(ghost.getPosY(), ghost.getPosX()) != 'b' ? map.getTile(ghost.getPosY(), ghost.getPosX()) : 'S');

                // Déplacement du fantôme
                ghost.setPosX(newX);
                ghost.setPosY(newY);

                // System.out.println(ghost.getPosX() + " / " + ghost.getPosY());

                // Mettre à jour la nouvelle case du fantôme avec 'b'
                map.updateTile(newY, newX, 'b');

                // System.out.println("============");
                // map.displayMap();
                // System.out.println("============");
            }
        }
    }

    private void movePacman() {
        int newX = pacman.getPosX();
        int newY = pacman.getPosY();

        // Regarde si la parti est perdu, si oui, on arrete le jeu et on affiche une nouvelle fenetre pour rejouer ou aller au menu principal
        int pos_x_pacman = pacman.getPosX();
        int pos_y_pacman = pacman.getPosY();

        int pos_x_ghost = ghost.getPosX();
        int pos_y_ghost = ghost.getPosY();

        if (pos_x_pacman == pos_x_ghost && pos_y_pacman == pos_y_ghost && !pacman.isPoweredUp()){
            System.out.println("perdu");
            movementTimer.stop();
            ScreenManager.showGameOver();
            return;
        }

        if (pos_x_pacman == pos_x_ghost && pos_y_pacman == pos_y_ghost && pacman.isPoweredUp()){
            ghost.setPosX(10);
            ghost.setPosY(8);
            pacman.activatePowerUp(0);
            pacman.addScore(100);
            ghost.setImage("/org/devops/projet_pacman/images/ghost.png");
        }

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

        if (pacman.isPoweredUp()) {
            pacman.decrementPowerUp();
        }

        if (map.isWalkable(newY, newX)) {
            map.updateTile(pacman.getPosY(), pacman.getPosX(), 'S');
            // Si Pacman peut se déplacer, met à jour sa position
            pacman.setPosX(newX);
            pacman.setPosY(newY);

            map.removePelletPosition(newX, newY);
            map.removeBigPelletPosition(newX, newY);

            pacman.collectPellet(map.getTile(newY, newX));
            map.updateTile(newY, newX, 'P');

            if (allPelletsEaten()) {
                movementTimer.stop();
                ScreenManager.showWin();
            }

        } else {
            // movementTimer.stop(); // Si le pacman est bloqué, tout le jeu bloque...
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
        }
    }
}