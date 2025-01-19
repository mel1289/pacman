package org.devops.projet_pacman.entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Ghost {

    private int posX;
    private int posY;
    private char direction; // 'r': droite / 'l': gauche / 'u': haut / 'd': bas

    private ImageView image;

    public Ghost(int posX, int posY, String pathSkin) {
        this.posX = posX;
        this.posY = posY;
        this.image = new ImageView(new Image(getClass().getResourceAsStream(pathSkin)));
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(String path) {
        this.image = new ImageView(new Image(getClass().getResourceAsStream(path)));;
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }

    public char getDirection() {
        return direction;
    }
}
