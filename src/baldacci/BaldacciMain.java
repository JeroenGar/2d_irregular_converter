package baldacci;

import baldacci.Parser;
import general.Util;
import json.Instance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BaldacciMain {

    public static void main(String[] args) throws Exception{
        File parentFolder = new File(args[0]);
        File outputFolder = new File(parentFolder.getAbsolutePath() + "_converted");
        outputFolder.mkdir();
        for (File folder : parentFolder.listFiles()) {
            try {
                if (folder.isDirectory()) {
                    System.out.println("Processing folder " + folder.getName());
                    Instance instance = Parser.parseInstance(folder);
                    File convertedFolder = new File(parentFolder.getAbsolutePath() + "_converted/" + folder.getName());
                    convertedFolder.mkdir();
                    Parser.writeInstance(instance, convertedFolder);

                    //create zip archive
                    FileOutputStream fos = new FileOutputStream(convertedFolder.getAbsolutePath() + ".zip");
                    ZipOutputStream zipOS = new ZipOutputStream(fos);

                    for (File file : convertedFolder.listFiles()) {
                        Util.zipFile(file, file.getName(), zipOS);
                    }

                    zipOS.close();
                    fos.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error processing folder " + folder.getName());
            }
        }
    }
}
