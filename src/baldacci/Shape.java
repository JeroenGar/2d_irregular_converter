package baldacci;

import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Shape {
    List<Point> points;

    public Shape(List<Point> points) {
        if (!points.get(points.size() - 1).equals(points.get(0))) {
            points.add(points.get(0));
        }

        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shape shape = (Shape) o;
        return Objects.equals(points, shape.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }

    public void draw(DXFGraphics dxfGraphics){
        dxfGraphics.drawPolyline(
                points.stream().mapToInt(p -> p.getX()).toArray(),
                points.stream().mapToInt(p -> p.getY()).toArray(),
                points.size());
    }
}
