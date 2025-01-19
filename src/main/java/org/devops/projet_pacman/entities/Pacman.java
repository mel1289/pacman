package org.devops.projet_pacman.entities;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Pacman {

    private int posX;
    private int posY;
    private char direction; // 'r': droite / 'l': gauche / 'u': haut / 'd': bas

    private int score;
    private boolean isPoweredUp;
    private int powerUpDuration;

    private ImageView image;

    public Pacman(int posX, int posY, String pathPacmanSkin) {
        this.posX = posX;
        this.posY = posY;
        this.direction = 'r';
        this.score = 0;
        this.isPoweredUp = false;
        this.powerUpDuration = 0;
        this.image = new ImageView(new Image(getClass().getResourceAsStream(pathPacmanSkin)));
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }

    public int getScore() {
        return score;
    }

    public void addScore (int score) {
        this.score = getScore() + score;
    }

    public boolean isPoweredUp() {
        return isPoweredUp;
    }

    public char getDirection() {
        return direction;
    }

    public void collectPellet(char tile) {
        if (tile == 'o') {
            score += 10;
        } else if (tile == 'O') {
            score += 50;
            activatePowerUp(60); // Power-up dure 10 tours
        }
    }

    public void activatePowerUp(int duration) {
        isPoweredUp = true;
        powerUpDuration = duration;
    }

    public void decrementPowerUp() {
        if (isPoweredUp) {
            powerUpDuration--;
            if (powerUpDuration <= 0) {
                isPoweredUp = false;
            }
        }
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
}
