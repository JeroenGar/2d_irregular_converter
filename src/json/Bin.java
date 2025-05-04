package json;

import com.google.gson.annotations.SerializedName;
import com.jsevy.jdxf.DXFDocument;
import com.jsevy.jdxf.DXFGraphics;
import general.Util;

import java.util.List;

public class Bin {
    @SerializedName("id")
    public Integer id;
    @SerializedName("cost")
    public Integer cost;
    @SerializedName("stock")
    public Integer stock;
    @SerializedName("dxf")
    public String dxfPath;

    @SerializedName("zones")
    public List<Zone> zones;

    @SerializedName("shape")
    public Shape shape;


    public Bin(Integer id, Integer cost, Integer stock, List<Zone> zones, Shape shape) {
        this.id = id;
        this.cost = cost;
        this.stock = stock;
        this.zones = zones;
        this.shape = shape;
    }

    public DXFDocument generateDXF() {
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
