package json;

import com.google.gson.annotations.SerializedName;
import com.jsevy.jdxf.DXFDocument;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Instance {
    @SerializedName("name")
    public String name;
    @SerializedName("items")
    public List<Item> items;
    @SerializedName("bins")
    public List<Bin> bins;

    @SerializedName("strip_height")
    public Double stripHeight;

    public Instance(String name, List<Item> items, List<Bin> bins) {
        this.name = name;
        this.items = items;
        this.bins = bins;
        this.stripHeight = null;
    }

    public Instance(String name, List<Item> items, Double stripHeight) {
        this.name = name;
        this.items = items;
        this.stripHeight = stripHeight;
        this.bins = null;
    }

    public void setShapePaths(String folderName) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            item.dxfPath = folderName + "/i_" + i + ".dxf";
        }
        if (bins != null) {
            for (int i = 0; i < bins.size(); i++) {
                Bin bin = bins.get(i);
                bin.dxfPath = folderName + "/o_" + i + ".dxf";
            }
        }
    }

    public void writeDXFs(File folder) throws IOException {
        for (Item item : items) {
            DXFDocument dxf = item.generateDXF();
            File file = new File(folder, item.dxfPath);
            FileWriter fw = new FileWriter(file);
            fw.write(dxf.toDXFString());
            fw.close();
        }
        if (bins != null) {
            for (Bin bin : bins) {
                DXFDocument dxf = bin.generateDXF();
                File file = new File(folder, bin.dxfPath);
                FileWriter fw = new FileWriter(file);
                fw.write(dxf.toDXFString());
                fw.close();
            }
        }
    }


    public String getName() {
        return name;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Bin> getBins() {
        return bins;
    }
}
