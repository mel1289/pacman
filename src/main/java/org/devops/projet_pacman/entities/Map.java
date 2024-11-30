package org.devops.projet_pacman.entities;

public class Map {

    private String[] map;
    private int height; // Correspond à rows
    private int width;  // Correspond à cols

    public Map(String[] map) {
        this.map = map;
        this.height = map.length;
        this.width = map[0].length();
    }

    public String[] getMap() {
        return map;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void displayMap() {
        for (String row : map) {
            System.out.println(row);
        }
    }

    public char getTile(int y, int x) {
        if (isValidPosition(y, x)) {
            return map[y].charAt(x);
        }
        throw new IndexOutOfBoundsException("Invalid position.");
    }

    public boolean isWall(int y, int x) {
        return getTile(y, x) == '/';
    }

    public boolean isWalkable(int y, int x) {
        char tile = getTile(y, x);
        System.out.println("Checking tile at y = " + y + ", x = " + x + " -> tile = '" + tile + "'");
        return tile != '/';
    }

    public void updateTile(int y, int x, char newTile) {
        if (isValidPosition(y, x)) {
            StringBuilder sb = new StringBuilder(map[y]);
            sb.setCharAt(x, newTile);
            map[y] = sb.toString();
        } else {
            throw new IndexOutOfBoundsException("Invalid position.");
        }
    }

    public boolean isValidPosition(int y, int x) {
        return y >= 0 && y < height && x >= 0 && x < width;
    }
}
