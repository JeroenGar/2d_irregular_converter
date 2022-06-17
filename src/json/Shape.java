package json;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.jsevy.jdxf.DXFGraphics;
import general.ShapeCleaner;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Shape {
    public List<Point> points;

    public Shape(List<Point> points) {
        if (!points.get(points.size() - 1).equals(points.get(0))) {
            points.add(points.get(0));
        }
        for (int i = 0; i < points.size(); i++) {
            for (int j = 0; j < points.size(); j++) {
                if (i == j || (i == 0 && j == points.size()-1) || (i == points.size()-1 && j == 0)) continue;
                if(points.get(i).equals(points.get(j))){
                    throw new RuntimeException("Duplicate point detected: " + points.get(i));
                }
            }
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
                points.stream().mapToDouble(p -> p.x).toArray(),
                points.stream().mapToDouble(p -> p.y).toArray(),
                points.size());
    }

    public static class Serializer implements JsonSerializer<Shape> {

        @Override
        public JsonElement serialize(Shape shape, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonElement jsonElement = jsonSerializationContext.serialize(shape.points);

            return jsonElement;
        }
    }

}
