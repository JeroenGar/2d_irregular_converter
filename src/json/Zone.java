package json;

import com.google.gson.annotations.SerializedName;

public class Zone {
    @SerializedName("quality")
    public Integer quality;

    @SerializedName("shape")
    public Shape shape;

    public Zone(Integer quality, Shape shape) {
        this.quality = quality;
        this.shape = shape;
    }
}