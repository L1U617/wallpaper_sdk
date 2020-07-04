package cs.hku.wallpaper_sdk.model;

import java.util.Arrays;

public class AngelPicReq {
    private String model;
    private double[] vector;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double[] getMatrix() {
        return vector;
    }

    public void setMatrix(double[] vector) {
        this.vector = vector;
    }

    public AngelPicReq(String model, double[] vector) {
        this.model = model;
        this.vector = vector;
    }
}
