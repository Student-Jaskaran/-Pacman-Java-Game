package PACMAN;

import javax.swing.*;
import java.util.List;

public class APPLICATION {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Launch Pac-Man Game Frame
            JFrame gameFrame = new JFrame("PacMan Game");
            PacMan gamePanel = new PacMan();
            gameFrame.add(gamePanel);
            gameFrame.pack();
            gameFrame.setLocation(100, 100);
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setVisible(true);

            // Launch High Score Frame
            DatabaseHelper db = new DatabaseHelper();
            List<Integer> scores = db.getHighScores();

            JFrame scoreFrame = new JFrame("High Scores");
            scoreFrame.add(new HighScorePanel(scores));
            scoreFrame.pack();
            scoreFrame.setLocation(750, 100); // Adjust location
            scoreFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            scoreFrame.setVisible(true);
        });
    }
}
