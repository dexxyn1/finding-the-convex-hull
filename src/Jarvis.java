import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class Jarvis {
    public enum CurrentStep {
        FindLeftMostPoint,
        SetInitialTarget,
        CheckingTargets,
        LoopingPoints,
        BufferStep1,
        SaveTargets,
        BufferStep2,
        Finished
    }
    public LinkedList<Point> points;
    public LinkedList<Point> resultList;
    public Point current;
    public Set<Point> result;
    public LinkedList<Point> collinearPoint;
    public Point nextTarget;
    public Point leftMostPoint;
    public int index;
    public CurrentStep nextStep;
    public Point lastTarget;
    public Jarvis(LinkedList<Point> points) {
        setJarvisPoints(points);
    }

    private void setNextTarget(Point point) {
        lastTarget = nextTarget;
        nextTarget = point;
    }

    public String nextStep() {
        String returnMessage = "";
        switch (nextStep) {
            case FindLeftMostPoint:
                leftMostPoint = Point.findBottomLeftPoint(points); // find left most point
                current = leftMostPoint;
                result = new LinkedHashSet<>();
                result.add(leftMostPoint);
                collinearPoint = new LinkedList<>();
                nextStep = CurrentStep.SetInitialTarget;
                returnMessage = String.format("Found Left-Most Point %s", leftMostPoint);
                break;
            case SetInitialTarget:
                nextTarget = points.get(0);
                lastTarget = points.get(0);
                nextStep = CurrentStep.CheckingTargets;
                index = 1;
                returnMessage = String.format("Set initial target : %s ", nextTarget);
                break;
            case CheckingTargets:
                nextStep = CurrentStep.LoopingPoints;
            case LoopingPoints:
                try {
                    if (points.get(index).equals(current)) {
                        returnMessage = String.format("Point chosen%s is same as current%s, skipping.", points.get(index), current);
                        index++;
                        break;
                    }
                    Point.Orientation orientation = Point.orientation(current, nextTarget, points.get(index));
/*                    System.out.printf("%s->%s->%s(O:%s)%n", current, nextTarget, points.get(index),
                            (orientation == Point.Orientation.Left ? "Left" :
                                    orientation == Point.Orientation.Right ? "Right" : "Collinear"));*/
                    if (orientation == Point.Orientation.Left) {
                        returnMessage = String.format("[%s--%s] || [%s--%s] :: Left, Found new target : %s", current, nextTarget,current, points.get(index), points.get(index));
                        setNextTarget(points.get(index));
                        collinearPoint = new LinkedList<>();
                    } else if (orientation == Point.Orientation.Collinear) {
                        if (current.getDistanceTo(nextTarget) < current.getDistanceTo(points.get(index))) {
                            collinearPoint.add(nextTarget);
                            setNextTarget(points.get(index));
                        } else {
                            collinearPoint.add(points.get(index));
                        }
                        returnMessage = String.format("[%s--%s] || [%s--%s] :: Collinear, getting farthest one", current, nextTarget, current, points.get(index));
                    } else {
                        returnMessage = String.format("[%s--%s] || [%s--%s] :: right side, skipping", current, nextTarget, current, points.get(index));
                    }
                }catch (IndexOutOfBoundsException ex) {
                    nextStep = CurrentStep.BufferStep1;
                }
                index++;
                if (index > points.size()) {
                    returnMessage = String.format("Found Left most point of : %s == %s ", current, nextTarget);
                }
                break;
            case BufferStep1:
                nextStep = CurrentStep.SaveTargets;
                returnMessage = "Recording new point";
                break;
            case SaveTargets:
                result.addAll(collinearPoint);
                if (nextTarget == leftMostPoint) {
                    nextStep = CurrentStep.Finished;
                    returnMessage = "All done!";
                    break;
                }
                index = 1;
                result.add(nextTarget);
                current = nextTarget;
                nextStep = CurrentStep.BufferStep2;
                nextTarget = points.get(0);
                returnMessage = String.format("Saved target! :: result : %s", result);
                break;
            case BufferStep2:
                nextStep = CurrentStep.CheckingTargets;
                returnMessage = String.format("Initial target set to : %s", nextTarget);
                break;
            case Finished:
                returnMessage = "Finished!";
        }
        return returnMessage;
    }

    public void setJarvisPoints(LinkedList<Point> points) {
       // resultList = jarvisAlgorithms(points);
        this.points = new LinkedList<>(points);
        nextStep = CurrentStep.FindLeftMostPoint;
    }
    private LinkedList<Point> jarvisAlgorithms(LinkedList<Point> points) {
        LinkedList<Point> toWorkOn = new LinkedList<>(points);
        LinkedList<Point> resultList;
        Point leftMostPoint = Point.findBottomLeftPoint(points); // find left most point
        Point current = leftMostPoint;
        Set<Point> result = new LinkedHashSet<>();
        result.add(leftMostPoint);

        LinkedList<Point> collinearPoint = new LinkedList<>();
        while(true) {
            System.out.println(result);
            Point nextTarget = toWorkOn.get(0);
            for (int i = 1; i < points.size(); i++) {
                if (points.get(i) == current) {
                    continue;
                }
                Point.Orientation orientation = Point.orientation(current, nextTarget, points.get(i));
                /*System.out.printf("%s->%s->%s(O:%s)%n", current, nextTarget, points.get(i),
                        (orientation == Point.Orientation.Left ? "Left" :
                        orientation == Point.Orientation.Right ? "Right" : "Collinear"));*/
                if (orientation == Point.Orientation.Left) {
                    nextTarget = points.get(i);
                    collinearPoint = new LinkedList<>();
                } else if (orientation == Point.Orientation.Collinear) {
                    if (current.getDistanceTo(nextTarget) < current.getDistanceTo(points.get(i))) {
                        collinearPoint.add(nextTarget);
                        nextTarget = points.get(i);
                    } else {
                        collinearPoint.add(points.get(i));
                    }
                }
            }
            result.addAll(collinearPoint);
            if (nextTarget == leftMostPoint)
                break;
            result.add(nextTarget);
            current = nextTarget;
        }
        resultList = new LinkedList<>(result);
        return resultList;
    }

}
