package general;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import json.Instance;
import json.Point;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Util {
    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    public static Color qualityColorMapper(int quality){
        switch (quality){
            case 0: return Color.RED;
            case 1: return Color.ORANGE;
            case 2: return Color.YELLOW;
            case 3: return Color.GREEN;
            case 4: return Color.BLUE;
            default: throw new RuntimeException("Unsupported quality: " + quality);
        }
    }

    public static Gson gson = new GsonBuilder()
                .registerTypeAdapter(json.Shape.class, new json.Shape.Serializer())
                .registerTypeAdapter(json.Point.class, new Point.Serializer())
                .setPrettyPrinting()
                .create();

    public static void writeInstance(Instance instance, File folder) throws IOException {
        try {
            File instanceFile = new File(folder.getAbsolutePath() + "/" + instance.name  + ".json");
            File dxfDirectory = new File(folder.getAbsolutePath() + "/dxf");
            dxfDirectory.mkdir();
            instance.setShapePaths(dxfDirectory.getName());
            instance.writeDXFs(folder);
            FileWriter fw = new FileWriter(instanceFile);
            fw.write(gson.toJson(instance));
            fw.close();
        } catch (Exception e) {
            System.err.println("Error writing instance: " + folder.getAbsolutePath());
            throw e;
        }
    }
}
