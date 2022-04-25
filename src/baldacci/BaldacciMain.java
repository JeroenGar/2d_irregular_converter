package baldacci;

import baldacci.Parser;
import json.Instance;

import java.io.File;

public class BaldacciMain {

    public static void main(String[] args) throws Exception{
        File folder = new File(args[0]);
        Instance instance = Parser.parseInstance(folder);
        File convertedFolder = new File(folder.getAbsolutePath() + "_converted");
        convertedFolder.mkdir();
        Parser.writeInstance(instance,convertedFolder);
    }

}
