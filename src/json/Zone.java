package json;

import baldacci.Shape;
import com.google.gson.annotations.SerializedName;

public class Zone {
    @SerializedName("Quality")
    public Integer quality;
    transient Shape shape;


    public Zone(Integer quality, Shape shape) {
        this.quality = quality;
        this.shape = shape;
    }
}
