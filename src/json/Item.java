package json;

import baldacci.Shape;
import com.google.gson.annotations.SerializedName;
import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;

import java.util.Map;

public class Item {
    @SerializedName("Demand")
    public Integer demand;
    @SerializedName("DemandMax")
    public Integer demandMax;
    @SerializedName("Value")
    public Integer value;
    @SerializedName("Shape")
    public String shapePath;
    @SerializedName("Quality")
    public Integer quality;

    @SerializedName("Zones")
    public Map<String, Zone> zones;

    public transient Shape shape;

    public Item(Integer demand, Integer demandMax, Integer value, Shape shape, Integer quality, Map<String, Zone> zones) {
        this.demand = demand;
        this.demandMax = demandMax;
        this.value = value;
        this.quality = quality;
        this.shape = shape;
        this.zones = zones;
    }


    public DXFDocument generateDXF(){
        DXFDocument dxfDocument = new DXFDocument();
        DXFGraphics dxfGraphics = dxfDocument.getGraphics();
        shape.draw(dxfGraphics);

        return dxfDocument;
    }



}
