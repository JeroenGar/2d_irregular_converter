package json;

import com.google.gson.annotations.SerializedName;

public class Zone {
    @SerializedName("Quality")
    public Integer quality;

    @SerializedName("Shape")
    public Shape shape;


    public Zone(Integer quality, Shape shape) {
        this.quality = quality;
        this.shape = shape;
    }
}