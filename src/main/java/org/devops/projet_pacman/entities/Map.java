package org.devops.projet_pacman.entities;

import java.util.ArrayList;
import java.util.List;

public class Map {

    private String[] map;
    private int height; // Correspond à rows
    private int width;  // Correspond à cols

    private List<int[]> positionPellet = new ArrayList<int[]>();
    private List<int[]> positionBigPellet = new ArrayList<int[]>();

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

    public void addPelletPosition(int x, int y) {
        // Vérifie si la position (x, y) existe déjà dans la liste
        for (int[] position : positionPellet) {
            if (position[0] == x && position[1] == y) {
                return; // Si la position existe déjà, ne rien faire
            }
        }

        // Si la position n'existe pas, on l'ajoute
        positionPellet.add(new int[]{x, y});
    }


    public void removePelletPosition(int x, int y) {
        for (int i = 0; i < positionPellet.size(); i++) {
            int[] position = positionPellet.get(i);
            if (position[0] == x && position[1] == y) {
                System.out.println("Pellet remove : " + position[0] + " " + position[1]);
                positionPellet.remove(i);
                return;
            }
        }
    }

    public boolean containsPelletPosition(int x, int y) {
        for (int[] position : positionPellet) {
            if (position[0] == x && position[1] == y) {
                return true;
            }
        }
        return false;
    }

    public void addBigPelletPosition(int x, int y) {
        // Vérifie si la position (x, y) existe déjà dans la liste
        for (int[] position : positionBigPellet) {
            if (position[0] == x && position[1] == y) {
                return; // Si la position existe déjà, ne rien faire
            }
        }

        // Si la position n'existe pas, on l'ajoute
        positionBigPellet.add(new int[]{x, y});
    }

    public void removeBigPelletPosition(int x, int y) {
        for (int i = 0; i < positionBigPellet.size(); i++) {
            int[] position = positionBigPellet.get(i);
            if (position[0] == x && position[1] == y) {
                System.out.println("BigPellet remove : " + position[0] + " " + position[1]);
                positionBigPellet.remove(i);
                return;
            }
        }
    }

    public boolean containsBigPelletPosition(int x, int y) {
        for (int[] position : positionBigPellet) {
            if (position[0] == x && position[1] == y) {
                return true;
            }
        }
        return false;
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
        else{
            System.out.println("Invalid position");
        }
        throw new IndexOutOfBoundsException("Invalid position.");
    }

    public boolean isWall(int y, int x) {
        return getTile(y, x) == '/';
    }

    public boolean isWalkable(int y, int x) {
        char tile = getTile(y, x);
        //System.out.println("Checking tile at y = " + y + ", x = " + x + " -> tile = '" + tile + "'");
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
        if (y >= 0 && y < height && x >= 0 && x < width) {
            return true;
        }
        else {
            System.out.println("Invalid position");
            return false;
        }
    }
}
