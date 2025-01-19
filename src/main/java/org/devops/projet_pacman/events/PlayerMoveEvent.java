package org.devops.projet_pacman.events;

public class PlayerMoveEvent {
    private String code;
    private double x;
    private double y;
    private char direction;

    public PlayerMoveEvent() { }

    public PlayerMoveEvent(String code, double x, double y, char direction) {
        this.code = code;
        this.x = x;
        this.y = y;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public char getDirection() {
        return direction;
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }
}
