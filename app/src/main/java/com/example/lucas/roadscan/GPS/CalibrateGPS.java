package com.example.lucas.roadscan.GPS;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lucas.roadscan.Singleton.GPS;

import java.util.Calendar;

/**
 * Created by lucas on 07/07/15.
 */
public class CalibrateGPS extends AsyncTask<Context, String, Void> {

    private ProgressDialog progress;
    private Context context;
    private GPS gps;
    private int decrement = 1000;
    private int waitTime = 16000;

    public CalibrateGPS(Context context){
        this.context = context;
        gps = GPS.getInstance(context);
    }

    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(context);
        progress.setMessage("Calibrating GPS...");
        progress.show();
    }

    @Override
    protected Void doInBackground(Context... params) {
        Long t = Calendar.getInstance().getTimeInMillis();
        while (gps.getLatitude() == 0 && Calendar.getInstance().getTimeInMillis() - t < 16000) {
            try {
                publishProgress("Calibrating GPS... Please wait (" + (waitTime/1000) + ")");
                waitTime = waitTime - decrement;
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        return null;
    }


    @Override
    protected void onProgressUpdate(String... params) {
        progress.setMessage(params[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(progress.isShowing()){
           progress.dismiss();
        }
    }
}
