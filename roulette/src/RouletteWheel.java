import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class RouletteWheel extends JPanel {
    private double ballAngle = 0;
    private double wheelAngle = 0;
    private int targetPosition = -1;
    private Timer timer;
    private final int[] wheelNumbers = {0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26};

    public RouletteWheel() {
        setPreferredSize(new Dimension(300, 300));
        setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 2 - 20;
        double angleStep = 2 * Math.PI / 37;

        Graphics2D g2d = (Graphics2D) g;
        g2d.rotate(wheelAngle, centerX, centerY);

        // Rysowanie koła
        for (int i = 0; i < 37; i++) {
            double startAngle = i * angleStep;
            String color = (wheelNumbers[i] == 0) ? "zielony" : (i % 2 == 0) ? "czarny" : "czerwony";
            g.setColor(getColor(color));
            g2d.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, (int) Math.toDegrees(startAngle), (int) Math.toDegrees(angleStep));
        }

        // Rysowanie liczb
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        for (int i = 0; i < 37; i++) {
            double angle = i * angleStep + angleStep / 2;
            int x = (int) (centerX + (radius * 0.7) * Math.cos(angle) - 5);
            int y = (int) (centerY + (radius * 0.7) * Math.sin(angle) + 5);
            g.drawString(String.valueOf(wheelNumbers[i]), x, y);
        }
        g2d.rotate(-wheelAngle, centerX, centerY);

        // Rysowanie kulki
        int ballRadius = 10;
        int ballX = (int) (centerX + (radius - ballRadius) * Math.cos(ballAngle)) - ballRadius / 2;
        int ballY = (int) (centerY + (radius - ballRadius) * Math.sin(ballAngle)) - ballRadius / 2;
        g.setColor(Color.WHITE);
        g.fillOval(ballX, ballY, ballRadius, ballRadius);
        g.setColor(Color.BLACK);
        g.drawOval(ballX, ballY, ballRadius, ballRadius);
    }

    private Color getColor(String color) {
        switch (color) {
            case "czerwony": return Color.RED;
            case "czarny": return Color.BLACK;
            case "zielony": return Color.GREEN;
            default: return Color.GRAY;
        }
    }

    public void startAnimation(int target, Runnable onFinish) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        targetPosition = target;
        double targetAngle = -(double) java.util.Arrays.stream(wheelNumbers).takeWhile(n -> n != target).count() * (2 * Math.PI / 37);

        final double[] ballSpeed = {new Random().nextDouble() * 0.2 + 0.2};
        final double[] wheelSpeed = {-(new Random().nextDouble() * 0.15 + 0.1)};
        final double deceleration = 0.005;
        final double[] totalBallAngle = {0.0};

        timer = new Timer(20, e -> {
            ballAngle += ballSpeed[0];
            wheelAngle += wheelSpeed[0];
            totalBallAngle[0] += ballSpeed[0];

            ballSpeed[0] = Math.max(0.01, ballSpeed[0] - deceleration);
            wheelSpeed[0] = Math.max(-0.01, wheelSpeed[0] + deceleration / 2);

            double currentAngle = ballAngle % (2 * Math.PI);
            double distanceToTarget = Math.min(Math.abs(currentAngle - targetAngle), 2 * Math.PI - Math.abs(currentAngle - targetAngle));

            if (totalBallAngle[0] > 4 * Math.PI && distanceToTarget < 0.1 && ballSpeed[0] < 0.02) {
                ballAngle = targetAngle;
                wheelAngle = 0;
                timer.stop();
                onFinish.run();
            }
            repaint();
        });
        timer.start();
    }
}