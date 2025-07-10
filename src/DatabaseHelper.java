package PACMAN;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    String url = "jdbc:mysql://localhost:3306/pacman?useSSL=false";
    String USER = "root";
    String PASS = "JK12@jaskaran"; // change if needed

    public void saveScore(String playerName, int score) {
        String sql = "INSERT INTO high_scores (name, score) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.setInt(2, score);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getHighScores() {
        List<Integer> scores = new ArrayList<>();
        String sql = "SELECT score FROM high_scores ORDER BY score DESC LIMIT 5";
        try (Connection conn = DriverManager.getConnection(url, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                scores.add(rs.getInt("score"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public void displayHighScores() {
        List<Integer> scores = getHighScores();
        System.out.println("Top 5 High Scores:");
        for (int i = 0; i < scores.size(); i++) {
            System.out.println((i + 1) + ": " + scores.get(i));
        }
    }
}
