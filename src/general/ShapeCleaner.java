package general;

import json.Point;
import json.Shape;

import java.awt.geom.Line2D;
import java.util.*;

public class ShapeCleaner {
    public static final int MAX_DISTANCE = 3;
    //TODO: exactly map different zones on to each other if they are close enough together
    //TODO: resolve self intersections

    public Shape clean(Shape shape) {
        Shape cleanedShape = cleanDuplicatePoints(shape);
        cleanedShape = cleanSelfIntersections(cleanedShape);

        return cleanedShape;
    }

    public Shape cleanDuplicatePoints(Shape original) {
        List<Point> points = new ArrayList<>(original.points);
        int i = 0;
        while (i < points.size() - 1) {
            if (points.get(i).equals(points.get(i + 1))) {
                points.remove(i);
            } else {
                i++;
            }
        }
        return new Shape(points);
    }

    public Shape cleanSelfIntersections(Shape original) {
        List<Line> lines = Line.generateFromShape(original);

        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.size(); j++) {
                if (i == j || i == (j + 1) % lines.size() || (i+1) % lines.size() == j) {
                    //same or adjacent lines
                    continue;
                } else {
                    Line line1 = lines.get(i);
                    Line line2 = lines.get(j);
                    if (Line.intersects(line1, line2)) {
                        //self intersection detected
                        System.out.println("Self intersection detected");
                        int startingIndex = line1.j;
                        int endingIndex = line2.i;
                        //all points between these two indices need to be flipped
                        List<Point> fixedPoints = new ArrayList<>();
                        for (int i1 = 0; i1 < original.points.size(); i1++) {
                            int translatedIndex = -1;
                            if (i1 >= startingIndex && i1 <= endingIndex) {
                                translatedIndex = endingIndex - (i1 - startingIndex);
                            } else {
                                translatedIndex = i1;
                            }
                            fixedPoints.add(original.points.get(translatedIndex));
                        }
                        return cleanSelfIntersections(new Shape(fixedPoints));
                    }
                }
            }
        }
        return original;
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

        for (Integer index : pointsToBeReplaced) {
            if (index == 0)
            original.points.set(index, reference.points.get(index));
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

        //Inject points from the reference shape if they are close enough

        for (Point referencePoint : reference.points) {
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
            for (int i = 0; i < shape.points.size() - 1; i++) {
                int j = i + 1;
                Line line = new Line(shape.points.get(i), shape.points.get(j), i, j);
                lines.add(line);
            }
            return lines;
        }
    }
}
