package com.example.lucas.roadscan;

/**
 * Created by lucas on 01/12/15.
 */
import android.location.Location;

import com.example.lucas.roadscan.Singleton.Constants;

/**
 * Created by lucas on 25/06/15.
 */
public class Pavement {
    private float R[];
    private float X,Y,Z;
    private Location location;
    private double lat, lng;
    private String time, IMEI;
    private float distance;
    private float mean;
    private float stdv;
    private float spd;
    private int id;
    private int flag;
    private float StdvMean, bearing, magnitude;



    public Pavement(){};




    public Pavement(Pavement pavAux, float stdv, float spd, int flag){

        this.location = pavAux.getLocation();
        this.time = pavAux.getTime();
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
        this.flag = flag;
        this.stdv = stdv;
        this.spd = spd;
        this.IMEI = Constants.deviceID;
        this.bearing = location.getBearing();
        magnitude = 0;

        this.distance = pavAux.getDistance();
    }

    public Pavement(float[] r, float x, float y, float z, Location location , String time) {
        R = r;
        X = x;
        Y = y;
        Z = z;

        this.location = location;
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
        this.time = time;
        this.IMEI = Constants.deviceID;
    }

    public Pavement(float[] r, float x, float y, float z) {
        R = r;
        X = x;
        Y = y;
        Z = z;
    }

    public float getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

    public double getLng() {
        return lng;
    }

    public float getSpd() {
        return spd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setSpd(float spd) {
        this.spd = spd;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Location getLocation() { return this.location; }

    public float getSpeed() {
        return (float)(location.getSpeed() * 3.6);
    }

    public String getTime() {
        return time;
    }

    public void setStdv(float stdv) {
        this.stdv = stdv;
    }

    public double getStdv() {

        return stdv;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getStdvMean() {
        return StdvMean;
    }

    public void setStdvMean(float stdvMean) {
        StdvMean = stdvMean;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setIMEI (String IMEI){
        this.IMEI = IMEI;
    }

    public String getIMEI (){ return IMEI; }

    public float[] getR() {
        return R;
    }

    public float getX() {
        return X;
    }

    public float getY() {
        return Y;
    }

    public void setDistance (float distance) {
        this.distance = distance;
    }

    public void setMean(float mean) {
        this.mean = mean;
    }

    public float getMean() {
        return mean;
    }

    public void setLocation(Location location) {

        this.location = location;
    }

    public float getDistance (){
        return this.distance;
    }

    public float getZ() {
        return Z;
    }

    public double getLat() { return lat;  }

    public double getLong() {
        return lng;
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

    public void setTime(String time) {
        this.time = time;
    }

    public int getFlag() {
        return this.flag;
    }
    @Override
    public String toString() {
        return "Pavement{" +
                "X=" + X +
                ", Y=" + Y +
                ", Z=" + Z +
                ", mean=" + mean +
                ", stdv=" + stdv +
                ", spd=" + spd +
                '}';
    }
}

