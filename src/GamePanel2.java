import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class GamePanel2 extends JPanel implements Runnable {
    final int WIDTH = 900;
    final int HEIGHT = 600;
    final int UNIT_SIZE = 30;
    char direction = 'R';
    List<Integer> snakeBodyX;
    List<Integer> snakeBodyY;
    boolean running;
    int bodyParts = 3;
    int applesEaten = 0;
    int appleX;
    int appleY;
    int highScore;
    Random random;
    Thread thread;

    public GamePanel2() {
        random = new Random();
        snakeBodyX = new ArrayList<>();
        snakeBodyY = new ArrayList<>();
        this.setFocusable(true);
        setInitialSnake();
        highScore = getHighScore();
        running = true;
        newApple();
        this.addKeyListener(new MyKeyAdapter());
        thread = new Thread(this);
        thread.start();
    }

    private void setInitialSnake() {
        snakeBodyX.add(UNIT_SIZE * 2);
        snakeBodyY.add(0);
        snakeBodyX.add(UNIT_SIZE);
        snakeBodyY.add(0);
        snakeBodyX.add(0);
        snakeBodyY.add(0);
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
                if (snakeBodyY.get(0)== HEIGHT) {
                    snakeBodyY.set(0, 0);
                } else {
                    snakeBodyY.set(0, snakeBodyY.get(0) + UNIT_SIZE);
                }
            }
            case 'L' -> {
                if (snakeBodyX.get(0) == 0) {
                    snakeBodyX.set(0, WIDTH - UNIT_SIZE);
                } else  {
                    snakeBodyX.set(0, snakeBodyX.get(0) - (UNIT_SIZE));
                }
            }
            case 'R' -> {
                if (snakeBodyX.get(0) > WIDTH) {
                    snakeBodyX.set(0, 0);
                }  else {
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

    private void checkCollision() {
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
        if (getHighScore() < applesEaten) {
            JOptionPane.showMessageDialog(this, "New high score: " + applesEaten, "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "YOUR SCORE: " + applesEaten, "Game over", JOptionPane.INFORMATION_MESSAGE);
        }
        System.exit(0);
    }

    private int getHighScore() {
        File file = new File("highscore.txt");
        String score = "0";
        try {
            Scanner myScanner = new Scanner(file);
            while (myScanner.hasNextLine()) {
                score = myScanner.nextLine();
            }
            if (applesEaten > Integer.parseInt(score) && !running) {
                upgradeScore(file);
            }
            myScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(score);
    }
    private void upgradeScore(File file) {
        PrintWriter myPrint = null;
        try {
            myPrint = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        myPrint.println(applesEaten);
        myPrint.close();
    }

    private void newApple() {
        appleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        while (snakeBodyY.contains(appleY) || snakeBodyX.contains(appleX) || appleY >= HEIGHT - (2 * UNIT_SIZE) || appleX >= WIDTH - (2 * UNIT_SIZE)) {
            appleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
            appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        }
    }


    @Override
    public void paint(Graphics g) {
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

    private void eatenApple() {
        if (snakeBodyY.get(0) == appleY && snakeBodyX.get(0) == appleX) {
            bodyParts++;
            snakeBodyX.add(snakeBodyX.get(bodyParts - 3));
            snakeBodyY.add(snakeBodyY.get(bodyParts - 3));
            applesEaten++;
            newApple();
        }
    }

    @Override
    public void run() {
        while (running) {
            checkCollision();
            move();
            eatenApple();
            repaint();
            try {
                Thread.sleep(80);
            } catch (InterruptedException ignored) {
            }
        }
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
