package org.devops.projet_pacman.entities;

public class Pacman {

    private int posX;
    private int posY;
    private char direction; // 'r': droite / 'l': gauche / 'u': haut / 'd': bas

    private int score;
    private boolean isPoweredUp;
    private int powerUpDuration;

    public Pacman(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.direction = 'r';
        this.score = 0;
        this.isPoweredUp = false;
        this.powerUpDuration = 0;
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

    public int getScore() {
        return score;
    }

    public boolean isPoweredUp() {
        return isPoweredUp;
    }

    public char getDirection() {
        return direction;
    }

    public void move(char direction, Map map) {
        int newY = posY;
        int newX = posX;

        switch (direction) {
            case 'u' -> newY--;
            case 'd' -> newY++;
            case 'l' -> newX--;
            case 'r' -> newX++;
            default -> {
                System.out.println("Invalid direction!");
                return;
            }
        }

        if (map.isValidPosition(newY, newX) && map.isWalkable(newY, newX)) {
            this.direction = direction;
            posX = newX;
            posY = newY;
            char tile = map.getTile(posY, posX);

            collectPellet(tile);
            map.updateTile(posY, posX, 'P');
        }
    }

    public void moveUp(Map map) {
        move('u', map);
    }

    public void moveDown(Map map) {
        move('d', map);
    }

    public void moveLeft(Map map) {
        move('l', map);
    }

    public void moveRight(Map map) {
        move('r', map);
    }

    public void collectPellet(char tile) {
        if (tile == 'o') {
            score += 10;
        } else if (tile == 'O') {
            score += 50;
            activatePowerUp(10); // Power-up dure 10 tours
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

    public boolean checkCollision(int ghostY, int ghostX) {
        return posX == ghostX && posY == ghostY;
    }
}
