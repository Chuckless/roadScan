package com.example.lucas.roadscan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.lucas.roadscan.Singleton.GPS;

/**
 * Created by lucas on 11/03/16.
 */
public class gpsBroadcastReceiver extends BroadcastReceiver{


    private GPS gps;

    @Override
    public void onReceive(Context context, Intent intent) {
        gps = GPS.getInstance();
        gps.getLocation();
    }
}
