package cs.hku.wallpaper_sdk.model;

import androidx.annotation.NonNull;

public class Direction {
    double x;
    double y;
    double z;

    public Direction(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @NonNull
    @Override
    public String toString() {
        return x+", "+y+", "+z;
    }

    public static Direction fromString(String str) {
        String[] s = str.split(",");
        return new Direction(Double.parseDouble(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]));
    }
}



