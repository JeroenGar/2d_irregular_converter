package general;

import json.Point;
import json.Shape;

import java.awt.geom.Line2D;
import java.util.*;
import java.util.stream.IntStream;

public class ShapeCleaner {
    public static final int MAX_DISTANCE = 3;
    //TODO: exactly map different zones on to each other if they are close enough together
    //TODO: resolve self intersections

    public Shape cleanSelfIntersections(Shape original) {
        return null;
    }

    //Maps points close to the reference shape onto the reference shape
    public Shape cleanSharedVertices(Shape original, Shape reference) {
        //Find which vertices are close to the reference shape
        Set<Integer> pointsToBeReplaced = new HashSet<>();
        List<Line> referenceLines = Line.generateFromShape(reference);

        boolean closeEnough = false;
        Integer startingInterval = null;
        Integer endingInterval = null;

        for (int i = 0; i < original.points.size(); i++) {
            Point point = original.points.get(i);
            if (referenceLines.stream().anyMatch(line -> line.distanceTo(point) < MAX_DISTANCE)){
                pointsToBeReplaced.add(i);
            }
        }

        //Replace the vertices
        List<Point> cleanedPoints = new ArrayList<>();

        for (int i = 0; i < original.points.size(); i++) {
            Point originalPoint = original.points.get(i);
            if(pointsToBeReplaced.contains(i)){
                Point point = reference.points.stream().min(
                        (p1,p2) -> Double.compare(Point.distance(p1, originalPoint), Point.distance(p2, originalPoint)))
                        .get();
                cleanedPoints.add(point);
            }
            else{
                cleanedPoints.add(originalPoint);
            }
        }
        return new Shape(cleanedPoints);
    }

    //Combines pairs of edge pairs in a straight line
    public Shape cleanStraightEdgePairs(Shape original) {
        return null;
    }


    private static class Interval {
        public int start;
        public int end;

        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "[" + start +
                    "," + end +
                    "]";
        }
    }

    private static class Line {
        public Point start;
        public Point end;
        public int i;
        public int j;

        public Line(Point start, Point end, int i, int j) {
            this.start = start;
            this.end = end;
            this.i = i;
            this.j = j;
        }

        public double distanceTo(Point point) {
            return new Line2D.Double(start.x, start.y, end.x, end.y).ptSegDist(point.x, point.y);
        }

        public static boolean intersects(Line l1, Line l2) {
            Line2D l2D1 = new Line2D.Double(l1.start.x, l1.start.y, l1.end.x, l1.end.y);
            Line2D l2D2 = new Line2D.Double(l2.start.x, l2.start.y, l2.end.x, l2.end.y);

            return l2D1.intersectsLine(l2D2);
        }

        public static List<Line> generateFromShape(Shape shape) {
            List<Line> lines = new ArrayList<>();
            for (int i = 0; i < shape.points.size(); i++) {
                int j = (i + 1) % shape.points.size();
                Line line = new Line(shape.points.get(i), shape.points.get(j), i, j);
                lines.add(line);
            }
            return lines;
        }
    }
}
