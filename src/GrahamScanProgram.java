import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;

public class GrahamScanProgram {
    private static GrahamScan grahamScan;
    private static JFrame frame;
    private static DrawingPanel drawingPanel;
    private static LinkedList<Point> points;
    private static JButton nextButton;
    private static JLabel nextStep;
    private static boolean lastDraw;
    private static JPanel infoPanel;
    private static File currentFile;
    private static JLabel returnedMessage;
    public static void main(String[] args) {
        points = new LinkedList<>();
        for (int x = 0; x < 20; x++) {
            int randomX = (int)(Math.random()*x/2)-3;
            int randomY = (int)(Math.random()*x/2)-4;
            Point point = new Point(randomX,randomY);
            points.addFirst(point);
        }

        grahamScan = new GrahamScan(null);
        setUpGUI();
        showSample();
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

    private static void setNextStep(String text) {
        nextStep.setText("Next Step : " + text);
        nextStep.revalidate();
        nextStep.repaint();
    }
    private boolean stackHasTwoItems(LinkedList<Point> points) {
        Point top = points.pop();
        Point nextToTop = points.peek();
        points.push(top);
        return (nextToTop != null);
    }
    private static  void printInfoPanel(boolean showPoints, boolean bottom, boolean pAngle, boolean stack) {
        if (grahamScan.points == null)
            return;
        infoPanel.removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.gridy =0;
        c.gridx = 0;
        if (bottom)
            infoPanel.add(new JLabel("Bottom-Left Point : " + grahamScan.bottomLeftPoint),c);
        else
            infoPanel.add(new JLabel("Bottom-Left Point : "),c);
        c.gridy = 1;
        c.gridx = 0;
        infoPanel.add(new JLabel("Points"),c);
        c.gridx = 1;
        infoPanel.add(new JLabel("P Angle"),c);
        for (int i = 0; i < grahamScan.points.size(); i ++) {
            c.gridy = i + 2;
            c.gridx = 0;
            if (showPoints)
                infoPanel.add(new JLabel(grahamScan.points.get(i).toString()), c);
            else
                infoPanel.add(new JLabel("--"),c );
            c.gridx = 1;
            if (pAngle)
                infoPanel.add(new JLabel(grahamScan.bottomLeftPoint == null ? "--" :
                    grahamScan.bottomLeftPoint.getAngleTo(grahamScan.points.get(i))+""), c);
            else
                infoPanel.add(new JLabel("--"), c);
        }
        StringBuilder stringBuilder = new StringBuilder();

        c.gridy = grahamScan.points.size()+1;
        c.gridx = 0;
        infoPanel.revalidate();
        infoPanel.repaint();
        frame.revalidate();
        frame.repaint();
    }
    private static LinkedList<Point> pointsFileToLinkedList() {
        Scanner scanner = null;
        LinkedList<Point> result = new LinkedList<>();
        try {
            scanner = new Scanner(currentFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }
        while (Objects.requireNonNull(scanner).hasNextLine())
        {
            String line = scanner.nextLine();
            String[] points = line.split(",");
            int x;
            int y;
            try {
                x = Integer.parseInt(points[0]);
                y = Integer.parseInt(points[1]);
            } catch (NumberFormatException e) {
                System.out.println("File chosen is not a file for points!");
                JOptionPane.showMessageDialog(frame, "File chosen is not a file for points!");
                return null;
            }
            result.addFirst(new Point(x, y));
        }
        return result;
    }
    private static void importPointsFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            System.out.println("Opening: " + currentFile.getName() + ".");
        } else {
            System.out.println("Open file cancelled by user.");
        }
    }

