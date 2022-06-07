package json;

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
        dxfDocument.setLayer("base");
        shape.draw(dxfGraphics);

        for (Map.Entry<String, Zone> entry : zones.entrySet()) {
            switch (entry.getValue().quality){
                case 0: dxfGraphics.setColor(Color.RED); break;
                case 1: dxfGraphics.setColor(Color.ORANGE); break;
                case 2: dxfGraphics.setColor(Color.YELLOW); break;
                case 3: dxfGraphics.setColor(Color.GREEN); break;
                case 4: dxfGraphics.setColor(Color.BLUE); break;
                default: throw new RuntimeException("Unknown quality: " + entry.getValue().quality);
            }
            dxfDocument.setLayer(entry.getKey());
            entry.getValue().shape.draw(dxfGraphics);
        }

        return dxfDocument;
    }

}
