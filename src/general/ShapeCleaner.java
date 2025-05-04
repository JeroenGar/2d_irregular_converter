package general;

import json.Point;
import json.Shape;

import java.awt.geom.Line2D;
import java.util.*;

public class ShapeCleaner {
    public static final int MAX_DISTANCE = 3;

    public Shape clean(Shape shape, String name) {
        ArrayList<Point> cleanedOuter = cleanDuplicatePoints(shape.outer_points, name);
        cleanedOuter = cleanSelfIntersections(cleanedOuter, name);

        ArrayList<ArrayList<Point>> cleanedInner = new ArrayList<>();
        for (ArrayList<Point> inner : shape.inner_points) {
            ArrayList<Point> cleanedInnerPoints = cleanDuplicatePoints(inner, name);
            cleanedInnerPoints = cleanSelfIntersections(cleanedInnerPoints, name);
            cleanedInner.add(cleanedInnerPoints);
        }

        return new Shape(cleanedOuter, cleanedInner);
    }

    public ArrayList<Point> cleanDuplicatePoints(ArrayList<Point> original, String name) {
        ArrayList<Point> points = new ArrayList<>(original);
        int i = 0;
        while (i < points.size() - 1) {
            if (points.get(i).equals(points.get(i + 1))) {
                points.remove(i);
            } else {
                i++;
            }
        }
        if (points.size() < original.size()) {
            System.out.println("\t\t" + name + " removed " + (original.size() - points.size()) + " duplicate points");
        }

        return points;
    }

    public ArrayList<Point> cleanSelfIntersections(ArrayList<Point> original, String name) {
        List<Line> lines = Line.generateFromPoints(original);

        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.size(); j++) {
                if (i == j || i == (j + 1) % lines.size() || (i + 1) % lines.size() == j) {
                    //same or adjacent lines
                    continue;
                } else {
                    Line line1 = lines.get(i);
                    Line line2 = lines.get(j);
                    if (Line.intersects(line1, line2)) {
                        //self intersection detected
                        System.out.println("\t\t" + name + ": self intersection detected");
                        int startingIndex = line1.j;
                        int endingIndex = line2.i;
                        //all points between these two indices need to be flipped
                        ArrayList<Point> fixedPoints = new ArrayList<>();
                        for (int i1 = 0; i1 < original.size(); i1++) {
                            int translatedIndex = -1;
                            if (i1 >= startingIndex && i1 <= endingIndex) {
                                translatedIndex = endingIndex - (i1 - startingIndex);
                            } else {
                                translatedIndex = i1;
                            }
                            fixedPoints.add(original.get(translatedIndex));
                        }
                        return cleanSelfIntersections(fixedPoints, name);
                    }
                }
            }
        }
        return original;
    }

    //Maps points close to the reference shape onto the reference shape
    public ArrayList<Point> cleanSharedVertices(ArrayList<Point> original, ArrayList<Point> reference, String name) {
        //Find which vertices are close to the reference shape
        Set<Integer> pointsToBeReplaced = new HashSet<>();
        List<Line> referenceLines = Line.generateFromPoints(reference);

        for (int i = 0; i < original.size(); i++) {
            Point point = original.get(i);
            if (referenceLines.stream().anyMatch(line -> line.distanceTo(point) < MAX_DISTANCE)) {
                pointsToBeReplaced.add(i);
            }
        }

        Set<Integer> falsePositives = new HashSet<>();
        for (Integer index : pointsToBeReplaced) {
            int indexBefore = index - 1 % original.size();
            int indexAfter = index + 1 % original.size();

            if (!pointsToBeReplaced.contains(indexBefore) && !pointsToBeReplaced.contains(indexAfter)) {
                falsePositives.add(index);
            }
        }
        pointsToBeReplaced.removeAll(falsePositives);

        int nSnappedVertices = 0;
        int nNotSnappedBecauseTooFar = 0;
        int nRemovedDuplicates = 0;
        int nInjectedVertices = 0;

        //Replace the vertices
        ArrayList<Point> cleanedPoints = new ArrayList<>();

        for (int i = 0; i < original.size(); i++) {
            Point originalPoint = original.get(i);
            if (pointsToBeReplaced.contains(i)) {
                Point point = reference.stream().min(
                                Comparator.comparingDouble(p -> Point.distance(p, originalPoint)))
                        .get();

                if (Point.distance(originalPoint, point) < MAX_DISTANCE * 10) {
                    cleanedPoints.add(point);
                    nSnappedVertices++;
                } else {
                    cleanedPoints.add(originalPoint);
                    nNotSnappedBecauseTooFar++;
                }
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

        for (Point referencePoint : reference) {
            List<Line> cleanedLines = Line.generateFromPoints(cleanedPoints);
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
                    } else if (Math.abs(indexOfClosestPoint - indexOfSecondClosestPoint) == reference.size() - 1) {
                        //Closest and second-closest point are adjacent, so we should inject the point
                        cleanedPoints.add(0, referencePoint);
                        nInjectedVertices++;
                    }
                }
            }
        }
        if (nSnappedVertices > 0) {
            System.out.println("\tCleaned " + name + ":");
            System.out.println("\t\tSN: " + (nSnappedVertices - nRemovedDuplicates) + "\tTF: " + nNotSnappedBecauseTooFar + "\tIJ: " + nInjectedVertices + "\tFP:" + falsePositives.size());
        }

        return cleanedPoints;
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

        public static boolean intersects(Line l1, Line l2) {
            Line2D l2D1 = new Line2D.Double(l1.start.x, l1.start.y, l1.end.x, l1.end.y);
            Line2D l2D2 = new Line2D.Double(l2.start.x, l2.start.y, l2.end.x, l2.end.y);

            return l2D1.intersectsLine(l2D2);
        }

        public static List<Line> generateFromPoints(ArrayList<Point> points) {
            List<Line> lines = new ArrayList<>();
            for (int i = 0; i < points.size() - 1; i++) {
                int j = i + 1;
                Line line = new Line(points.get(i), points.get(j), i, j);
                lines.add(line);
            }
            return lines;
        }

        public double distanceTo(Point point) {
            return new Line2D.Double(start.x, start.y, end.x, end.y).ptSegDist(point.x, point.y);
        }
    }
}
