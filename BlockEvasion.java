import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class BlockEvasion extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 1500, HEIGHT = 900;
    private static final int PLAYER_WIDTH = 50, PLAYER_HEIGHT = 50;
    private static final int OBSTACLE_WIDTH = 100, OBSTACLE_HEIGHT = 50;
    private static final int INITIAL_OBSTACLE_SPEED = 5;
    private static final int INITIAL_OBSTACLE_FREQUENCY = 25;

    private Timer timer;
    private int playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
    private int playerY = HEIGHT - PLAYER_HEIGHT - 10;
    private int obstacleSpeed = INITIAL_OBSTACLE_SPEED;
    private int obstacleFrequency = INITIAL_OBSTACLE_FREQUENCY;
    private int currentLevel = 1;
    private long startTime;
    private boolean isGameOver = false;

    private ArrayList<Rectangle> obstacles = new ArrayList<>();
    private Random random = new Random();

    public BlockEvasion() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        startTime = System.currentTimeMillis();
        timer = new Timer(1000 / 60, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGameOver) {
            repaint();
            return;
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle obstacle = obstacles.get(i);
            obstacle.y += obstacleSpeed;
            if (obstacle.y > HEIGHT) {
                obstacles.remove(i);
                i--;
            }
        }

        if (random.nextInt(obstacleFrequency) == 0) {
            int obstacleX = random.nextInt(WIDTH - OBSTACLE_WIDTH);
            obstacles.add(new Rectangle(obstacleX, -OBSTACLE_HEIGHT, OBSTACLE_WIDTH, OBSTACLE_HEIGHT));
        }

        Rectangle player = new Rectangle(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);
        for (Rectangle obstacle : obstacles) {
            if (player.intersects(obstacle)) {
                isGameOver = true;
                timer.stop();
            }
        }

        // progression
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime > 30000) {
            currentLevel++;
            obstacleSpeed++;
            obstacleFrequency = Math.max(5, obstacleFrequency - 2);
            startTime = System.currentTimeMillis();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 74));
            g.drawString("Game Over", WIDTH / 2 - 200, HEIGHT / 2 - 50);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 36));
            g.drawString("R for retry, Q for quit", WIDTH / 2 - 250, HEIGHT / 2 + 50);
            return;
        }

        g.setColor(Color.WHITE);
        g.fillRect(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

        g.setColor(Color.RED);
        for (Rectangle obstacle : obstacles) {
            g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        g.drawString("Survived: " + elapsedTime + " seconds - Level " + currentLevel, 10, 30);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isGameOver) {
            if (e.getKeyCode() == KeyEvent.VK_R) {
                resetGame();
            } else if (e.getKeyCode() == KeyEvent.VK_Q) {
                System.exit(0);
            }
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT && playerX > 0) {
            playerX -= 10;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerX < WIDTH - PLAYER_WIDTH) {
            playerX += 10;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void resetGame() {
        playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
        obstacles.clear();
        obstacleSpeed = INITIAL_OBSTACLE_SPEED;
        obstacleFrequency = INITIAL_OBSTACLE_FREQUENCY;
        currentLevel = 1;
        startTime = System.currentTimeMillis();
        isGameOver = false;
        timer.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Block Evasion");
        BlockEvasion game = new BlockEvasion();

        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
