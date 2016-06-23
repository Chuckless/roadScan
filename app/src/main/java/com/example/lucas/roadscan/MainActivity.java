package com.example.lucas.roadscan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.os.*;
import android.provider.Settings.Secure;

import android.util.Log;
import android.util.TimeFormatException;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.roadscan.GPS.CalibrateGPS;

import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdaterInterface;
import com.example.lucas.roadscan.Singleton.Constants;
import com.example.lucas.roadscan.Singleton.DataContainer;
import com.example.lucas.roadscan.Singleton.GPS;
import com.example.lucas.roadscan.Singleton.fileManager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity implements ScreenUpdaterInterface {

    static TextView textAvgspeed, textSpeed, textDist, textCount, textUploaded, textDev, textGPS, textRunning, textDevMean;
    static CheckBox checkTXT, checkUpload;

    private GPS gps = null;
    private CalibrateGPS cGPS;
    private cloudService cs = null;
    private DataContainer dc = null;
    private DatabaseHandler db = null;
    private LinearLayout colorSquare = null;

    private DataReader dr;

    private Handler dotsHandler;
    private Runnable r;


    static EditText editAvgSpeed;
    static Switch mySwitch;
    static Button btnChange, btnDelete;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants.deviceID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        colorSquare = (LinearLayout) findViewById(R.id.colorLayout);

        Constants.mContext = MainActivity.this;

        gps = GPS.getInstance(MainActivity.this);
        cs = new cloudService(MainActivity.this);

        if(!gps.isGPSEnabled)
            gps.buildAlertMessageNoGps();
        else{
            cGPS = new CalibrateGPS(MainActivity.this);
            cGPS.execute();
        }

        Constants.SensorFrequency = (1/Constants.Hertz)*1000000;

        mySwitch    = (Switch)   findViewById(R.id.switch1);
        textAvgspeed= (TextView) findViewById(R.id.textAvgspeed);
        textRunning = (TextView) findViewById(R.id.textRunning);

        editAvgSpeed= (EditText) findViewById(R.id.editSpd);

        btnChange   = (Button)   findViewById(R.id.changeBtn);
        btnDelete   = (Button)   findViewById(R.id.btnDelete);

        checkTXT    = (CheckBox) findViewById(R.id.check_txt);
        checkUpload = (CheckBox) findViewById(R.id.check_upload);

        textSpeed 		= (TextView) findViewById(R.id.textSpeed);
        textDist 		= (TextView) findViewById(R.id.textDist);
        textCount       = (TextView) findViewById(R.id.textCount);
        textUploaded    = (TextView) findViewById(R.id.textUploaded);
        textDev         = (TextView) findViewById(R.id.textDev);
        textGPS         = (TextView) findViewById(R.id.textGPS);
        textDevMean     = (TextView) findViewById(R.id.textDevMean);


        textAvgspeed.  setVisibility(View.INVISIBLE);
        editAvgSpeed.  setVisibility(View.INVISIBLE);

        checkTXT.      setVisibility(View.INVISIBLE);
        checkUpload.   setVisibility(View.INVISIBLE);

        btnChange.     setVisibility(View.INVISIBLE);
        btnDelete.     setVisibility(View.INVISIBLE);

        textSpeed.     setVisibility(View.INVISIBLE);
        textDist.      setVisibility(View.INVISIBLE);

        textCount.     setVisibility(View.INVISIBLE);
        textUploaded.  setVisibility(View.INVISIBLE);

        textDev.       setVisibility(View.INVISIBLE);
        textDevMean.   setVisibility(View.INVISIBLE);

        textGPS.       setVisibility(View.INVISIBLE);


        editAvgSpeed.   setText("" + Constants.AVGSPEED);

        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                db = DatabaseHandler.getInstance(MainActivity.this);
                db.clearDB();
                Constants.DBEmpty = 1;
                Constants.DBLIMIT = Constants.defaultDBLIMIT;
                updateCountTV("" + db.getPavementCount());
            }
        });

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    Constants.showValues = 1;

                    colorSquare.setBackgroundColor(0xFFFFFFFF);
                    colorSquare.invalidate();

                    textAvgspeed.  setVisibility(View.VISIBLE);
                    editAvgSpeed.  setVisibility(View.VISIBLE);

                    checkTXT.      setVisibility(View.VISIBLE);
                    checkUpload.   setVisibility(View.VISIBLE);

                    btnChange.     setVisibility(View.VISIBLE);
                    btnDelete.     setVisibility(View.VISIBLE);

                    textDist.       setVisibility(View.VISIBLE);
                    textSpeed.      setVisibility(View.VISIBLE);

                    textCount.      setVisibility(View.VISIBLE);
                    textUploaded.   setVisibility(View.VISIBLE);

                    textDev.        setVisibility(View.VISIBLE);

                    textGPS.        setVisibility(View.VISIBLE);
                    textDevMean.   setVisibility(View.VISIBLE);

                } else {
                    Constants.showValues = 0;

                    textAvgspeed.  setVisibility(View.INVISIBLE);
                    editAvgSpeed.  setVisibility(View.INVISIBLE);

                    checkTXT.      setVisibility(View.INVISIBLE);
                    checkUpload.   setVisibility(View.INVISIBLE);

                    btnChange.     setVisibility(View.INVISIBLE);
                    btnDelete.     setVisibility(View.INVISIBLE);

                    textSpeed.     setVisibility(View.INVISIBLE);
                    textDist.      setVisibility(View.INVISIBLE);

                    textCount.     setVisibility(View.INVISIBLE);
                    textUploaded.   setVisibility(View.INVISIBLE);

                    textDev.       setVisibility(View.INVISIBLE);
                    textDevMean.   setVisibility(View.INVISIBLE);

                    textGPS.       setVisibility(View.INVISIBLE);

                }
            }
        });


        btnChange.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Constants.AVGSPEED = Integer.parseInt(editAvgSpeed.getText().toString());

                Constants.URLADD = "http://" + Constants.SERVERIP + "/roadie/addarray.php";

                editAvgSpeed.setText("" + Constants.AVGSPEED);

                Toast.makeText(MainActivity.this, "Values changed", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }



    // Method to start the service
    public void startService(View view) {
        // if(!gps.isGPSEnabled) {
        //gps.buildAlertMessageNoGps();
        //} else {
        if (Constants.serviceRunning == 0) {
            Constants.DRSLEEP = 10;
            Constants.timeOuts = 0;

            gps = GPS.getInstance(MainActivity.this);

            if (!gps.isGPSEnabled) {
                gps.buildAlertMessageNoGps();
            } else {

                Constants.serviceRunning = 1;
                Constants.HALT = 0;
                startService(new Intent(getBaseContext(), MyService.class));

                dr = new DataReader(MainActivity.this);


                Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

                dotsHandler = new Handler();
                r = new Runnable() {
                    int dotcount = 0;

                    @Override
                    public void run() {

                        dotsHandler.postDelayed(this, Constants.CCSeconds * 1000);
                        if (dotcount == 0) {
                            textRunning.setText("Service running.");
                        } else if (dotcount == 1) {
                            textRunning.setText("Service running..");
                        } else if (dotcount == 2) {
                            textRunning.setText("Service running...");
                        } else if (dotcount == 3) {
                            textRunning.setText("Service running....");
                        }

                        dotcount++;
                        if (dotcount == 4) {
                            dotcount = 0;
                        }

                    }
                };
                dotsHandler.postDelayed(r, 1000);
            }

        } else {
            Toast.makeText(this, "Service's already running!", Toast.LENGTH_SHORT).show();
        }

    }
    //}

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopService(new Intent(getBaseContext(), MyService.class));
        Constants.serviceRunning = 0;

        terminateServices();

        if(dr != null){
            dr.stopReading();
            dr = null;
        }


    }

    // Method to stop the service
    public void stopService(View view) {
        dc = DataContainer.getInstance();

        if(Constants.serviceRunning == 1) {
            if(Constants.HALT == 0) { //Faz o sensor parar de coletar valores e pede para usuario tentar novamente em poucos segundos
                Constants.HALT = 1;
                if(Constants.StopedMode == 1){

                    updateColor(0xFFFFFFFF);
                    Constants.serviceRunning = 0;
                }
            }
            if(dc.getCount() <= 1) { //Quando nao tiver mais nada pra ser processado, encerra atividades

                stopService(new Intent(getBaseContext(), MyService.class));
                Constants.serviceRunning = 0;

                terminateServices();

                Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
                textRunning.setText("Service stopped.");

            }else{
                //Log.d("roadieMain", "dcCount: " + dc.getCount());
                Toast.makeText(this, "Try again in a few seconds", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(MainActivity.this, "Service's not running!", Toast.LENGTH_LONG).show();
        }

    }

    private void terminateServices() {
        if(gps != null) {
            gps.stopUsingGPS();
            gps = null;
        }

        if(dotsHandler != null) {
            dotsHandler.removeCallbacks(r);
            dotsHandler = null;
        }

        //cs.toUpload(db.getPavementCount());

    }

    @Override
    public void Toaster(String string) {
        Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void updateDistTV(String string) {textDist.setText("Dist: " + string); }

    @Override
    public void updateSpeedTV(String string) {textSpeed.setText("Speed: " + string); }

    @Override
    public void updateCountTV(String string) {textCount.setText("Container: " + string); }

    @Override
    public void updateUploadedTV(String string) {textUploaded.setText("Processed: " + string); }

    @Override
    public void updateDevTV(String string) { textDev.setText("Stdv: " + string); }

    @Override
    public void updateGPSTV( String string ) { textGPS.setText("Status: " + string); }

    @Override
    public void updateRunning (String string ) {textRunning.setText("Service running" + string);}

    @Override
    public void updateDevMean(String string) {textDevMean.setText("Stdv Mean: " + string);}

    @Override
    public void updateColor(int color) {
        colorSquare.setBackgroundColor(color);
        colorSquare.invalidate();
    }


}