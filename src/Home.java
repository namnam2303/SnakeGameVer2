import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

public class Home implements ActionListener {
    JFrame frame;
    JPanel title_panel;
    JPanel button_panel;
    JLabel textfield;
    JButton btnMorden;
    JButton btnClassic;
    Border whileLine;

    public Home() {
        frame = new JFrame();
        title_panel = new JPanel();
        button_panel = new JPanel();
        textfield = new JLabel();
        btnMorden = new JButton();
        btnClassic = new JButton();
        whileLine = BorderFactory.createLineBorder(Color.white, 2);
        setInterface();
    }

    private void setInterface() {
        frame.setTitle("Snake v2 by NamNP");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 610);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setVisible(true);
        textfield.setBackground(new Color(25, 25, 25));
        textfield.setForeground(new Color(255, 255, 255));
        textfield.setFont(new Font("Monaco", Font.BOLD, 75));
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setText("Game Mode");
        textfield.setOpaque(true);
        title_panel.setLayout(new BorderLayout());
        title_panel.setBounds(0, 0, 900, 100);
        setButtons();
        title_panel.add(textfield);
        frame.add(title_panel, BorderLayout.NORTH);
        frame.add(button_panel);
        frame.setLocationRelativeTo(null);
        title_panel.setBorder(whileLine);
        button_panel.setBorder(whileLine);
        Image icon = Toolkit.getDefaultToolkit().getImage("snake.jpg");
        frame.setIconImage(icon);
    }

    private void setButtons() {
        button_panel.setLayout(new GridLayout(2, 0));
        button_panel.add(btnClassic);
        button_panel.add(btnMorden);
        btnMorden.setSize(800, 350);
        btnMorden.addActionListener(this);
        btnMorden.setFont(new Font("Monaco", Font.BOLD, 120));
        btnMorden.setFocusable(false);
        btnMorden.setBackground(new Color(25, 25, 25));
        btnMorden.setForeground(new Color(0, 143, 50));
        btnMorden.setText("Classic");
        btnClassic.setSize(800, 350);
        btnClassic.addActionListener(this);
        btnClassic.setFont(new Font("Monaco", Font.BOLD, 120));
        btnClassic.setFocusable(false);
        btnClassic.setBackground(Color.black);
        btnClassic.setForeground(new Color(0, 143, 50));
        btnClassic.setText("Modern");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnMorden)) {
            frame.dispose();
            new GameFrame(new GamePanel());
        } else {
            frame.dispose();
            new GameFrame(new GamePanel2());
        }
    }

    public static void main(String[] args) {
        new Home();
    }
    private static class GameFrame extends JFrame {
        public GameFrame(JPanel gamePanel) {
            this.add(gamePanel);
            this.setSize(900,610);
            this.setLocationRelativeTo(null);
            this.setResizable(false);
            this.setVisible(true);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("Snake v2 by NamNP");
            this.setIconImage(Toolkit.getDefaultToolkit().getImage("snake.jpg"));
        }
    }
}