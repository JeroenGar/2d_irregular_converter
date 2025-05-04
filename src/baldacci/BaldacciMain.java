package baldacci;

import general.Util;
import json.Instance;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

public class BaldacciMain {
    final static boolean CLEAN_SHAPES = true;

    public static void main(String[] args) throws Exception {
        File parentFolder = new File(args[0]);
        File outputFolder = new File(parentFolder.getAbsolutePath() + "_converted");
        outputFolder.mkdir();
        for (File folder : parentFolder.listFiles()) {
            try {
                if (folder.isDirectory()) {
                    System.out.println("Processing folder " + folder.getName());
                    Instance instance = BaldacciParser.parseInstance(folder);
                    File convertedFolder = new File(parentFolder.getAbsolutePath() + "_converted/" + folder.getName());
                    convertedFolder.mkdir();
                    Util.writeInstance(instance, convertedFolder);

                    //create zip archive
                    FileOutputStream fos = new FileOutputStream(convertedFolder.getAbsolutePath() + ".zip");
                    ZipOutputStream zipOS = new ZipOutputStream(fos);

                    for (File file : convertedFolder.listFiles()) {
                        Util.zipFile(file, file.getName(), zipOS);
                    }

                    zipOS.close();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error processing folder " + folder.getName());
                throw e;
            }
        }
    }
}
