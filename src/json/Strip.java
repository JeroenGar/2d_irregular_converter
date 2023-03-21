package json;

import com.google.gson.annotations.SerializedName;

public class Strip {
    @SerializedName("Height")
    public double height;

    public Strip(double height) {
        this.height = height;
    }
}
