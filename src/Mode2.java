import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Mode2 extends JPanel implements ActionListener {
    static final int WIDTH = 900;
    static final int HEIGHT = 600;
    static final int UNIT_SIZE = 30;
    int delay = 110;
    List<Integer> snakeBodyX;
    List<Integer> snakeBodyY;
    int bodyParts = 3;
    int highScore;
    int applesEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    ExecutorService executor;

    private final Object fileLock = new Object();

    Mode2() {
        executor = Executors.newSingleThreadExecutor();
        highScore = getHighScore();
        random = new Random();
        snakeBodyX = new ArrayList<>();
        snakeBodyY = new ArrayList<>();
        setInitialSnake();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    private int getHighScore() {
        synchronized (fileLock) {
            return Integer.parseInt(readFile("highscore.txt", "0"));
        }
    }

    private void setInitialSnake() {
        snakeBodyX.add(UNIT_SIZE * 2);
        snakeBodyY.add(0);
        snakeBodyX.add(UNIT_SIZE);
        snakeBodyY.add(0);
        snakeBodyX.add(0);
        snakeBodyY.add(0);
    }

    private void startGame() {
        newApple();
        running = true;
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.red);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
        for (int i = 1; i < bodyParts; i++) {
            g.setColor(new Color(0, 143, 50));
            g.fillRect(snakeBodyX.get(i), snakeBodyY.get(i), UNIT_SIZE, UNIT_SIZE);
        }
        g.setColor(Color.green);
        g.fillRect(snakeBodyX.get(0), snakeBodyY.get(0), UNIT_SIZE, UNIT_SIZE);
        g.setColor(Color.red);
        Font myFont = new Font("Serif", Font.BOLD, 30);
        g.setFont(myFont);
        g.drawString("Score : " + applesEaten, 30, 40);
        g.drawString("High score: " + highScore, 675, 40);
    }

    private void newApple() {
        appleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        while (snakeBodyY.contains(appleY) || snakeBodyX.contains(appleX) || appleY >= HEIGHT - (2 * UNIT_SIZE) || appleX >= WIDTH - (2 * UNIT_SIZE)) {
            appleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
            appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        }
    }

    private void move() {
        int x = snakeBodyX.get(0);
        int y = snakeBodyY.get(0);
        switch (direction) {
            case 'U' -> {
                if (snakeBodyY.get(0) == 0) {
                    snakeBodyY.set(0, HEIGHT - (UNIT_SIZE * 2));
                } else {
                    snakeBodyY.set(0, snakeBodyY.get(0) - UNIT_SIZE);
                }
            }
            case 'D' -> {
                if (snakeBodyY.get(0) == HEIGHT - (UNIT_SIZE * 2)) {
                    snakeBodyY.set(0, 0);
                } else {
                    snakeBodyY.set(0, snakeBodyY.get(0) + UNIT_SIZE);
                }
            }
            case 'L' -> {
                if (snakeBodyX.get(0) <= 0) {
                    snakeBodyX.set(0, WIDTH - UNIT_SIZE);
                } else {
                    snakeBodyX.set(0, snakeBodyX.get(0) - (UNIT_SIZE));
                }
            }
            case 'R' -> {
                if (snakeBodyX.get(0) == WIDTH - UNIT_SIZE) {
                    snakeBodyX.set(0, 0);
                } else {
                    snakeBodyX.set(0, snakeBodyX.get(0) + UNIT_SIZE);
                }
            }
        }
        for (int i = 1; i < bodyParts; i++) {
            int x2 = snakeBodyX.get(i);
            snakeBodyX.set(i, x);
            x = x2;
            int y2 = snakeBodyY.get(i);
            snakeBodyY.set(i, y);
            y = y2;
        }
    }

    private void eatenApple() {
        if (snakeBodyY.get(0) == appleY && snakeBodyX.get(0) == appleX) {
            bodyParts++;
            snakeBodyX.add(snakeBodyX.get(bodyParts - 3));
            snakeBodyY.add(snakeBodyY.get(bodyParts - 3));
            applesEaten++;
            newApple();
            if (applesEaten % 5 == 0) {
                timer.setDelay(delay - 10);
                delay -= 10;
            }
        }
    }

    private void checkCollisions() {
        int snakeHeadX = snakeBodyX.get(0);
        int snakeHeadY = snakeBodyY.get(0);
        for (int i = 1; i < bodyParts; i++) {
            if (snakeHeadX == snakeBodyX.get(i) && snakeHeadY == snakeBodyY.get(i)) {
                gameOver();
            }
        }
    }

    private void gameOver() {
        running = false;
        executor.execute(() -> {
            synchronized (fileLock) {
                if (getHighScore() < applesEaten) {
                    writeFile("highscore.txt", String.valueOf(applesEaten));
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "New high score: " + applesEaten, "Congratulations!", JOptionPane.INFORMATION_MESSAGE));
                } else {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "YOUR SCORE: " + applesEaten, "Game over", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });
        System.exit(0);
    }

    private String readFile(String fileName, String defaultValue) {
        File file = new File(fileName);
        try (Scanner myScanner = new Scanner(file)) {
            return myScanner.hasNextLine() ? myScanner.nextLine() : defaultValue;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    private void writeFile(String fileName, String data) {
        try (PrintWriter myPrint = new PrintWriter(new File(fileName))) {
            myPrint.println(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            eatenApple();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
