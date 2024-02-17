
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BouncingBalls extends JFrame {

    public BouncingBalls() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // усі потоки завершуються при закритті вікна
        setLayout(new BorderLayout());//CHECK

        BouncingBallsPanel bouncingBallsPanel = new BouncingBallsPanel();//CHECK
        add(bouncingBallsPanel, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Ball");
        addButton.addActionListener(e -> bouncingBallsPanel.addBall());
        add(addButton, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BouncingBalls::new);
    }
}

class BouncingBallsPanel extends JPanel {

    private final List<Ball> balls;

    public BouncingBallsPanel() {
        balls = new ArrayList<>();
        setPreferredSize(new Dimension(400, 400));

//        Timer timer = new Timer(10, e -> {
//            for (Ball ball : balls) {
//                ball.move(getWidth(), getHeight());
//            }
//            repaint();
//        });
//        timer.start();

    }

    public void addBall() {
        Random random = new Random();
        int startX = random.nextInt(getWidth() - Ball.DIAMETER);
        int startY = random.nextInt(getHeight() - Ball.DIAMETER);
        Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Ball ball = new Ball(startX, startY, color, getWidth(), getHeight(), this::repaint);
        balls.add(ball);

        Thread thread = new Thread(ball);
        thread.start();
        System.out.println(thread.getPriority());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Ball ball : balls) {
            ball.draw(g);
        }
    }
}

class Ball implements Runnable {
    interface Repaint {
        void repaint();
    }
    public static final int DIAMETER = 30;
    private int x;
    private int y;
    private int dx;
    private int dy;
    private final Color color;
    private final int panelWidth;
    private final int panelHeight;

    private final Repaint repaint;

    public Ball(int x, int y, Color color, int panelWidth, int panelHeight, Repaint repaint) {
        this.x = x;
        this.y = y;
        this.color = color;
        dx = getRandomSpeed();
        dy = getRandomSpeed();
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.repaint = repaint;

    }

    private int getRandomSpeed() {
        return (int) (Math.random() * 5) + 1;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, DIAMETER, DIAMETER);
    }

    public void move(int panelWidth, int panelHeight) {
        x += dx;
        y += dy;

        if (x < 0 || x > panelWidth - DIAMETER) {
            dx = -dx;
        }
        if (y < 0 || y > panelHeight - DIAMETER) {
            dy = -dy;
        }
    }

    @Override
    public void run() {
        while (true) {
            move(panelWidth, panelHeight);
            repaint.repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
