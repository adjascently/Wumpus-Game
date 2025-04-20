package test;

import controller.GameController;
import controller.GameState;
import model.*;
import view.GameFrame;
import view.GamePanel;
import app.Main;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.*;

public class GameTests {
    private Maze maze;
    private Player player;
    private Cave startCave, adjacentCave, wumpusCave, pitCave, batCave;
    private model.GameState gameState;

    @Before
    public void setUp() {
        maze = new Maze(5, 5);
        startCave = maze.getCaves().get(0);
        player = new Player(startCave, 3);
        gameState = new model.GameState(maze, player);

        adjacentCave = maze.getCaves().get(1);
        wumpusCave = maze.getCaves().get(2);
        pitCave = maze.getCaves().get(3);
        batCave = maze.getCaves().get(4);

        startCave.addAdjacentCave(adjacentCave);
        adjacentCave.addAdjacentCave(wumpusCave);
        startCave.addAdjacentCave(pitCave);
        startCave.addAdjacentCave(batCave);

        wumpusCave.setWumpus(true);
        pitCave.setPit(true);
        batCave.setBats(true);

        maze.setWumpusCave(wumpusCave);
    }

    /*** GameState Tests ***/

    // Verifies that the initial game status is RUNNING
    @Test
    public void testInitialGameStatusRunning() {
        assertEquals(model.GameState.GameStatus.RUNNING, gameState.getStatus());
    }

    // Moves the player to an adjacent empty cave
    @Test
    public void testMoveToEmptyCave() {
        boolean moved = gameState.movePlayer(adjacentCave.getRow(), adjacentCave.getCol());
        assertTrue(moved);
        assertEquals(adjacentCave, player.getCurrentCave());
        assertEquals(model.GameState.GameStatus.RUNNING, gameState.getStatus());
    }

    // Tests that entering a cave with the Wumpus ends the game
    @Test
    public void testEncounterWumpus() {
        player.setCurrentCave(wumpusCave);
        gameState.movePlayer(wumpusCave.getRow(), wumpusCave.getCol());
        assertEquals(model.GameState.GameStatus.LOST, gameState.getStatus());
        assertTrue(gameState.getMessage().contains("Wumpus"));
    }

    // Tests that falling into a pit ends the game
    @Test
    public void testFallIntoPit() {
        gameState.movePlayer(pitCave.getRow(), pitCave.getCol());
        assertEquals(model.GameState.GameStatus.LOST, gameState.getStatus());
        assertTrue(gameState.getMessage().contains("pit"));
    }

    // Tests that bats carry the player to a random cave and game continues
    @Test
    public void testBatsCarryAwayPlayer() {
        gameState.movePlayer(batCave.getRow(), batCave.getCol());
        assertEquals(model.GameState.GameStatus.RUNNING, gameState.getStatus());
        assertNotEquals(batCave, player.getCurrentCave());
        assertTrue(gameState.getMessage().contains("Bats"));
    }

    // Tests shooting an arrow into the Wumpus cave results in win
    @Test
    public void testShootArrowHitsWumpus() {
        boolean result = gameState.shootArrow(wumpusCave.getRow(), wumpusCave.getCol());
        assertTrue(result);
        assertEquals(model.GameState.GameStatus.WON, gameState.getStatus());
        assertTrue(gameState.getMessage().contains("win"));
    }

    // Tests that shooting an arrow at the wrong cave does not win
    @Test
    public void testShootArrowMisses() {
        boolean result = gameState.shootArrow(adjacentCave.getRow(), adjacentCave.getCol());
        assertFalse(result);
        assertEquals(model.GameState.GameStatus.RUNNING, gameState.getStatus());
        assertTrue(gameState.getMessage().contains("missed"));
    }

    // Tests that you cannot shoot an arrow in your own cave
    @Test
    public void testShootArrowFromSameCaveFails() {
        boolean result = gameState.shootArrow(startCave.getRow(), startCave.getCol());
        assertFalse(result);
        assertTrue(gameState.getMessage().contains("current cave"));
    }

    // Tests that invalid move (off-grid) is rejected
    @Test
    public void testInvalidMove() {
        boolean result = gameState.movePlayer(-1, -1);
        assertFalse(result);
        assertEquals("Invalid move!", gameState.getMessage());
    }

