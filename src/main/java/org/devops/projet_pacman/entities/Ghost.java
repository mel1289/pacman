package org.devops.projet_pacman.entities;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Ghost {

    private int posX;
    private int posY;

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

    public void setImage(ImageView image) {
        this.image = image;
    }
}
