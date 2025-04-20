package view;

import controller.GameController;
import model.Maze;
import model.Cave;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static javax.swing.SwingUtilities.*;

public class GamePanel extends JPanel {
    private final GameController gameController;
    private final Maze maze;
    private static final int CELL_SIZE = 80;
    private BufferedImage mazeCache;

    private boolean revealHazards = false;  // ✅ NEW: toggle to show hazards after game over

    public GamePanel(GameController gameController) {
        this.gameController = gameController;
        this.maze = gameController.getMaze();

        setPreferredSize(new Dimension(maze.getWidth() * CELL_SIZE, maze.getHeight() * CELL_SIZE));
        cacheMazeGrid();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }

    private void handleMouseClick(MouseEvent e) {
        int col = e.getX() / CELL_SIZE;
        int row = e.getY() / CELL_SIZE;

        if (!maze.isValidPosition(row, col)) return;

        int button = e.getButton();

        if (button == MouseEvent.BUTTON1) {
            if (gameController.movePlayer(row, col)) {
                repaint();
                checkGameEnd();
            }
        } else if (button == MouseEvent.BUTTON3) {
            if (gameController.shootArrow(row, col)) {
                repaint();
                JOptionPane.showMessageDialog(this, "You shot the Wumpus! You win!");
            } else {
                repaint();
                JOptionPane.showMessageDialog(this, "Arrow missed!");
            }
            checkGameEnd();
        }
    }

    private void checkGameEnd() {
        if (gameController.getGameState() == controller.GameState.WON) {
            JOptionPane.showMessageDialog(this, "Congratulations! You defeated the Wumpus!");
            System.exit(0);
        } else if (gameController.getGameState() == controller.GameState.LOST || gameController.getPlayer().getArrows() <= 0) {
            Maze maze = gameController.getMaze();
            StringBuilder reveal = new StringBuilder("Game Over! You lost!\n\nHere were the hazard locations:\n");

            for (Cave cave : maze.getCaves()) {
                if (cave.hasWumpus()) {
                    reveal.append("🐗 Wumpus at: (" + cave.getRow() + ", " + cave.getCol() + ")\n");
                }
                if (cave.hasPit()) {
                    reveal.append("🕳️ Pit at: (" + cave.getRow() + ", " + cave.getCol() + ")\n");
                }
                if (cave.hasBats()) {
                    reveal.append("🦇 Bats at: (" + cave.getRow() + ", " + cave.getCol() + ")\n");
                }
            }

            revealHazards = true;     // ✅ Turn on visual hazard reveal
            repaint();                // ✅ Redraw with hazard icons

            JOptionPane.showMessageDialog(this, reveal.toString());

            try {
                Thread.sleep(2000);   // Pause for 2 seconds so user can see them
            } catch (InterruptedException ignored) {}

            System.exit(0);
        }
    }

    private void cacheMazeGrid() {
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;

        if (width <= 0 || height <= 0) return;

        mazeCache = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = mazeCache.getGraphics();

        for (int row = 0; row < maze.getHeight(); row++) {
            for (int col = 0; col < maze.getWidth(); col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;

                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                g.setColor(Color.BLACK);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
        g.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mazeCache != null) {
            g.drawImage(mazeCache, 0, 0, this);
        }
        drawEntities(g);
        drawPlayerInfo(g);
    }

    private void drawEntities(Graphics g) {
        for (Cave cave : maze.getCaves()) {
            int x = cave.getCol() * CELL_SIZE + CELL_SIZE / 4;
            int y = cave.getRow() * CELL_SIZE + CELL_SIZE / 4;

            if (cave.hasPlayer()) {
                g.setColor(Color.BLUE);
                g.fillOval(x, y, CELL_SIZE / 2, CELL_SIZE / 2);
            }

            // ✅ Show hazards only after game over
            if (revealHazards) {
                if (cave.hasWumpus()) {
                    g.setColor(Color.RED);
                    g.fillOval(x, y, CELL_SIZE / 2, CELL_SIZE / 2);
                } else if (cave.hasPit()) {
                    g.setColor(Color.BLACK);
                    g.fillRect(x, y, CELL_SIZE / 2, CELL_SIZE / 2);
                } else if (cave.hasBats()) {
                    g.setColor(Color.MAGENTA);
                    g.fillOval(x + CELL_SIZE / 8, y + CELL_SIZE / 8, CELL_SIZE / 4, CELL_SIZE / 4);
                }
            }
        }
    }

    private void drawPlayerInfo(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Arrows: " + gameController.getPlayer().getArrows(), 10, 20);
    }
}
