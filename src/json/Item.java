package json;

import com.google.gson.annotations.SerializedName;
import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;
import general.Util;

import java.util.ArrayList;
import java.util.List;
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

    @SerializedName("BaseQuality")
    public Integer baseQuality;

    @SerializedName("AllowedOrientations")
    public List<Double> allowedOrientations;

    @SerializedName("Zones")
    public List<Zone> zones;

    @SerializedName("Shape")
    public Shape shape;

    public Item(Integer demand, Integer demandMax, Integer value, Integer baseQuality, List<Double> allowedOrientations, List<Zone> zones, Shape shape) {
        this.demand = demand;
        this.demandMax = demandMax;
        this.value = value;
        this.baseQuality = baseQuality;
        this.allowedOrientations = allowedOrientations;
        this.shape = shape;
        this.zones = zones;
    }


    public DXFDocument generateDXF(){
        DXFDocument dxfDocument = new DXFDocument();
        DXFGraphics dxfGraphics = dxfDocument.getGraphics();

        if (this.baseQuality != null){
            dxfGraphics.setColor(Util.qualityColorMapper(this.baseQuality));
        }
        shape.draw(dxfGraphics);

        if (zones != null) {
            int i = 0;
            for (Zone zone : zones) {
                String name = "zone_" + i + "_q" + zone.quality;

                dxfGraphics.setColor(Util.qualityColorMapper(zone.quality));
                dxfDocument.setLayer(name);
                zone.shape.draw(dxfGraphics);
                i++;
            }
        }

        return dxfDocument;
    }



}
