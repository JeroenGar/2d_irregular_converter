package shirts;

import json.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ShirtsParser {
    static List<Double> allowedOrientations = Arrays.asList(0.0, 180.0);

    public static Instance parseInstance(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String name = "shirts";
        List<Bin> bins = new ArrayList<>();

        int height = 40;
        int width = 63;

        Bin bin = createBin(height, width);
        bins.add(bin);

        List<Item> items = new ArrayList<>();
        String line;

        while (true) {
            if (br.readLine() == null){ //PIECE 1
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
            Item item = new Item(demand, demand, null, null, allowedOrientations, null, shape);
            items.add(item);

            br.readLine();
        }

        return new Instance(name, items, bins);
    }


    private static Bin createBin(int height, int width){
        return new Bin(
                (double) (height*width),
                1,
                new HashMap<>(),
                new Shape(
                        new ArrayList<>(Arrays.asList(
                                new Point(0, 0),
                                new Point(width, 0),
                                new Point(width, height),
                                new Point(0, height)
                        )),
                        new ArrayList<>()
                )
        );
    }
}
