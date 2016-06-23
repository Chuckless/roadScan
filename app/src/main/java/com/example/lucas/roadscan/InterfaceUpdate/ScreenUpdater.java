package com.example.lucas.roadscan.InterfaceUpdate;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.lucas.roadscan.Singleton.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lucas on 07/07/15.
 */
public class ScreenUpdater extends AsyncTask<String, Void, String>{

    private Context context;
    private ProgressDialog progress;
    private ScreenUpdaterInterface sui;
    private int task;

    public ScreenUpdater(Context context, ScreenUpdaterInterface sui){
        this.sui = sui;
        this.context = context;
    }

    public void setTask(int task) {
        this.task = task;
    }

    @Override
    protected String doInBackground(String... params) {
        return params[0];
    }

    @Override
    protected void onPostExecute(String msg) {
        switch (task){
            case Constants.PROCESS_OVER:
                sui.Toaster(msg);
                break;
            case Constants.UPDATE_STDV:
                sui.updateDevTV(msg);
                break;
            case Constants.UPDATE_SPD:
                sui.updateSpeedTV(msg);
                break;
            case Constants.UPDATE_DIST:
                sui.updateDistTV(msg);
                break;
            case Constants.UPDATE_COUNT:
                sui.updateCountTV(msg);
                break;
            case Constants.UPDATE_UPLOADED:
                sui.updateUploadedTV(msg);
                break;
            case Constants.UPDATE_GPS:
                sui.updateGPSTV(msg);
                break;
            case Constants.UPDATE_RUN:
                sui.updateRunning(msg);
                break;
            case Constants.UPDATE_DEVMEAN:
                sui.updateDevMean(msg);
                break;
            case Constants.UPDATE_COLOR:
                sui.updateColor(Integer.parseInt(msg));
                break;
        }


    }
}
