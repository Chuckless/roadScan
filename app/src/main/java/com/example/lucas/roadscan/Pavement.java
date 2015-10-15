package com.example.lucas.roadscan;

/**
 * Created by lucas on 06/07/15.
 */

public class Pavement {
    int id;
    double IRI;
    double latitude;
    double longitude;
    String IMEI;

    public Pavement(){

    }

    public Pavement(double IRI, double latitude, double longitude, String IMEI){
        this.IRI = IRI;
        this.latitude = latitude;
        this.longitude = longitude;
        this.IMEI = IMEI;
    }

    public String getIMEI() {
        return IMEI;
    }

    @Override
    public String toString() {
        return "Pavement [ IRI= " + IRI + "]";
    }

    public void setIMEI(String iMEI) {
        IMEI = iMEI;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getIRI() {
        return IRI;
    }

    public void setIRI(double iRI) {
        IRI = iRI;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


}


