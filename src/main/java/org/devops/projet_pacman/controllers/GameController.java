package org.devops.projet_pacman.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;

public class GameController {

    @FXML
    private Pane gamePane;

    @FXML
    private ImageView pacman;

    private static final double SPEED = 6.0;
    private double directionX = 0;
    private double directionY = 0;

    private AnimationTimer gameLoop;

    private boolean isMouthOpen = true;

    private final Image pacmanOpen = new Image(getClass().getResource("/org/devops/projet_pacman/images/pacman_opened.png").toExternalForm());
    private final Image pacmanClosed = new Image(getClass().getResource("/org/devops/projet_pacman/images/pacman_closed.png").toExternalForm());


    @FXML
    public void initialize() {
        pacman.setImage(pacmanClosed);
        pacman.setLayoutX(gamePane.getPrefWidth() / 2);
        pacman.setLayoutY(gamePane.getPrefHeight() / 2);
        gamePane.setOnKeyPressed(this::handleKeyPressed);
        gamePane.setFocusTraversable(true);
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 200_000_000) {
                    toggleMouth();
                    lastUpdate = now;
                }
                movePacman();
            }
        };
        gameLoop.start();
    }

    private void handleKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();

        switch (code) {
            case UP -> {
                directionX = 0;
                directionY = -SPEED;
                pacman.setRotate(270);
                pacman.setScaleX(1);
            }
            case DOWN -> {
                directionX = 0;
                directionY = SPEED;
                pacman.setRotate(90);
                pacman.setScaleX(1);
            }
            case LEFT -> {
                directionX = -SPEED;
                directionY = 0;
                pacman.setRotate(0);
                pacman.setScaleX(-1);
            }
            case RIGHT -> {
                directionX = SPEED;
                directionY = 0;
                pacman.setRotate(0);
                pacman.setScaleX(1);
            }
            default -> {
            }
        }
    }

    private void movePacman() {
        double newX = pacman.getLayoutX() + directionX;
        double newY = pacman.getLayoutY() + directionY;

        double paneWidth = gamePane.getWidth();
        double paneHeight = gamePane.getHeight();

        if (newX + pacman.getFitWidth() < 0) {
            newX = paneWidth;
        } else if (newX > paneWidth) {
            newX = -pacman.getFitWidth();
        }

        if (newY + pacman.getFitHeight() < 0) {
            newY = paneHeight;
        } else if (newY > paneHeight) {
            newY = -pacman.getFitHeight();
        }

        pacman.setLayoutX(newX);
        pacman.setLayoutY(newY);
    }

    private void toggleMouth() {
        isMouthOpen = !isMouthOpen;
        pacman.setImage(isMouthOpen ? pacmanOpen : pacmanClosed);
    }
}
