package com.example.lucas.roadscan;

/**
 * Created by lucas on 06/07/15.
 */


/**
 * Created by lucas on 25/06/15.
 */
public class PavementAux {
    private float R[];
    private float X,Y,Z;
    private double latitude, longitude;
    private String time;
    private float distance;


    public PavementAux(float[] r, float x, float y, float z, double latitude, double longitude, String time, float distance) {
        R = r;
        X = x;
        Y = y;
        Z = z;

        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getDistance() {
        return distance;
    }

    public String getTime() {
        return time;
    }

    public float[] getR() {
        return R;
    }

    public float getX() {
        return X;
    }

    public float getY() {
        return Y;
    }

    public float getZ() {
        return Z;
    }

    public double getLat() {
        return latitude;
    }

    public double getLong() {
        return longitude;
    }

    public void setR(float[] r) {
        R = r;
    }

    public void setX(float x) {
        X = x;
    }

    public void setY(float y) {
        Y = y;
    }

    public void setZ(float z) {
        Z = z;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

