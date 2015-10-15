package com.example.lucas.roadscan;

/**
 * Created by lucas on 06/07/15.
 */


        import java.io.IOException;
        import java.text.DecimalFormat;
        import java.util.ArrayList;

        import android.app.Service;
        import android.content.Intent;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.IBinder;
        import android.text.format.Time;
        import android.widget.Toast;

        import com.example.lucas.roadscan.Singleton.Constants;
        import com.example.lucas.roadscan.Singleton.DataContainer;
        import com.example.lucas.roadscan.Singleton.GPS;

public class MyService extends Service implements SensorEventListener{



    private float X, Y, Z;
    private float X1, Y1, Z1;
    private float Xaux, Yaux, Zaux;
    private float speed, distance;
    private boolean prevGPSStatus = true;
    private float[] gravity = {0,0,0} ,  linear_acceleration = {0,0,0};
    private PavementAux rawPavement;
    private DataContainer dc;
    //private float[] window = new float[MainActivity.WSIZE];
    private ArrayList<Float> window = new ArrayList<Float>();
    private double latitude, longitude, IRI;
    private float R[] = new float[9];
    private float I[] = new float[9];
    float[] mGravity;
    float[] mGeomagnetic;
    float[] mLinear;
    double azimuth, roll, pitch; // View to draw a compass
    private int cont = 0, trigger = 0;
    static int uploadKey = 0;

    private SensorManager sensorManager;
    private Sensor sensorAcc, sensorMag, sensorLin;
    private GPS gps;
    public static DatabaseHandler db;
    private CheckConnection check;
    private Time now;
    DecimalFormat df;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        now 	= new Time();
        dc = DataContainer.getInstance();
        df = new DecimalFormat("0.00000");
        gps = GPS.getInstance(this);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        sensorAcc   = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMag   = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorLin   = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sensorManager.registerListener((SensorEventListener) this, sensorAcc, Constants.SensorFrequency);
        sensorManager.registerListener((SensorEventListener) this, sensorMag, Constants.SensorFrequency);
        sensorManager.registerListener((SensorEventListener) this, sensorLin, Constants.SensorFrequency);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sensorManager != null)
            sensorManager.unregisterListener(this);

        //gps.stopUsingGPS();
        //db.closeDB();

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }

        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                if(gps.location != null) {
                    if(dc.getCount() <= 10) {
                        SensorManager.getRotationMatrix(R, I, event.values, mGeomagnetic);

                        X = event.values[0];
                        Y = event.values[1];
                        Z = event.values[2];

                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();

                        now.setToNow();
                        String time = now.format("%H%M%S");

                        rawPavement = new PavementAux(R, X, Y, Z, latitude, longitude, time, gps.getDistance());
                        dc.addData(rawPavement);
                    }
                /*if(Constants.treatedCount > 0){
                    Constants.treatedCount--;
                    dc.deleteFirst();

                }*/
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

