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

        for (int i = 0; i < original.points.size(); i++) {
            Point point = original.points.get(i);
            if (referenceLines.stream().anyMatch(line -> line.distanceTo(point) < MAX_DISTANCE)) {
                pointsToBeReplaced.add(i);
            }
        }

        int nSnappedVertices = 0;
        int nRemovedDuplicates = 0;
        int nInjectedVertices = 0;

        //Replace the vertices
        List<Point> cleanedPoints = new LinkedList<>();

        for (int i = 0; i < original.points.size(); i++) {
            Point originalPoint = original.points.get(i);
            if (pointsToBeReplaced.contains(i)) {
                Point point = reference.points.stream().min(
                                Comparator.comparingDouble(p -> Point.distance(p, originalPoint)))
                        .get();
                cleanedPoints.add(point);
                nSnappedVertices++;
            } else {
                cleanedPoints.add(originalPoint);
            }
        }

        //Remove duplicates
        int i = 0;
        while (i < cleanedPoints.size() - 1) {
            if (cleanedPoints.get(i).equals(cleanedPoints.get(i + 1))) {
                cleanedPoints.remove(i);
                nRemovedDuplicates++;
            } else {
                i++;
            }
        }
        if (cleanedPoints.get(cleanedPoints.size() - 1).equals(cleanedPoints.get(0))) {
            cleanedPoints.remove(cleanedPoints.size() - 1);
        }

        //Inject points from the reference shape if they are close enough

        for(Point referencePoint : reference.points) {
            List<Line> cleanedLines = Line.generateFromShape(new Shape(cleanedPoints));
            if (!cleanedPoints.contains(referencePoint)) {
                if (cleanedLines.stream().anyMatch(line -> line.distanceTo(referencePoint) < MAX_DISTANCE)) {
                    //Point should be added to the cleanedPoints
                    //Only question that remains now is where to insert it
                    //Search for the 2 closest points
                    Point closestPoint = cleanedPoints.stream().min(
                                    Comparator.comparingDouble(p -> Point.distance(p, referencePoint)))
                            .get();
                    Point secondClosestPoint = cleanedPoints.stream().filter(p -> !p.equals(closestPoint))
                            .min(Comparator.comparingDouble(p -> Point.distance(p, referencePoint)))
                            .get();
                    int indexOfClosestPoint = cleanedPoints.indexOf(closestPoint);
                    int indexOfSecondClosestPoint = cleanedPoints.indexOf(secondClosestPoint);
                    if (Math.abs(indexOfClosestPoint - indexOfSecondClosestPoint) == 1) {
                        //Closest and second-closest point are adjacent, so we should inject the point
                        cleanedPoints.add(Math.max(indexOfClosestPoint, indexOfSecondClosestPoint), referencePoint);
                        nInjectedVertices++;
                    } else if (Math.abs(indexOfClosestPoint - indexOfSecondClosestPoint) == reference.points.size() - 1) {
                        //Closest and second-closest point are adjacent, so we should inject the point
                        cleanedPoints.add(0, referencePoint);
                        nInjectedVertices++;
                    }
                }
            }
        }
        System.out.println("Snapped vertices: " + (nSnappedVertices - nRemovedDuplicates) + "\tInjected vertices: " + nInjectedVertices);

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
