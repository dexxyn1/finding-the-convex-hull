import java.util.*;
import java.util.stream.Collectors;

public class GrahamScan {
    public LinkedList<Point> points;
    private LinkedList<Point> nonStepByStepresult;
    public LinkedList<Point> stack;
    public CurrentStep currentStep;
    public Point bottomLeftPoint;
    public int index;
    public enum CurrentStep {
        FindBottomLeftPoint,
        ComputePolarAngle,
        SortByPolarAngle,
        RemoveSameAngle,
        CheckIfPossible,
        CreateStack,
        Processing,
        Finished
    }
    static class ProcessingData {
        int i;

    }
    public void setGrahamScan(LinkedList<Point> points) {
        nonStepByStepresult = grahamScan(new LinkedList<>(points));
        stack = new LinkedList<>();
        index = 3;
        currentStep = CurrentStep.FindBottomLeftPoint;
        this.points = new LinkedList<>(points);
    }
    public String nextStep() {
        String returnMessage = "";
        switch (currentStep) {
            case FindBottomLeftPoint: //Find bottom left point
                bottomLeftPoint = Point.findBottomLeftPoint(points);
                currentStep = CurrentStep.ComputePolarAngle;
                returnMessage = String.format("Bottom-left point found : %s", bottomLeftPoint);
                break;
            case ComputePolarAngle: //Compute Polar Angle
                for (Point value : points) {
                    if (!value.equals(bottomLeftPoint))
                        value.setAngleFromOrigin(bottomLeftPoint.getAngleTo(value));
                }
                returnMessage = "Computed polar angles";
                currentStep = CurrentStep.SortByPolarAngle;
                break;
            case SortByPolarAngle://sort points according to their angle to the bottom left point
                this.points.remove(bottomLeftPoint);
                this.points = points.stream().sorted(Comparator.comparing(Point::getAngleFromOrigin)).collect(Collectors.toCollection(LinkedList::new));
                this.points.addFirst(bottomLeftPoint);
                currentStep = CurrentStep.RemoveSameAngle;
                returnMessage = "Sorted by polar angle";
                break;
            case RemoveSameAngle: //remove points with equal angle except the farthest one
                for (int i = 1; i <  this.points.size()-1;) { //start from the one after bottom left point
                    if ( this.points.get(i).getAngleFromOrigin() ==  this.points.get(i+1).getAngleFromOrigin()) {
                        if (bottomLeftPoint.getDistanceTo( this.points.get(i)) > bottomLeftPoint.getDistanceTo( this.points.get(i+1))) {
                            this.points.remove(i+1);
                        } else {
                            this.points.remove(i);
                        }
                        System.out.println("REMOVING");
                    }else {
                        i++;
                    }
                }
                returnMessage = "Removed points with save angle except the farthest from bottom-left point";
                currentStep = CurrentStep.CheckIfPossible;
                break;
            case CheckIfPossible:
                if (this.points.size() <3)
                    throw new IllegalArgumentException("Convex hull is not possible");
                currentStep = CurrentStep.CreateStack;
                returnMessage = "Convex hull is possible";
                break;
            case CreateStack:
                stack.push(this.points.get(0));
                stack.push(this.points.get(1));
                stack.push(this.points.get(2));
                returnMessage = "Stack is created";
                currentStep = CurrentStep.Processing;
                break;
            case Processing:
                // Keep removing top while the angle formed by
                // points next-to-top, top, and points[i] makes
                // a non-left turn
                if (Point.orientation(nextToTop(stack), getTop(stack), points.get(index)) != Point.Orientation.Left) {
                    Point point = stack.pop();
                    returnMessage = String.format("Took a right turn, popping %s", point);
                } else {
                    stack.push(points.get(index));
                    returnMessage = String.format("Considering adding %s", points.get(index));
                    index++;
                }
                if (index == points.size()) {
                    returnMessage = "Moving to final step";
                    currentStep = CurrentStep.Finished;
                }

                break;
            case Finished:
                returnMessage = "Finished!!";
        }
        return returnMessage;
    }

    GrahamScan(LinkedList<Point> points) {
        if (points != null)
            setGrahamScan(points);
    }
    private LinkedList<Point> grahamScan (LinkedList<Point> points) {
        LinkedList<Point> nonStepByStepresult = new LinkedList<>();
        //Find bottom left point
        Point bottomLeftPoint = Point.findBottomLeftPoint(points);
        points.remove(bottomLeftPoint);
        for (Point value : points) {
            value.setAngleFromOrigin(bottomLeftPoint.getAngleTo(value));
        }
        //sort points according to their angle to the bottom left point
        points = points.stream().sorted(Comparator.comparing(Point::getAngleFromOrigin)).collect(Collectors.toCollection(LinkedList::new));
        //remove points with equal angle except the farthest one
        for (int i = 0; i <  points.size()-1;) {
            if ( points.get(i).getAngleFromOrigin() ==  points.get(i+1).getAngleFromOrigin()) {
                if (bottomLeftPoint.getDistanceTo( points.get(i)) > bottomLeftPoint.getDistanceTo( points.get(i+1))) {
                    points.remove(i+1);
                } else {
                    points.remove(i);
                }
            }else {
                i++;
            }
        }

        if (points.size() <3)
            throw new IllegalArgumentException("Convex hull is not possible");

        points.addFirst(bottomLeftPoint);
        nonStepByStepresult.push(points.get(0));
        nonStepByStepresult.push(points.get(1));
        nonStepByStepresult.push(points.get(2));

        for (int i = 3; i < points.size(); i++) {

            // Keep removing top while the angle formed by
            // points next-to-top, top, and points[i] makes
            // a non-left turn
            while (Point.orientation(nextToTop(nonStepByStepresult), getTop(nonStepByStepresult), points.get(i)) != Point.Orientation.Left) {
                //System.out.println(stack)
                nonStepByStepresult.pop();
                if (nonStepByStepresult.size() == 0) {
                    throw new IllegalArgumentException();
                }
            }
            nonStepByStepresult.push(points.get(i));
        }
        System.out.println(nonStepByStepresult);
        return nonStepByStepresult;
    }
    private Point getTop(LinkedList<Point> stack) {
        return stack.peek();
    }
    private Point nextToTop(LinkedList<Point> stack)
    {
        Point p = stack.pop();
        //System.out.println(p);

        Point nextToTop = stack.peek();
        stack.push(p);
        return nextToTop;
    }





}
