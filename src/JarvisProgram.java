import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class JarvisProgram {
    private static LinkedList<Point> points;
    private static JFrame frame;
    private static DrawingPanel drawingPanel;
    private static Jarvis jarvis;
    private static JButton nextButton;
    public static void main(String[] args) {
        points = new LinkedList<>();
        for (int x = 0; x < 20; x++) {
            int randomX = (int)(Math.random()*x/2)-3;
            int randomY = (int)(Math.random()*x/2)-4;
            Point point = new Point(randomX,randomY);
            points.addFirst(point);
        }
        jarvis = new Jarvis(points);
        System.out.println(jarvis.resultList);
        setUpGUI();
        drawPoints();
    }
    private static void drawPoints() {
        nextButton.setEnabled(false);
        for (Point point : points) {
            drawingPanel.drawPoint(point);
            drawingPanel.repaint();
            /*
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

             */
        }
        nextButton.setEnabled(true);
    }
    private static void drawRecordedPoints() {
        if (jarvis.result.size() > 1) {
            LinkedList<Point> points = new LinkedList<>(jarvis.result);
            for (int i = 0; i < points.size() - 1; i++) {
                drawingPanel.drawLine(points.get(i), points.get(i + 1), Color.green);
            }
        }
    }
    private static void drawFinalWrap() {
        LinkedList<Point> points = new LinkedList<>(jarvis.result);
        for (int i = 0; i < points.size() - 1; i++) {
            drawingPanel.drawLine(points.get(i), points.get(i + 1), Color.green);
        }
        drawingPanel.drawLine(points.get(0), points.get(points.size()-1), Color.green);
    }
    private static void highlightPointResult() {
        LinkedList<Point> points = new LinkedList<>(jarvis.result);
        for (Point point : points) {
            drawingPanel.drawPoint(point, Color.MAGENTA, 5);
        }
    }
    private static void setUpGUI() {
        drawingPanel = new DrawingPanel(500, 500, 20);
        frame = new JFrame("Jarvis Algorithm");
        JPanel centerPanel = new JPanel();
        JPanel buttonsPanel = new JPanel();
        JPanel tempCenterPanel1 = new JPanel();
        JPanel tempCenterPanel2 = new JPanel();
        JLabel info = new JLabel("...");
        tempCenterPanel1.add(drawingPanel);
        tempCenterPanel2.add(info);
        nextButton = new JButton("Next");
        centerPanel.add(tempCenterPanel1);
        centerPanel.add(tempCenterPanel2);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        buttonsPanel.add(nextButton);
        frame.add(centerPanel);
        frame.add(buttonsPanel, BorderLayout.EAST);
        frame.setVisible(true);

        nextButton.addActionListener(e -> {
            String message = jarvis.nextStep();
            info.setText(message);
            switch (jarvis.nextStep) {
                case FindLeftMostPoint:
                    System.out.println(message);
                    break;
                case SetInitialTarget:
                    highlightPointResult();
                    System.out.println(message);
                    break;
                case CheckingTargets:
                    drawingPanel.drawDLine(jarvis.current, jarvis.nextTarget, Color.black);
                    System.out.printf("\t\tdrawing %s -> %s%n", jarvis.current, jarvis.nextTarget);
                    System.out.println(message);
                    break;
                case LoopingPoints:
                    drawingPanel.removeAllLines();
                    if (jarvis.nextTarget != jarvis.points.get(jarvis.index-1)) {
                        drawingPanel.drawDLine(jarvis.current, jarvis.points.get(jarvis.index-1), Color.blue);
                        System.out.printf("\t\t1drawing %s -> %s%n", jarvis.current, jarvis.points.get(jarvis.index-1));
                        drawingPanel.drawDLine(jarvis.current, jarvis.nextTarget, Color.black);
                        System.out.printf("\t\t2drawing %s -> %s%n", jarvis.current, jarvis.nextTarget);
                    } else {
                        drawingPanel.drawDLine(jarvis.current, jarvis.lastTarget);
                        System.out.printf("\t\t3drawing %s -> %s%n", jarvis.current, jarvis.lastTarget);
                        drawingPanel.drawDLine(jarvis.current, jarvis.nextTarget, Color.blue);
                        System.out.printf("\t\t4drawing %s -> %s%n", jarvis.current, jarvis.nextTarget);
                    }
                    drawRecordedPoints();
                    System.out.println(message);
                    drawingPanel.revalidate();
                    drawingPanel.repaint();
                    break;
                case BufferStep1:
                    drawingPanel.removeAllLines();
                    drawingPanel.drawLine(jarvis.current, jarvis.nextTarget, Color.yellow);
                    drawRecordedPoints();
                    System.out.printf("\t\t3drawing %s -> %s%n", jarvis.current, jarvis.lastTarget);
                    System.out.println(message);
                    break;
                case SaveTargets:
                    System.out.println(message);
                    break;
                case BufferStep2:
                    drawingPanel.removeAllLines();
                    drawRecordedPoints();
                    highlightPointResult();
                    System.out.println(message);
                    break;
                case Finished:
                    drawFinalWrap();
                    System.out.println(message);
                    break;
            }
            frame.revalidate();
            frame.repaint();
        });
        frame.pack();
    }
}
