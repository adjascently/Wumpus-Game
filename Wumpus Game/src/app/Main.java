package app;

import controller.GameController;
import view.GameFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For fast debugging without checking maze winnability:
            GameController controller = new GameController(true);  // Skip maze regeneration

            // For full version with auto-retry on unwinnable mazes, uncomment below:
            // GameController controller = new GameController();

            GameFrame frame = new GameFrame(controller);
            frame.setVisible(true);
        });
    }
}
