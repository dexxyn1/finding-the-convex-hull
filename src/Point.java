import java.util.LinkedList;

public class Point {
    public int x;
    public int y;
    public int angleFromOrigin;
    public double currentAngle;
    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getAngleTo(Point point) {
        return (int)Math.toDegrees(Math.atan2(point.y-y, point.x-x));
    }
    public double getDistanceTo(Point point) {
        return Math.sqrt(Math.pow(this.x - point.x, 2) + Math.pow(this.y - point.y, 2));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getAngleFromOrigin() {
        return angleFromOrigin;
    }

    public void setAngleFromOrigin(int angleToOrigin) {
        this.angleFromOrigin = angleToOrigin;
    }

    public double getCurrentAngle() {
        return currentAngle;
    }

    public void setCurrentAngle(double currentAngle) {
        this.currentAngle = currentAngle;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point point = (Point) obj;
            return point.x == x && point.y == y;
        }return false;
    }
    public static Orientation orientation(Point p, Point q, Point r)
    {
        int val = (q.y - p.y) * (r.x - q.x) -
                (q.x - p.x) * (r.y - q.y);
        if (val == 0) return Orientation.Collinear;  // colinear
        return (val > 0)? Orientation.Right: Orientation.Left; // clock or counterclock wise // right = 1 left = 2
    }
    public enum  Orientation {
        Collinear,
        Left,
        Right
    }
    public static Point findLeftMostPoint (LinkedList<Point> points) {
        Point leftMostPoint = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            if (leftMostPoint.x > points.get(i).x)
                leftMostPoint = points.get(i);
        }
        return leftMostPoint;
    }

    public static Point findBottomLeftPoint(LinkedList<Point> points) {
        if (points.size() == 0)
            throw new IllegalArgumentException();
        Point target = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).getY() == target.getY() && points.get(i).getX() < target.getX()) {
                target = points.get(i);
            } else if (points.get(i).getY() < target.getY()) {
                target = points.get(i);
            }
        }
        return target;
    }
}
