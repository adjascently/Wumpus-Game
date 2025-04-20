package model;

public class GameState {
    private final Maze maze;
    private final Player player;
    private GameStatus status;  // Current game status (RUNNING, WON, LOST)
    private String message;

    // Constructor to initialize the game state
    public GameState(Maze maze, Player player) {
        this.maze = maze;
        this.player = player;
        this.status = GameStatus.RUNNING;  // Game starts in the RUNNING state
        this.message = "";
    }

    // Enum to represent the game's status
    public enum GameStatus {
        RUNNING, WON, LOST
    }

    // Method to move the player
    public boolean movePlayer(int row, int col) {
        if (status != GameStatus.RUNNING) return false;  // Game over, can't move

        if (!maze.isValidPosition(row, col)) {
            message = "Invalid move!";
            return false;  // Invalid move
        }

        Cave targetCave = maze.getCaves().get(row * maze.getWidth() + col); // Get cave at the specified position
        player.setCurrentCave(targetCave);  // Move player to the target cave
        checkPlayerPosition();  // Check if the move triggers any game-ending conditions

        return true;  // Successful move
    }

    // Method to shoot an arrow at a specified cave
    public boolean shootArrow(int row, int col) {
        if (status != GameStatus.RUNNING) return false;  // Game over, can't shoot

        if (!maze.isValidPosition(row, col)) {
            message = "Invalid shot!";
            return false;  // Invalid shot
        }

        Cave targetCave = maze.getCaves().get(row * maze.getWidth() + col); // Get cave at the specified position
        if (targetCave == player.getCurrentCave()) {
            message = "Can't shoot in the current cave!";
            return false;  // Can't shoot in the current cave
        }

        // Handle shooting arrow at the Wumpus
        if (targetCave == maze.getWumpusCave()) {
            status = GameStatus.WON;
            message = "You shot the Wumpus! You win!";
            return true;  // Arrow hit the Wumpus
        } else {
            message = "Arrow missed!";
            return false;  // Arrow missed
        }
    }

    // Check the player's current position for any hazards (Wumpus, Pit, or Bats)
    private void checkPlayerPosition() {
        Cave currentCave = player.getCurrentCave();
        if (currentCave.hasWumpus()) {
            status = GameStatus.LOST;
            message = "You encountered the Wumpus! Game Over!";
        } else if (currentCave.hasPit()) {
            status = GameStatus.LOST;
            message = "You fell into a pit! Game Over!";
        } else if (currentCave.hasBats()) {
            message = "Bats carried you away!";
            // Bats move the player to another random cave
            Cave randomCave = maze.getCaves().get((int) (Math.random() * maze.getCaves().size()));
            player.setCurrentCave(randomCave);
        }
    }

    // Getter for the game status
    public GameStatus getStatus() {
        return status;
    }

    // Getter for the message
    public String getMessage() {
        return message;
    }

    // Getter for the player
    public Player getPlayer() {
        return player;
    }

    // Getter for the maze
    public Maze getMaze() {
        return maze;
    }

    // Check if the game is over
    public boolean isGameOver() {
        return status != GameStatus.RUNNING;
    }
}
