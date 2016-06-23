package com.example.lucas.roadscan.Singleton;

/**
 * Created by lucas on 06/07/15.
 */
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by lucas on 24/06/15.
 */
public class Constants {

    public static int AVGSPEED   = 20;
    public static int defaultDBLIMIT = 1;
    public static int DBLIMIT   = 1;
    public static int DRSLEEP = 200000;
    public static int Hertz = 50;

    public static String SERVERIP = "200.239.153.214";
    public static String URLADD = "http://" + SERVERIP + "/roadie/addarray.php";
    public static int DROPTIMES = 5;
    public static String TXTHint = "";
    public static int go = 0;
    public static int StopedMode = 0;
    public static Context mContext;


    public static String deviceID;
    public static int SensorFrequency;
    public static int showValues = 0;
    public static int sensorWorking = 0;
    public static int uploadKey = 0;
    public static int success = 0;
    public static int serviceRunning = 0;
    public static int DataReader = 0;
    public static int CheckConnection = 0;
    public static int CCSeconds = 1;
    public static int DBEmpty = 0;
    public static int minDistance = 1;
    public static int peaksCtrl = 3;
    public static int GPSchangesCount = 0;
    public static int HALT = 0;
    public static int timeOuts = 0;
    public static double peakFactor = 1.5;
    public static double lowLimit = 0.8;
    public static int actualColor = 0xFFFFFFFF;
    public static int delayTime = 1;

    public static final int PROCESS_OVER = 1;
    public static final int UPDATE_MEDIA = 2;
    public static final int UPDATE_STDV = 3;
    public static final int UPDATE_SPD = 4;
    public static final int UPDATE_DIST = 5;
    public static final int UPDATE_COUNT = 6;
    public static final int UPDATE_UPLOADED = 7;
    public static final int UPDATE_LAT = 8;
    public static final int UPDATE_LNG = 9;
    public static final int UPDATE_GPS = 10;
    public static final int UPDATE_RUN = 11;
    public static final int UPDATE_DEVMEAN = 12;
    public static final int UPDATE_COLOR = 13;

    public static final int WHITE = 0xFFFFFFFF;
    public static final int GREEN = 0xFFA2FF29;
    public static final int ORANGE = 0xFFFFA930;
    public static final int YELLOW = 0xFFFFFA1F;
    public static final int RED = 0xFFFF353C;
    public static final int BLACK = 0xFF464646;

}

