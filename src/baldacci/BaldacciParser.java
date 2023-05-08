package baldacci;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import general.ShapeCleaner;
import json.*;

import java.io.*;
import java.util.*;

public class BaldacciParser {
    public static Instance parseInstance(File file) throws IOException {
        try {
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
                    optionalEqualShapeItem.get().demandMax += item.demandMax;
                } else {
                    items.add(item);
                }
            }
            br.close();
            fr.close();
            return new Instance(name, items, bins);
        } catch (Exception e) {
            System.err.println("Error parsing file: " + file.getAbsolutePath());
            throw e;
        }


    }

    private static Item parseItem(File file, int demand) throws IOException {
        System.out.println("Parsing item " + file.getName());
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            br.readLine();
            br.readLine();

            int numberOfVertices = Integer.parseInt(br.readLine().split(" ")[3]);
            ArrayList<Point> points = new ArrayList<>();
            for (int i = 0; i < numberOfVertices; i++) {
                String[] coords = br.readLine().split(" ");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                points.add(new Point(x, y));
            }
            int numberOfDefects = Integer.parseInt(br.readLine().split(" ")[3]);
            Integer quality = null;
            Map<String, Zone> zones = new HashMap<>(numberOfDefects);
            for (int i = 0; i < numberOfDefects; i++) {
                br.readLine(); //defect code
                int defectQuality = Integer.parseInt(br.readLine().split(" ")[2]);
                Shape defectShape = null;
                String geometryOrNVerticesLine = br.readLine();
                if (geometryOrNVerticesLine.startsWith("Defect geometry")) {
                    //defect applies to entire part
                    quality = defectQuality;
                } else {
                    int numberOfDefectVertices = Integer.parseInt(geometryOrNVerticesLine.split(" ")[3]);
                    ArrayList<Point> defectPoints = new ArrayList<>();
                    for (int j = 0; j < numberOfDefectVertices; j++) {
                        String[] coords = br.readLine().split(" ");
                        int x = Integer.parseInt(coords[0]);
                        int y = Integer.parseInt(coords[1]);
                        defectPoints.add(new Point(x, y));
                    }
                    defectShape = new Shape(defectPoints);
                    if (defectShape.equals(new Shape(points))) {
                        //Quality zone matches the entire shape
                        if (quality == null || defectQuality < quality) {
                            System.out.println("Quality zone matches the entire shape");
                            quality = defectQuality;
                        }
                    }
                    else {
                        Zone zone = new Zone(defectQuality, defectShape);
                        zones.put("zone " + i, zone);
                    }
                }
            }

            br.close();
            fr.close();

            Shape shape = new Shape(points);
            if (BaldacciMain.CLEAN_SHAPES){
                ShapeCleaner cleaner = new ShapeCleaner();
                shape = cleaner.clean(shape, file.getName());
                for (Map.Entry<String, Zone> entry : zones.entrySet()) {
                    Zone zone = entry.getValue();
                    String name = entry.getKey();
                    zone.shape = cleaner.clean(zone.shape, name);
                    zone.shape.outer_points = cleaner.cleanSharedVertices(zone.shape.outer_points, shape.outer_points, name);
                }
            }
            return new Item(demand, demand, null, quality, null, zones, shape);
        } catch (Exception e) {
            System.err.println("Error parsing file: " + file.getAbsolutePath());
            throw e;
        }
    }


    private static Bin parseBin(File file) throws IOException {
        System.out.println("Parsing bin: " + file.getName());
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            br.readLine();
            br.readLine();

            int numberOfVertices = Integer.parseInt(br.readLine().split(" ")[3]);
            ArrayList<Point> outer_points = new ArrayList<>();
            ArrayList<ArrayList<Point>> inner_points = new ArrayList<>();
            for (int i = 0; i < numberOfVertices; i++) {
                String[] coords = br.readLine().split(" ");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                outer_points.add(new Point(x, y));
            }
            int numberOfDefects = Integer.parseInt(br.readLine().split(" ")[3]);
            Map<String, Zone> zones = new HashMap<>(numberOfDefects);
            for (int i = 0; i < numberOfDefects; i++) {
                br.readLine();
                int defectType = Integer.parseInt(br.readLine().split(" ")[2]);
                int numberOfDefectVertices = Integer.parseInt(br.readLine().split(" ")[3]);
                ArrayList<Point> defectPoints = new ArrayList<>();
                for (int j = 0; j < numberOfDefectVertices; j++) {
                    String[] coords = br.readLine().split(" ");
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    defectPoints.add(new Point(x, y));
                }
                if (defectType == 0) {
                    //Hole in the bin
                    inner_points.add(defectPoints);
                }
                else {
                    //Quality zone
                    Shape defectShape = new Shape(defectPoints);
                    Zone zone = new Zone(defectType, defectShape);
                    zones.put("zone " + i, zone);
                }
            }

            Shape shape = new Shape(outer_points, inner_points);
            if (BaldacciMain.CLEAN_SHAPES){
                ShapeCleaner cleaner = new ShapeCleaner();
                shape = cleaner.clean(shape, file.getName());
                for (Map.Entry<String,Zone> entry : zones.entrySet()) {
                    Zone zone = entry.getValue();
                    String name = entry.getKey();
                    zone.shape = cleaner.clean(zone.shape, name);
                    //zone.shape.outer_points = cleaner.cleanSharedVertices(zone.shape.outer_points, shape.outer_points, name);
                }
            }

            Bin bin = new Bin(1.0, 1, zones, shape);

            br.close();
            fr.close();

            return bin;
        } catch (Exception e) {
            System.err.println("Error parsing file: " + file.getAbsolutePath());
            throw e;
        }
    }
}
