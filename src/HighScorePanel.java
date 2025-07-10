package PACMAN;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HighScorePanel extends JPanel {
    private List<Integer> highScores;

    public HighScorePanel(List<Integer> scores) {
        this.highScores = scores;
        setPreferredSize(new Dimension(200, 300));
        setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("High Scores", 30, 30);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (highScores == null || highScores.isEmpty()) {
            g.drawString("No scores yet", 30, 60);
        } else {
            for (int i = 0; i < highScores.size(); i++) {
                g.drawString((i + 1) + ". " + highScores.get(i), 30, 60 + i * 30);
            }
        }
    }
}
