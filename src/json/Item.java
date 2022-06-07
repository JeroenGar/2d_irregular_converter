package json;

import com.google.gson.annotations.SerializedName;
import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;
import general.Util;

import java.awt.*;
import java.util.Map;

public class Item {
    @SerializedName("Demand")
    public Integer demand;
    @SerializedName("DemandMax")
    public Integer demandMax;

    @SerializedName("Value")
    public Integer value;
    @SerializedName("Dxf")
    public String dxfPath;

    @SerializedName("Quality")
    public Integer quality;

    @SerializedName("Zones")
    public Map<String, Zone> zones;

    @SerializedName("Shape")
    public Shape shape;

    public Item(Integer demand, Integer demandMax, Integer value, Integer quality, Map<String, Zone> zones, Shape shape) {
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

        if (this.quality != null){
            dxfGraphics.setColor(Util.qualityColorMapper(this.quality));
        }
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
