package json;

import com.google.gson.annotations.SerializedName;
import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;
import general.Util;

import java.util.List;

public class Item {
    @SerializedName("id")
    public Integer id;
    @SerializedName("demand")
    public Integer demand;
    @SerializedName("demand_max")
    public Integer demandMax;

    @SerializedName("value")
    public Integer value;
    @SerializedName("dxf")
    public String dxfPath;

    @SerializedName("min_quality")
    public Integer minQuality;

    @SerializedName("allowed_orientations")
    public List<Double> allowedOrientations;

    @SerializedName("zones")
    public List<Zone> zones;

    @SerializedName("shape")
    public Shape shape;

    public Item(Integer id, Integer demand, Integer demandMax, Integer value, Integer minQuality, List<Double> allowedOrientations, List<Zone> zones, Shape shape) {
        this.id = id;
        this.demand = demand;
        this.demandMax = demandMax;
        this.value = value;
        this.minQuality = minQuality;
        this.allowedOrientations = allowedOrientations;
        this.shape = shape;
        this.zones = zones;
    }


    public DXFDocument generateDXF() {
        DXFDocument dxfDocument = new DXFDocument();
        DXFGraphics dxfGraphics = dxfDocument.getGraphics();

        if (this.minQuality != null) {
            dxfGraphics.setColor(Util.qualityColorMapper(this.minQuality));
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
