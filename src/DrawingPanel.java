import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class DrawingPanel extends JPanel {
    private int radius = 2;
    private int pointMultiplier = 100;
    private final int PADDING;
    private final int HEIGHT;
    private final int WIDTH;
    private final int arrowHeadLength = 5;
    private final int FONT_SIZE = 10;
    private final int COORDINATE_PIXEL_INTERVAL;
    private final Color DEFAULT_COLOR = Color.black;
    private final Color TEXT_COLOR = Color.black;
    DrawingPanel(int height, int width, int padding) {
        this.PADDING = padding;
        this.HEIGHT = height;
        this.WIDTH = width;
        COORDINATE_PIXEL_INTERVAL = 30;
        setPreferredSize(new Dimension(width+(padding*2), height+(padding*2)));
        setBackground(Color.gray);
    }
    private static class Line {
        Point point1;
        Point point2;
        Color color = null;
        Line(Point p1, Point p2) {
            point1 = new Point(p1.x, p1.y);
            point2 = new Point(p2.x, p2.y);
        }
        Line(Point p1, Point p2, Color color) {
            this(p1, p2);
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Line) {
                Line line = (Line) obj;
                return line.point1.x == point1.x &&
                        line.point1.y == point1.y &&
                        line.point2.x == point2.x &&
                        line.point2.y == point2.y;
            }
            return false;
        }

        @Override
        public String toString() {
            return String.format("(%d,%d)->(%d,%d)", point1.x, point1.y, point2.x, point2.y);
        }
    }
    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    private class DrawPoint {
        Point point;
        Color color;
        int radius;
        DrawPoint(Point point, Color color) {
            this.color = color;
            this.point = point;
            radius = 0;
        }
        DrawPoint(Point point, Color color, int radius) {
            this(point, color);
            this.radius = radius;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DrawPoint)
                return obj.equals(this.point);
            else return false;
        }
    }
    private LinkedList<DrawPoint> points = new LinkedList<>();
    private LinkedList<Line> lines = new LinkedList<>();
    private LinkedList<Line> dottedLines = new LinkedList<>();
    public void reset() {
        points = new LinkedList<>();
        removeAllLines();
    }
    public void drawLine(Point origin, Point destination) {
        lines.add(new Line(origin, destination));
    }
    public void drawLine(Point origin, Point destination, Color color) {
        lines.add(new Line(origin, destination, color));
    }
    public void removeLine(Point origin, Point destination) {
        lines.remove(new Line(origin, destination));
    }
    public void removeAllLines() {
        lines = new LinkedList<>();
        dottedLines = new LinkedList<>();
    }
    public void drawDLine(Point origin, Point destination) {
        dottedLines.add(new Line(origin, destination));
    }
    public void drawDLine(Point origin, Point destination, Color color) {
        dottedLines.add(new Line(origin, destination, color));
    }
    public void removeDLine(Point origin, Point destination) {
        dottedLines.remove(new Line(origin, destination));
    }
    public void drawPoint(Point point) {
        DrawPoint drawPoint = new DrawPoint(point, Color.black);
        if (points.contains(drawPoint))
            if (drawPoint.color == Color.black)
                throw new IllegalArgumentException("Point already exists!");
            else {
                points.remove(drawPoint);
                points.addFirst(drawPoint);
            }
        else
            points.add(drawPoint);
    }
    public void drawPoint(Point point, Color color) {
        DrawPoint drawPoint = new DrawPoint(point, color);
        if (points.contains(drawPoint))
            if (drawPoint.color == color)
                throw new IllegalArgumentException("Point already exists!");
            else {
                points.remove(drawPoint);
                points.addFirst(drawPoint);
            }
        else
            points.add(drawPoint);
    }
    public void drawPoint(Point point, Color color, int radius) {
        DrawPoint drawPoint = new DrawPoint(point, color, radius);
        if (points.contains(drawPoint))
            if (drawPoint.color == color)
                throw new IllegalArgumentException("Point already exists!");
            else {
                points.remove(drawPoint);
                points.addFirst(drawPoint);
            }
        else
            points.add(drawPoint);
    }
    public void removePoint(Point point) {
        points.remove(new DrawPoint(point, Color.black));
    }
    private int getYLocation(int y) {
        return (HEIGHT - (y*pointMultiplier))+PADDING;
    }
    private int getXLocation(int x) {
        return PADDING + (x*pointMultiplier);
    }
    private int getPointXLocation(Point point) {
        return getPointXRawLocation(point)-radius;
    }
    private int getPointXLocation(Point point, int radius) {
        return getPointXRawLocation(point)-radius;
    }
    private int getPointXRawLocation(Point point) {
        return (PADDING+point.x*COORDINATE_PIXEL_INTERVAL+WIDTH/2);
    }
    private int getPointYLocation(Point point) {
        return getPointYRawLocation(point)-radius;
    }
    private int getPointYLocation(Point point, int radius) {
        return getPointYRawLocation(point)-radius;
    }
    private int getPointYRawLocation(Point point) {
        return (PADDING+(-point.y*COORDINATE_PIXEL_INTERVAL)+HEIGHT/2);
    }
    private int diameter() {
        return radius*2;
    }
    private void drawCircle(Graphics g, Point point)  {
        g.fillOval(getXLocation(point.x), getYLocation(point.y), radius, radius);
    }
    private void drawPointWithText(Graphics g, DrawPoint point) {
        g.setColor(point.color);
        int diameter = point.radius == 0 ? diameter() : point.radius*2;
        g.fillOval(getPointXLocation(point.point, diameter/2),  getPointYLocation(point.point, diameter/2),
                diameter,diameter);
        g.setColor(TEXT_COLOR);
        drawPointLocation(g, point.point);
    }
    private void drawPointLocation(Graphics g, Point point) {
        g.drawString(String.format("(%d,%d)", point.x, point.y),
                getPointXLocation(point),   getPointYLocation(point));
    }

    private void drawGraph(Graphics g) {
        //g.setColor(graphAxisColor);

        /*
        for (int i = computeTentativeMiddleX(); i < computeTentativeMiddleX(); i++) {
            g.drawLine(i, computePaintableTopY(),i ,computePaintableBottomY());
            drawArrow(g, i, computePaintableBottomY(), Direction.UP);
            drawArrow(g, i, computePaintableTopY(), Direction.DOWN);
        }
        for (int i = computeTentativeMiddleY(); i < computeTentativeMiddleY(); i++) {
            g.drawLine(computePaintableLeftX(),i,computePaintableRightX(), i);
            drawArrow(g, computePaintableLeftX(), i, Direction.LEFT);
            drawArrow(g, computePaintableRightX(), i, Direction.RIGHT);
        }

         */
        //g.setColor(graphLineIntervalColor);
        drawCoordinateGuideLines(g);
    }
    private void drawCoordinateGuideLines(Graphics g) {
        drawCoordinateGuideLines(g, Direction.DOWN);
        drawCoordinateGuideLines(g, Direction.LEFT);
        drawCoordinateGuideLines(g, Direction.RIGHT);
        drawCoordinateGuideLines(g, Direction.UP);
        //drawCoordinateGuideLinesForQuadrant(g, originForQuadrant, Direction.RIGHT, Direction.UP, Quadrant.ONE);
        //drawCoordinateGuideLinesForQuadrant(g, originForQuadrant, Direction.LEFT, Direction.UP, Quadrant.TWO);
        //drawCoordinateGuideLinesForQuadrant(g, originForQuadrant, Direction.LEFT, Direction.DOWN, Quadrant.THREE);
        //drawCoordinateGuideLinesForQuadrant(g, originForQuadrant, Direction.RIGHT, Direction.DOWN, Quadrant.FOUR);

    }
    private void drawCoordinateGuideLines(Graphics g, Direction lineDir) {
        int centerX = WIDTH/2;
        int centerY = HEIGHT/2;

        int xInterval;
        int yInterval;
        switch (lineDir) {
            case RIGHT:
                xInterval = COORDINATE_PIXEL_INTERVAL;
                yInterval = 0;
                break;
            case LEFT:
                xInterval = -COORDINATE_PIXEL_INTERVAL;
                yInterval = 0;
                break;
            case UP:
                yInterval = COORDINATE_PIXEL_INTERVAL;
                xInterval=0;
                break;
            case DOWN:
                yInterval = -COORDINATE_PIXEL_INTERVAL;
                xInterval=0;
                break;
            default:
                throw new IllegalArgumentException();
        }
        g.setColor(Color.red);
        g.fillOval((PADDING + centerX)-radius, (PADDING + centerY)-radius, 2*radius, 2*radius);
        g.drawLine(PADDING+centerX, PADDING+ centerY, PADDING+centerX, PADDING);
        g.drawLine(PADDING+centerX, PADDING+ centerY, PADDING+centerX, HEIGHT+PADDING);
        g.drawLine(PADDING+centerX, PADDING+ centerY, PADDING, PADDING + centerY);
        g.drawLine(PADDING+centerX, PADDING+ centerY, WIDTH+PADDING, PADDING + centerY);
        for (int i = 0; true ; i++) {
            int xx= centerX + (xInterval*i);
            int yy = centerY + (yInterval*i);
            //System.out.printf("Drawing at : (%d, %d)%n", xx, yy);

            //drawCircle(g, new Point(xx, yy));
           // g.drawLine(PADDING+xx, PADDING+yy, PADDING+xx+5, PADDING+yy+5);
            if (lineDir == Direction.DOWN)
                if (yy <= 0)
                    break;
                else
                    g.drawLine(PADDING+xx-5, PADDING+yy, PADDING+xx+5, PADDING+yy);
            if (lineDir == Direction.UP)
                if (yy >= HEIGHT)
                    break;
                else
                    g.drawLine(PADDING+xx-5, PADDING+yy, PADDING+xx+5, PADDING+yy);
            if (lineDir == Direction.LEFT)
                if (xx <= 0)
                    break;
                else
                    g.drawLine(PADDING+xx, PADDING+yy-5, PADDING+xx, PADDING+yy+5);
            if (lineDir == Direction.RIGHT)
                if (xx >= WIDTH)
                    break;
                else
                    g.drawLine(PADDING+xx, PADDING+yy-5, PADDING+xx, PADDING+yy+5);
        }
        g.setColor(Color.black);

    }

    private void drawArrow(Graphics g, int x, int y, Direction ad) {
        int x1;
        int x2;
        int y1;
        int y2;
        switch (ad) {
            case UP:
                x1 = -arrowHeadLength;
                x2 = arrowHeadLength;
                y1 = arrowHeadLength;
                y2 = arrowHeadLength;
                break;
            case DOWN:
                x1 = -arrowHeadLength;
                x2 = arrowHeadLength;
                y1 = -arrowHeadLength;
                y2 = -arrowHeadLength;
                break;
            case LEFT:
                x1 = arrowHeadLength;
                x2 = arrowHeadLength;
                y1 = -arrowHeadLength;
                y2 = arrowHeadLength;
                break;
            case RIGHT:
                x1 = -arrowHeadLength;
                x2 = -arrowHeadLength;
                y1 = -arrowHeadLength;
                y2 = arrowHeadLength;
                break;
            default:
                x1 = 0;
                x2 = 0;
                y1 = 0;
                y2 = 0;

        }
        g.drawLine(x, y, x+x1, y+y1);
        g.drawLine(x, y, x+x2, y+y2);
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(new Font("TimesRoman", Font.PLAIN, FONT_SIZE));
        drawGraph(g);
        for (DrawPoint point : points) {
            drawPointWithText(g, point);
        }
        g.setColor(Color.blue);
        //System.out.println("LINES: " + lines);
        for (Line line : lines) {
            //System.out.printf("drawing line %s%n",line);
            if (line.color != null)
                g.setColor(line.color);
            else
                g.setColor(DEFAULT_COLOR);
            g.drawLine(getPointXRawLocation(line.point1), getPointYRawLocation(line.point1),
                    getPointXRawLocation(line.point2), getPointYRawLocation(line.point2));
        }
        for (Line line : dottedLines) {
            //System.out.printf("drawing line %s%n",line);
            if (line.color != null)
                g.setColor(line.color);
            else
                g.setColor(DEFAULT_COLOR);
            drawDashedLine(g,line.point1, line.point2);
        }

    }
    private void drawDashedLine(Graphics g, Point point1, Point point2){
        Graphics2D g2d = (Graphics2D) g.create();
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);
        g2d.drawLine(getPointXRawLocation(point1), getPointYRawLocation(point1),
                getPointXRawLocation(point2), getPointYRawLocation(point2));
        g2d.dispose();
    }
}