    private static void setReturnedMessage(String message) {
        returnedMessage.setText("Current Step: " + message);
    }
    private static void setUpGUI () {
        frame = new JFrame("Graham Scan");
        drawingPanel = new DrawingPanel(400, 400, 20);
        JPanel centerPanel = new JPanel();
        JPanel drawingPanel2 = new JPanel();
        drawingPanel2.add(drawingPanel);
        nextButton = new JButton("NEXT");
        nextStep = new JLabel("---");
        centerPanel.add(drawingPanel2);
        returnedMessage = new JLabel("Current step: ----");
        JPanel messagePanel = new JPanel();
        messagePanel.add(returnedMessage);
        centerPanel.add(messagePanel);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        JPanel rightSide = new JPanel();
        rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.Y_AXIS));
        rightSide.add(nextButton);
        rightSide.add(nextStep);
        rightSide.setPreferredSize(new Dimension(270, 0));


        nextButton.addActionListener(e -> {
            if (grahamScan.points == null)
                return;
            String messageReturned = grahamScan.nextStep();
            setReturnedMessage(messageReturned);
            switch (grahamScan.currentStep) {
                case ComputePolarAngle:
                    printInfoPanel(true, true, false, false);
                    setNextStep("Compute Polar Angle");
                    break;
                case SortByPolarAngle:
                    printInfoPanel(true, true, true, false);
                    setNextStep("Sort by Polar Angle");
                    break;
                case RemoveSameAngle:
                    printInfoPanel(true, true, true, false);
                    setNextStep("Process Same Angles");
                    break;
                case CheckIfPossible:
                    printInfoPanel(true, true, true, false);

                    setNextStep("Check if convex hull is possible");
                    break;
                case CreateStack:
                    setNextStep("Create stack");
                    break;
                case Processing:
                    setNextStep("Process");
                    System.out.println("SS"+grahamScan.stack);
                    drawingPanel.removeAllLines();
                    for (int i = 1; i < grahamScan.stack.size() - 1; i++) {
                        System.out.println("DRAWING");
                        drawingPanel.drawLine(grahamScan.stack.get(i), grahamScan.stack.get(i + 1));
                    }
                    drawingPanel.drawDLine(grahamScan.stack.get(0), grahamScan.stack.get(1));
                    drawingPanel.drawDLine(grahamScan.points.get(grahamScan.index), grahamScan.stack.get(0));
                    frame.repaint();
                    break;
                case Finished:
                    nextStep.setText("--");
                    if (!lastDraw) {
                        drawingPanel.removeAllLines();
                        for (int i = 0; i < grahamScan.stack.size() - 1; i++) {
                            System.out.println("DRAWING");
                            drawingPanel.drawLine(grahamScan.stack.get(i), grahamScan.stack.get(i + 1));
                        }
                        drawingPanel.drawLine(grahamScan.stack.get(0), grahamScan.stack.get(grahamScan.stack.size()-1));
                        frame.repaint();
                    }
                    JOptionPane.showMessageDialog(frame, "Done!");
            }

        });
        //centerPanel.add(next);
        //centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());
        infoPanel.setPreferredSize(new Dimension(300, 0));
        printInfoPanel(true, false, false, false);

        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open");
        menuBar.add(file);
        file.add(open);
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importPointsFromFile();
                reset();
            }
        });
        nextButton.setEnabled(false);
        frame.setJMenuBar(menuBar);
        frame.add (rightSide, BorderLayout.EAST);
        frame.add (centerPanel, BorderLayout.CENTER);
        frame.add(infoPanel, BorderLayout.WEST);
        frame.setSize(1000, 600);
        frame.setVisible(true);
    }
    private static void reset() {
        drawingPanel.reset();
        nextButton.setEnabled(true);
        points = pointsFileToLinkedList();
        grahamScan.setGrahamScan(points);
        drawPoints();
        printInfoPanel(true, false, false, false);
        nextStep.setText("Next Step : Find Bottom Left Point");
        setReturnedMessage("Showing points");
        frame.revalidate();
        frame.repaint();
    }

    private static void showSample() {
        drawingPanel.reset();
        nextButton.setEnabled(true);
        grahamScan.setGrahamScan(points);
        drawPoints();
        printInfoPanel(true, false, false, false);
        nextStep.setText("Next Step : Find Bottom Left Point");
        setReturnedMessage("Showing points");
        frame.revalidate();
        frame.repaint();
    }
}
