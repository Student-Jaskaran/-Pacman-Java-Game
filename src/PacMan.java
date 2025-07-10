package PACMAN;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;
import java.util.List;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    private List<Integer> highScores = new ArrayList<>();
    private int currentScore = 0;

    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'U';
        int velocityX = 0, velocityY = 0;

        char nextDirection = ' '; // default = no next direction


        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            switch (this.direction) {
                case 'U' -> {
                    this.velocityX = 0;
                    this.velocityY = -tileSize / 4;
                }
                case 'D' -> {
                    this.velocityX = 0;
                    this.velocityY = tileSize / 4;
                }
                case 'L' -> {
                    this.velocityX = -tileSize / 4;
                    this.velocityY = 0;
                }
                case 'R' -> {
                    this.velocityX = tileSize / 4;
                    this.velocityY = 0;
                }
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private int rowCount = 21, columnCount = 19, tileSize = 32;
    private int boardWidth = columnCount * tileSize, boardHeight = rowCount * tileSize;

    private DatabaseHelper dbHelper = new DatabaseHelper();
    private Image wallImage, blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "X       bpo       X",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"

    };

    HashSet<Block> walls, foods, ghosts;
    Block pacman;
    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0, lives = 3;
    boolean gameOver = false;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();
        for (Block ghost : ghosts) ghost.updateDirection(directions[random.nextInt(4)]);
        dbHelper.displayHighScores();
        gameLoop = new Timer(50, this);
        gameLoop.start();
        loadHighScores();
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char ch = tileMap[r].charAt(c);
                int x = c * tileSize, y = r * tileSize;
                switch (ch) {
                    case 'X' -> walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                    case 'b' -> ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                    case 'o' -> ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                    case 'p' -> ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                    case 'r' -> ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                    case 'P' -> pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                    case ' ' -> foods.add(new Block(null, x + 14, y + 14, 4, 4));
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        for (Block ghost : ghosts) g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        for (Block wall : walls) g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        g.setColor(Color.WHITE);
        for (Block food : foods) g.fillRect(food.x, food.y, food.width, food.height);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) g.drawString("Game Over: " + score, tileSize / 2, tileSize / 2);
        else g.drawString("x" + lives + " Score: " + score, tileSize / 2, tileSize / 2);
        g.drawString("High Scores:", tileSize / 2, tileSize * 2);
        if (highScores.isEmpty()) {
            g.drawString("No high scores yet!", tileSize / 2, tileSize * 3);
        } else {
            for (int i = 0; i < highScores.size(); i++) {
                g.drawString((i + 1) + ". " + highScores.get(i), tileSize / 2, tileSize * (3 + i));
            }
        }
    }

    private void loadHighScores() {
        highScores = dbHelper.getHighScores();
        System.out.println("High Scores Loaded: " + highScores);
    }

    public void move() {
        if (pacman.nextDirection != ' ') {
            pacman.updateDirection(pacman.nextDirection);
            pacman.nextDirection = ' ';
        }

// Now move in current direction
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

// Check for wall collision and revert if needed
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }


        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                }
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public void updateHighScores(int score) {
        highScores.add(score);
        highScores.sort((a, b) -> b - a);
        if (highScores.size() > 5) highScores.remove(highScores.size() - 1);
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            dbHelper.saveScore("Player High Score", score);
            loadHighScores();
            gameLoop.stop();
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> pacman.nextDirection = 'U';
            case KeyEvent.VK_DOWN -> pacman.nextDirection = 'D';
            case KeyEvent.VK_LEFT -> pacman.nextDirection = 'L';
            case KeyEvent.VK_RIGHT -> pacman.nextDirection = 'R';
        }

        switch (pacman.direction) {
            case 'U' -> pacman.image = pacmanUpImage;
            case 'D' -> pacman.image = pacmanDownImage;
            case 'L' -> pacman.image = pacmanLeftImage;
            case 'R' -> pacman.image = pacmanRightImage;
        }
    }
}


