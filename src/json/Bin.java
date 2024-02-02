package json;

import com.google.gson.annotations.SerializedName;
import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;
import general.Util;

import java.util.List;

public class Bin {
    @SerializedName("Cost")
    public Integer cost;
    @SerializedName("Stock")
    public Integer stock;
    @SerializedName("Dxf")
    public String dxfPath;

    @SerializedName("Zones")
    public List<Zone> zones;

    @SerializedName("Shape")
    public Shape shape;


    public Bin(Integer cost, Integer stock, List<Zone> zones, Shape shape) {
        this.cost = cost;
        this.stock = stock;
        this.zones = zones;
        this.shape = shape;
    }

    public DXFDocument generateDXF(){
        DXFDocument dxfDocument = new DXFDocument();
        DXFGraphics dxfGraphics = dxfDocument.getGraphics();

        shape.draw(dxfGraphics);

        int i = 0;
        for (Zone zone : zones) {
            String name = "zone_" + i + "_q" + zone.quality;

            dxfGraphics.setColor(Util.qualityColorMapper(zone.quality));
            dxfDocument.setLayer(name);
            zone.shape.draw(dxfGraphics);
            i++;
        }

        return dxfDocument;
    }

}
