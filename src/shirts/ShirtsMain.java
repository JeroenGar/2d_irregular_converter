package shirts;

import general.Util;
import json.Instance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class ShirtsMain {

    public static void main(String[] args) throws IOException {
        File parentFolder = new File(args[0]);
        File outputFolder = new File(parentFolder.getAbsolutePath() + "_converted");
        outputFolder.mkdir();

        File inputFile = new File(parentFolder.getAbsolutePath() + "/shirts.txt");
        Instance instance = ShirtsParser.parseInstance(inputFile);

        Util.writeInstance(instance, outputFolder);

        //create zip archive
        FileOutputStream fos = new FileOutputStream(outputFolder.getAbsolutePath() + ".zip");
        ZipOutputStream zipOS = new ZipOutputStream(fos);

        for (File file : outputFolder.listFiles()) {
            Util.zipFile(file, file.getName(), zipOS);
        }

        zipOS.close();
        fos.close();
    }
}
