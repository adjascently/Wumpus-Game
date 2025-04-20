package controller;

import model.*;
import view.*;

import javax.swing.*;
import java.util.*;

public class GameController {
    private GameState gameState;
    private Maze maze;
    private Player player;
    private final GamePanel gamePanel;

    public GameController() {
        do {
            maze = new Maze(10, 10);
            player = new Player(maze.getCaves().get(0), 3);
            player.getCurrentCave().setPlayer(true);
            maze.getCaves().get(0).setPlayer(true);
        } while (!isWinnableMaze());

        this.gameState = GameState.RUNNING;
        gamePanel = new GamePanel(this);
    }

    public GameController(boolean skipWinnableCheck) {
        if (skipWinnableCheck) {
            maze = new Maze(5, 5);
            player = new Player(maze.getCaves().get(0), 3);
            player.getCurrentCave().setPlayer(true);
            maze.getCaves().get(0).setPlayer(true);
        } else {
            do {
                maze = new Maze(10, 10);
                player = new Player(maze.getCaves().get(0), 3);
                player.getCurrentCave().setPlayer(true);
                maze.getCaves().get(0).setPlayer(true);
            } while (!isWinnableMaze());
        }

        this.gameState = GameState.RUNNING;
        gamePanel = new GamePanel(this);
    }

    public Maze getMaze() { return maze; }

    public GameState getGameState() { return gameState; }

    public void setGameState(GameState gameState) { this.gameState = gameState; }

    public Player getPlayer() { return player; }

    public GamePanel getGamePanel() { return gamePanel; }

    public boolean movePlayer(int row, int col) {
        if (gameState != GameState.RUNNING) return false;

        if (maze.isValidPosition(row, col)) {
            for (Cave cave : maze.getCaves()) {
                if (cave.getRow() == row && cave.getCol() == col) {
                    player.getCurrentCave().setPlayer(false);
                    player.setCurrentCave(cave);
                    cave.setPlayer(true);

                    // 🧟 Death hazards (handled visually by GamePanel after state update)
                    if (cave.hasWumpus()) {
                        gameState = GameState.LOST;
                        JOptionPane.showMessageDialog(gamePanel, "You were eaten by the Wumpus!");
                        return true;
                    } else if (cave.hasPit()) {
                        gameState = GameState.LOST;
                        JOptionPane.showMessageDialog(gamePanel, "You fell into a bottomless pit!");
                        return true;
                    }

                    // 🦇 Safe bat teleport
                    else if (cave.hasBats()) {
                        JOptionPane.showMessageDialog(gamePanel, "Super bats carried you away!");

                        List<Cave> safeCaves = new ArrayList<>();
                        for (Cave c : maze.getCaves()) {
                            if (!c.hasWumpus() && !c.hasPit() && !c.hasBats() && !c.hasPlayer()) {
                                safeCaves.add(c);
                            }
                        }

                        if (!safeCaves.isEmpty()) {
                            Cave randomCave = safeCaves.get(new Random().nextInt(safeCaves.size()));
                            return movePlayer(randomCave.getRow(), randomCave.getCol());  // Recursive move
                        } else {
                            JOptionPane.showMessageDialog(gamePanel, "No safe cave to teleport to!");
                        }
                    }

                    // ⚠️ Nearby hazard warning
                    StringBuilder warning = new StringBuilder();
                    for (Cave neighbor : cave.getAdjacentCaves()) {
                        if (neighbor.hasWumpus()) warning.append("You smell something foul nearby.\n");
                        if (neighbor.hasPit()) warning.append("You feel a cold draft nearby.\n");
                    }

                    if (!warning.isEmpty()) {
                        JOptionPane.showMessageDialog(gamePanel, warning.toString().trim());
                    }

                    return true;
                }
            }
        }
        return false;
    }

    public boolean shootArrow(int row, int col) {
        if (gameState != GameState.RUNNING) return false;

        if (maze.isValidPosition(row, col)) {
            player.shootArrow();
            for (Cave cave : maze.getCaves()) {
                if (cave.getRow() == row && cave.getCol() == col) {
                    if (cave == maze.getWumpusCave()) {
                        gameState = GameState.WON;
                        return true;
                    }
                }
            }

            if (player.getArrows() <= 0) {
                gameState = GameState.LOST;
            }
        }
        return false;
    }

    private boolean isWinnableMaze() {
        Set<Cave> visited = new HashSet<>();
        Queue<Cave> queue = new LinkedList<>();
        Cave start = player.getCurrentCave();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Cave current = queue.poll();
            if (current.hasPit() || current.hasBats()) continue;

            for (Cave neighbor : current.getAdjacentCaves()) {
                if (neighbor.hasWumpus()) return true;
            }

            for (Cave neighbor : current.getAdjacentCaves()) {
                if (!visited.contains(neighbor)
                        && !neighbor.hasPit()
                        && !neighbor.hasBats()
                        && !neighbor.hasWumpus()) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return false;
    }
}
