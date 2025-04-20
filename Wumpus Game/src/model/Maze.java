package model;

import java.util.*;

public class Maze {
    private final List<Cave> caves;
    private Player player;
    private Cave wumpusCave;

    private final int width;
    private final int height;

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        caves = new ArrayList<>();

        for (int i = 0; i < width * height; i++) {
            int row = i / width;
            int col = i % width;
            caves.add(new Cave(i, row, col));
        }

        generateMazeConnections();  // ✅ Establish graph structure
        placeHazards();             // ✅ Add Wumpus, pits, bats
    }

    // Creates random graph connections (each cave connected to at least 3 others)
    private void generateMazeConnections() {
        Random rand = new Random();
        for (Cave cave : caves) {
            while (cave.getAdjacentCaves().size() < 3) {
                Cave randomCave = caves.get(rand.nextInt(caves.size()));
                if (randomCave != cave && !cave.getAdjacentCaves().contains(randomCave)) {
                    cave.addAdjacentCave(randomCave);
                }
            }
        }
    }

    // Randomly assign 1 Wumpus, 2 pits, 2 bats — avoiding the player's starting cave
    private void placeHazards() {
        Random rand = new Random();
        List<Cave> safeCaves = new ArrayList<>(caves);
        safeCaves.remove(0);  // Don't place hazards in the starting cave

        Collections.shuffle(safeCaves, rand);

        if (safeCaves.size() >= 5) {
            safeCaves.get(0).setWumpus(true);
            setWumpusCave(safeCaves.get(0));

            safeCaves.get(1).setPit(true);
            safeCaves.get(2).setPit(true);

            safeCaves.get(3).setBats(true);
            safeCaves.get(4).setBats(true);
        }
    }

    public List<Cave> getCaves() {
        return caves;
    }

    public void setWumpusCave(Cave cave) {
        this.wumpusCave = cave;
    }

    public Cave getWumpusCave() {
        return wumpusCave;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }
}
