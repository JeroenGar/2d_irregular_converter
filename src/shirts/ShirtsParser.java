package shirts;

import json.Instance;
import json.Item;
import json.Point;
import json.Shape;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShirtsParser {
    static List<Double> allowedOrientations = Arrays.asList(0.0, 180.0);
    static Double stripHeight = 40.0;

    public static Instance parseInstance(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String name = "shirts";

        List<Item> items = new ArrayList<>();

        while (true) {
            if (br.readLine() == null) { //PIECE 1
                break;
            }
            br.readLine(); //QUANTITY
            int demand = Integer.parseInt(br.readLine());
            br.readLine(); //NUMBER OF VERTICES
            int numberOfVertices = Integer.parseInt(br.readLine());
            br.readLine(); //VERTICES (X,Y)

            ArrayList<Point> points = new ArrayList<>();
            for (int i = 0; i < numberOfVertices; i++) {
                String coordinates = br.readLine().trim().replaceAll(" +", " ");
                double x = Double.parseDouble(coordinates.split(" ")[0]);
                double y = Double.parseDouble(coordinates.split(" ")[1]);
                points.add(new Point(x, y));
            }

            Shape shape = new Shape(points, new ArrayList<>());
            Integer id = items.size();
            Item item = new Item(id, demand, null, null, null, allowedOrientations, null, shape);
            items.add(item);

            br.readLine();
        }
        return new Instance(name, items, stripHeight);
    }
}
