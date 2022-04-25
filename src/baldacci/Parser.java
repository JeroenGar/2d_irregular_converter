package baldacci;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import json.Zone;
import json.Instance;
import json.Item;
import json.Bin;

import java.io.*;
import java.util.*;

public class Parser {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Instance parseInstance(File file) throws IOException {
        FileReader fr = new FileReader(file.getAbsolutePath() + "/problem.dat");
        BufferedReader br = new BufferedReader(fr);

        String name = br.readLine().split(" : ")[1];
        br.readLine(); //Scale factor
        int numberOfObjects = Integer.parseInt(br.readLine().split(" : ")[1]);
        List<Bin> bins = new ArrayList<>();
        for (int i = 0; i < numberOfObjects; i++) {
            File objectFile = new File(file.getAbsolutePath() + "/" + br.readLine());
            Bin bin = parseBin(objectFile);
            bins.add(bin);
        }
        int numberOfItems = Integer.parseInt(br.readLine().split(" : ")[1]);
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < numberOfItems; i++) {
            String line = br.readLine();
            File itemFile = new File(file.getAbsolutePath() + "/" + line.split(" ")[0]);
            int itemDemand = Integer.parseInt(line.split(" ")[1]);
            Item item = parseItem(itemFile, itemDemand);
            Optional<Item> optionalEqualShapeItem = items.stream().filter(item1 -> item1.shape.equals(item.shape)).findFirst();

            if (optionalEqualShapeItem.isPresent()) {
                optionalEqualShapeItem.get().demand += item.demand;
            }
            else{
                items.add(item);
            }
        }

        if (br.readLine() != null) {
            throw new IOException("EOF expected");
        }


        return new Instance(name, items, bins);
    }

    private static Item parseItem(File file, int demand) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        br.readLine();
        br.readLine();

        int numberOfVertices = Integer.parseInt(br.readLine().split(" ")[3]);
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < numberOfVertices; i++) {
            String[] coords = br.readLine().split(" ");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            points.add(new Point(x, y));
        }
        int itemType = Integer.parseInt(br.readLine().split(" ")[3]);
        int quality = itemType == 0 ? 2 : 1;

        return new Item(demand, demand, null,  new Shape(points), quality, null);
    }


    private static Bin parseBin(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        br.readLine();
        br.readLine();

        int numberOfVertices = Integer.parseInt(br.readLine().split(" ")[3]);
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < numberOfVertices; i++) {
            String[] coords = br.readLine().split(" ");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            points.add(new Point(x, y));
        }
        int numberOfDefects = Integer.parseInt(br.readLine().split(" ")[3]);
        Map<String, Zone> defects = new HashMap<>(numberOfDefects);
        for (int i = 0; i < numberOfDefects; i++) {
            br.readLine();
            int defectType = Integer.parseInt(br.readLine().split(" ")[2]);
            int numberOfDefectVertices = Integer.parseInt(br.readLine().split(" ")[3]);
            List<Point> defectPoints = new ArrayList<>();
            for (int j = 0; j < numberOfDefectVertices; j++) {
                String[] coords = br.readLine().split(" ");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                defectPoints.add(new Point(x, y));
            }
            Shape defectShape = new Shape(defectPoints);
            Zone zone = new Zone(defectType, defectShape);
            defects.put("zone " + i, zone);
        }
        Bin bin = new Bin(null, null, 2, defects, new Shape(points));


        return bin;
    }

    public static void writeInstance(Instance instance, File folder) throws IOException {
        File instanceFile = new File(folder.getAbsolutePath() + "/instance.json");
        File dxfDirectory = new File(folder.getAbsolutePath() + "/dxf");
        dxfDirectory.mkdir();
        instance.setShapePaths(dxfDirectory.getName());
        instance.writeDXFs(folder);
        FileWriter fw = new FileWriter(instanceFile);
        fw.write(gson.toJson(instance));
        fw.close();
    }
}
