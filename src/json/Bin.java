package json;

import com.google.gson.annotations.SerializedName;
import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;
import general.Util;

import java.awt.*;
import java.util.Map;

public class Bin {
    @SerializedName("Cost")
    public Double cost;
    @SerializedName("Stock")
    public Integer stock;
    @SerializedName("Dxf")
    public String dxfPath;

    @SerializedName("Zones")
    public Map<String, Zone> zones;

    @SerializedName("Shape")
    public Shape shape;


    public Bin(Double cost, Integer stock, Map<String, Zone> zones, Shape shape) {
        this.cost = cost;
        this.stock = stock;
        this.zones = zones;
        this.shape = shape;
    }

    public DXFDocument generateDXF(){
        DXFDocument dxfDocument = new DXFDocument();
        DXFGraphics dxfGraphics = dxfDocument.getGraphics();

        shape.draw(dxfGraphics);

        for (Map.Entry<String, Zone> entry : zones.entrySet()) {
            Zone zone = entry.getValue();
            String name = entry.getKey();

            dxfGraphics.setColor(Util.qualityColorMapper(zone.quality));
            dxfDocument.setLayer(name);
            zone.shape.draw(dxfGraphics);
        }

        return dxfDocument;
    }

}