    // Tests that invalid arrow shot is rejected
    @Test
    public void testInvalidShot() {
        boolean result = gameState.shootArrow(-1, -1);
        assertFalse(result);
        assertEquals("Invalid shot!", gameState.getMessage());
    }

    // Tests that shooting an arrow reduces arrow count
    @Test
    public void testPlayerArrowsDecrease() {
        int before = player.getArrows();
        player.shootArrow();
        assertEquals(before - 1, player.getArrows());
    }

    // Tests Maze boundary checks
    @Test
    public void testMazeBoundaries() {
        assertTrue(maze.isValidPosition(0, 0));
        assertFalse(maze.isValidPosition(-1, 0));
        assertFalse(maze.isValidPosition(maze.getHeight(), 0));
    }

    /*** Controller Tests  ***/

// Tests basic movement via controller
    @Test
    public void testGameControllerMovement() {
        GameController controller = new GameController(true);
        Maze m = controller.getMaze();
        Cave target = m.getCaves().get(1);
        boolean moved = controller.movePlayer(target.getRow(), target.getCol());
        assertTrue(moved);
    }

    // Tests shooting the Wumpus via controller
    @Test
    public void testGameControllerShooting() {
        GameController controller = new GameController(true);
        Maze m = controller.getMaze();
        Cave wumpusCave = m.getCaves().get(5);
        m.setWumpusCave(wumpusCave);

        boolean result = controller.shootArrow(wumpusCave.getRow(), wumpusCave.getCol());
        assertTrue(result);
        assertEquals(GameState.WON, controller.getGameState());
    }

    // Tests setting game state via controller
    @Test
    public void testGameStateSwitching() {
        GameController controller = new GameController(true);
        controller.setGameState(GameState.LOST);
        assertEquals(GameState.LOST, controller.getGameState());
    }

    // Tests movePlayer with invalid input
    @Test
    public void testGameControllerMoveToInvalidPosition() {
        GameController controller = new GameController(true);
        boolean result = controller.movePlayer(-1, -1);
        assertFalse(result);
    }

    // Tests shootArrow with invalid coordinates
    @Test
    public void testGameControllerShootInvalidPosition() {
        GameController controller = new GameController(true);
        boolean result = controller.shootArrow(-1, -1);
        assertFalse(result);
    }

    // Tests shooting all arrows and losing the game
    @Test
    public void testGameControllerShootArrowWithNoArrows() {
        GameController controller = new GameController(true);
        Player p = controller.getPlayer();
        while (p.getArrows() > 0) {
            controller.shootArrow(0, 0);
        }
        assertEquals(GameState.LOST, controller.getGameState());
    }


    /*** View Tests  ***/

// Tests if the GameFrame shows up with valid properties
    @Test
    public void testGameFrameInitialization() {
        GameController controller = new GameController(true);  // Skip winnable check
        GameFrame frame = new GameFrame(controller);
        frame.pack();
        frame.setVisible(true);
        GamePanel panel = controller.getGamePanel();

        assertNotNull(frame);
        assertEquals("Hunt the Wumpus", frame.getTitle());
        assertTrue(panel.getWidth() > 0);
        assertTrue(panel.getHeight() > 0);
    }

    // Verifies GameFrame contains a GamePanel
    @Test
    public void testGameFrameComponents() {
        GameController controller = new GameController(true);  // Skip winnable check
        GameFrame frame = new GameFrame(controller);
        Component[] components = frame.getContentPane().getComponents();
        boolean containsPanel = false;
        for (Component comp : components) {
            if (comp instanceof GamePanel) {
                containsPanel = true;
                break;
            }
        }
        assertTrue("GamePanel should be added to GameFrame", containsPanel);
    }

    /*** Extra Coverage ***/

    // Tests Wumpus class getter
    @Test
    public void testWumpusGetCave() {
        Cave cave = new Cave(99, 9, 9);
        Wumpus wumpus = new Wumpus(cave);
        assertEquals(cave, wumpus.getCave());
    }

    // Tests GameState enum values
    @Test
    public void testControllerGameStateValues() {
        assertEquals("RUNNING", GameState.RUNNING.name());
        assertEquals("WON", GameState.WON.name());
        assertEquals("LOST", GameState.LOST.name());
    }

    // Smoke test for Main class startup
    @Test
    public void testMainMethodRunsWithoutError() {
        try {
            Main.main(new String[]{});
        } catch (Exception e) {
            fail("Main method should not throw exceptions");
        }
    }
}
