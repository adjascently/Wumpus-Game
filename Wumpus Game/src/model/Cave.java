package model;

import java.util.ArrayList;
import java.util.List;

public class Cave {
    private final int id;
    private int row;  // Removed 'final'
    private int col;  // Removed 'final'
    private final List<Cave> adjacentCaves;
    private boolean hasPit, hasBats, hasWumpus, hasPlayer;

    public Cave(int id, int row, int col) {  // Added 'row' and 'col' parameters
        this.id = id;
        this.row = row;  // Now initialized with the constructor parameters
        this.col = col;  // Now initialized with the constructor parameters
        this.adjacentCaves = new ArrayList<>();
        this.hasPlayer = false;
    }

    public int getId() { return id; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public List<Cave> getAdjacentCaves() { return adjacentCaves; }

    public boolean hasPit() { return hasPit; }
    public boolean hasBats() { return hasBats; }
    public boolean hasWumpus() { return hasWumpus; }
    public boolean hasPlayer() { return hasPlayer; }

    public void setPit(boolean hasPit) { this.hasPit = hasPit; }
    public void setBats(boolean hasBats) { this.hasBats = hasBats; }
    public void setWumpus(boolean hasWumpus) { this.hasWumpus = hasWumpus; }
    public void setPlayer(boolean hasPlayer) { this.hasPlayer = hasPlayer; }

    public void addAdjacentCave(Cave cave) {
        if (!adjacentCaves.contains(cave)) {
            adjacentCaves.add(cave);
            cave.getAdjacentCaves().add(this);
        }
    }
}
