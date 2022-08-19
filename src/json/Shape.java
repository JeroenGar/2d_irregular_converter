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
    public List<Point> outer_points;
    public List<List<Point>> inner_points;

    public Shape(List<Point> outer_points) {
        if (!outer_points.get(outer_points.size() - 1).equals(outer_points.get(0))) {
            outer_points.add(outer_points.get(0));
        }
        /*for (int i = 0; i < points.size(); i++) {
            for (int j = 0; j < points.size(); j++) {
                if (i == j || (i == 0 && j == points.size()-1) || (i == points.size()-1 && j == 0)) continue;
                if(points.get(i).equals(points.get(j))){
                    throw new RuntimeException("Duplicate point detected: " + points.get(i));
                }
            }
        }*/

        this.outer_points = outer_points;
        this.inner_points = new ArrayList<>();
    }

    public Shape(List<Point> outer_points, List<List<Point>> inner_points){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shape shape = (Shape) o;
        return Objects.equals(outer_points, shape.outer_points) && Objects.equals(inner_points, shape.inner_points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outer_points, inner_points);
    }

    public void draw(DXFGraphics dxfGraphics){
        dxfGraphics.drawPolyline(
                outer_points.stream().mapToDouble(p -> p.x).toArray(),
                outer_points.stream().mapToDouble(p -> p.y).toArray(),
                outer_points.size());

        for(List<Point> inner_points : inner_points){
            dxfGraphics.drawPolyline(
                    inner_points.stream().mapToDouble(p -> p.x).toArray(),
                    inner_points.stream().mapToDouble(p -> p.y).toArray(),
                    inner_points.size());
        }
    }

    public static class Serializer implements JsonSerializer<Shape> {

        @Override
        public JsonElement serialize(Shape shape, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonElement outerPoints = jsonSerializationContext.serialize(shape.outer_points);
            JsonElement innerPoints = jsonSerializationContext.serialize(shape.inner_points);

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("Outer", outerPoints);
            jsonObject.add("Inner", innerPoints);

            return jsonObject;
        }
    }

}
