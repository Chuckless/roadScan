package com.example.lucas.roadscan;

/**
 * Created by lucas on 06/07/15.
 */


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.Manifest;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdater;
import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdaterInterface;
import com.example.lucas.roadscan.Singleton.Constants;
import com.example.lucas.roadscan.Singleton.DataContainer;
import com.example.lucas.roadscan.Singleton.GPS;

public class MyService extends Service implements SensorEventListener{



    // Constants for the low-pass filters
    private float timeConstant = 0.18f;
    private float alpha = 0.9f;
    private float dt = 0;

    // Timestamps for the low-pass filters
    private float timestamp = System.nanoTime();
    private float timestampOld = System.nanoTime();

    private float X, Y, Z;
    private float[] gravity = new float[] { 0, 0, 0 } ;
    private float[] linearAcceleration = new float[] { 0, 0, 0 };
    private int count = 0;

    private Pavement rawPavement;
    private DataContainer dc;
    private float R[] = new float[9];
    private float I[] = new float[9];
    public ScreenUpdater su;

    float[] mGeomagnetic = null;
    private DateFormat timeFormat;

    private SensorManager sensorManager;
    private Sensor sensorAcc;
    private Sensor sensorGrav;
    private Sensor sensorMag;
    private GPS gps = null;
    DecimalFormat df;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");

        dc = DataContainer.getInstance();
        df = new DecimalFormat("0.00000");
        gps = GPS.getInstance();

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        sensorMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorGrav  = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorAcc   = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Log.d("SensorService", "Acc: " + sensorAcc);
        Log.d("SensorService", "Grav: " + sensorGrav);
        Log.d("SensorService", "Mag: " + sensorMag);

        if(sensorMag != null)
            sensorManager.registerListener((SensorEventListener) this, sensorMag, SensorManager.SENSOR_DELAY_GAME);

        if(sensorAcc != null)
            sensorManager.registerListener((SensorEventListener)this, sensorAcc, SensorManager.SENSOR_DELAY_GAME);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sensorManager != null)
            sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("SensorService", "called");
        Date date = new Date();

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (Constants.go == 1 && gps.getSpeed() >= Constants.AVGSPEED && Constants.HALT == 0) {
                Constants.sensorWorking = 1;
                if(mGeomagnetic != null)
                    SensorManager.getRotationMatrix(R, I, event.values, mGeomagnetic);

                timestamp = System.nanoTime();

                // Find the sample period (between updates).
                // Convert from nanoseconds to seconds
                dt = 1 / (count / ((timestamp - timestampOld) / 1000000000.0f));
                count++;

                alpha = timeConstant / (timeConstant + dt);

                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                linearAcceleration[0] = event.values[0] - gravity[0];
                linearAcceleration[1] = event.values[1] - gravity[1];
                linearAcceleration[2] = event.values[2] - gravity[2];

                X = linearAcceleration[0];
                Y = linearAcceleration[1];
                Z = linearAcceleration[2];

                if (Constants.StopedMode == 1) {
                    if (Constants.HALT == 0) {
                        rawPavement = new Pavement(R, X, Y, Z);
                        dc.addData(rawPavement);
                    }
                } else {
                    rawPavement = new Pavement(R, X, Y, Z, gps.getLastLocation(), timeFormat.format(date));
                    dc.addData(rawPavement);
                }
            }else{
                Constants.actualColor = Constants.WHITE;

                Log.d("DataReader", "WHITE PELO SERVICE");
            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}

