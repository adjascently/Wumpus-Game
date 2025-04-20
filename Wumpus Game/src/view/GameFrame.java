package view;
import javax.swing.*;
import controller.GameController;

public class GameFrame extends JFrame {
    public GameFrame(GameController controller) {
        setTitle("Hunt the Wumpus");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(controller.getGamePanel());
    }
}
