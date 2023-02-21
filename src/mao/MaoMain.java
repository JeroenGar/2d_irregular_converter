package mao;

import general.Util;
import json.Instance;
import org.xml.sax.SAXException;
import shirts.ShirtsParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class MaoMain {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        File parentFolder = new File(args[0]);
        File outputFolder = new File(parentFolder.getAbsolutePath() + "_converted");
        outputFolder.mkdir();

        File inputFile = new File(parentFolder.getAbsolutePath() + "/mao.xml");
        Instance instance = MaoParser.parseInstance(inputFile);

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
