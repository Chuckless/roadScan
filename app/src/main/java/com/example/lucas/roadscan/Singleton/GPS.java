package com.example.lucas.roadscan.Singleton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdater;
import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdaterInterface;

import java.text.DecimalFormat;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by lucas on 06/07/15.
 */
public class GPS  implements LocationListener {

    private static GPS singleton = null;
    private final Context mContext;
    ScreenUpdater su;

    protected GPS(Context context){
        mContext = context;
        if(Constants.StopedMode == 1)
            Constants.go = 1;

        getLocation();
    }

    public static GPS getInstance(Context context){
        if(singleton == null){
            singleton = new GPS(context);
            Constants.GPSchangesCount = 0;
        }
        return singleton;
    }

    public static GPS getInstance(){
        if(singleton != null){
            return singleton;
        }
        return null;
    }



    // flag for GPS status
    public boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    public Location location; // location
    Location prevLocation;
    double latitude = 0.0; // latitude
    double longitude; // longitude
    float speed, distance;

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 50;//1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(mContext.LOCATION_SERVICE);

            // getting GPS status
//            isGPSEnabled = locationManager
//                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
//            isNetworkEnabled = locationManager
//                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
            String provider = locationManager.getBestProvider(criteria, true);


            String bestProvider = locationManager.getBestProvider(criteria, false);

            if (bestProvider== null){
                isGPSEnabled = locationManager.isProviderEnabled(provider);
                Log.i("GPS", "Provider " + provider + " has been selected.");
            }else{
                isGPSEnabled = locationManager.isProviderEnabled(bestProvider);
                Log.i("GPS", "Provider " + bestProvider + " has been selected.");
            }

            if (isGPSEnabled) {
                //Log.i("GPS", "isGPSEnabled");
                location = locationManager.getLastKnownLocation(provider);
                //prevLocation = location;

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
                if (locationManager != null) {

                    //Log.i("GPS", "LastKnownLocation = " + location);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates((LocationListener) GPS.this);
            singleton = null;
            Constants.go = 0;
            Log.d("GPS", "Terminando serviço GPS");
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        //if(location != null){
        //    latitude = location.getLatitude();
        //}

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        //if(location != null){
        //    longitude = location.getLongitude();
        //}

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }



    public void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("É necessário que o GPS esteja ligado. Deseja liga-lo?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                })
                .setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("GPS", "LocationChanged");

        if(location != null) {
            if (Constants.GPSchangesCount >= Constants.DROPTIMES) {

                this.location = location;
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                speed = (float) (location.getSpeed() * 3.6);
                if (speed > 0 && Constants.serviceRunning == 1)
                    distance = distance + location.distanceTo(prevLocation);

                prevLocation = location;

                if (Constants.showValues == 1) {
                   /* su = new ScreenUpdater(mContext, (ScreenUpdaterInterface) mContext);
                    su.setTask(Constants.UPDATE_LAT);
                    su.execute("" + latitude);

                    su = new ScreenUpdater(mContext, (ScreenUpdaterInterface) mContext);
                    su.setTask(Constants.UPDATE_LNG);
                    su.execute("" + longitude);*/

                    su = new ScreenUpdater(mContext, (ScreenUpdaterInterface) mContext);
                    su.setTask(Constants.UPDATE_SPD);
                    su.execute("" + speed);


                    /*su = new ScreenUpdater(mContext, (ScreenUpdaterInterface) mContext);
                    su.setTask(Constants.UPDATE_MEDIA);
                    su.execute("" + location.getAccuracy());*/


                    su = new ScreenUpdater(mContext, (ScreenUpdaterInterface) mContext);
                    su.setTask(Constants.UPDATE_DIST);
                    su.execute("" + distance);
                }

                Log.i("GPS", "distance " + distance);
                //Log.i("GPS", "latitude " + latitude);
                //Log.i("GPS", "longitude " + longitude);
                //Log.i("GPS", "accuracy " + location.getAccuracy());

                Constants.go = 1;
            }


            if (Constants.StopedMode == 0){
                if (Constants.GPSchangesCount < Constants.DROPTIMES) {
                    Constants.go = 0;
                    Constants.GPSchangesCount++;

                    su = new ScreenUpdater(mContext, (ScreenUpdaterInterface) mContext);
                    su.setTask(Constants.UPDATE_GPS);
                    su.execute("" + (Constants.DROPTIMES - Constants.GPSchangesCount) + "Drops left");
                    prevLocation = location;
                }
            }
        }
    }


    public Location getLastLocation (){
        return this.location;
    }
    public float getDistance() {
        return distance;
    }


    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public void onProviderDisabled(String provider) {
        su = new ScreenUpdater(mContext, (ScreenUpdaterInterface) mContext);
        su.setTask(Constants.UPDATE_MEDIA);
        su.execute("" + "Disable");

        Constants.go = 0;
        Constants.GPSchangesCount = 0;
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("GPS", "MyLocationListener::onStatusChanged");
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("GPS", "Status Changed: Out of Service");

                su = new ScreenUpdater(mContext, (ScreenUpdaterInterface) mContext);
                su.setTask(Constants.UPDATE_GPS);
                su.execute("" + "Out of Service");

                Constants.go = 0;
                Constants.GPSchangesCount = 0;
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("GPS", "Status Changed: Temporarily Unavailable");
                su = new ScreenUpdater(mContext, (ScreenUpdaterInterface) mContext);
                su.setTask(Constants.UPDATE_GPS);
                su.execute("" + "Temp Unavailable");

                Constants.go = 0;
                Constants.GPSchangesCount = 0;

                break;
            case LocationProvider.AVAILABLE:
                Log.d("GPS", "Status Changed: Available");

                /*su = new ScreenUpdater(mContext, (ScreenUpdaterInterface) mContext);
                su.setTask(Constants.UPDATE_GPS);
                su.execute("" + "Available");*/

                break;
            default:
                Log.d("GPS", "Status Changed: I dont know... throw an Exception");

               /* su = new ScreenUpdater(mContext, (ScreenUpdaterInterface) mContext);
                su.setTask(Constants.UPDATE_GPS);
                su.execute("" + "IDK");*/
        }
    }




}
