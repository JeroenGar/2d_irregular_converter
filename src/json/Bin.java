package json;

import baldacci.Shape;
import com.google.gson.annotations.SerializedName;
import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;

import java.awt.*;
import java.util.Map;

public class Bin {
    @SerializedName("Cost")
    public Double cost;
    @SerializedName("Stock")
    public Integer stock;
    @SerializedName("Quality")
    public Integer quality;
    @SerializedName("Shape")
    public String shapePath;

    @SerializedName("Zones")
    public Map<String, Zone> zones;

    public transient Shape shape;


    public Bin(Double cost, Integer stock, Integer quality, Map<String, Zone> zones, Shape shape) {
        this.cost = cost;
        this.stock = stock;
        this.quality = quality;
        this.zones = zones;
        this.shape = shape;
    }

    public DXFDocument generateDXF(){
        DXFDocument dxfDocument = new DXFDocument();
        DXFGraphics dxfGraphics = dxfDocument.getGraphics();
        shape.draw(dxfGraphics);

        for (Map.Entry<String, Zone> entry : zones.entrySet()) {
            dxfGraphics.setColor(entry.getValue().quality == 0 ? Color.RED : Color.ORANGE);
            dxfDocument.setLayer(entry.getKey());
            entry.getValue().shape.draw(dxfGraphics);
        }

        return dxfDocument;
    }

}
