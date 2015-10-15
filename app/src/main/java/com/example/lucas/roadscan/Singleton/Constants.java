package com.example.lucas.roadscan.Singleton;

/**
 * Created by lucas on 06/07/15.
 */
import java.util.ArrayList;

/**
 * Created by lucas on 24/06/15.
 */
public class Constants {

    public static int MINSPEED   = 20;
    public static int MINDIST    = 20;
    public static int defaultDBLIMIT = 128;
    public static int DBLIMIT   = 128;
    public static int DRSLEEP = 10000;
    public static int defaultDRSLEEP = 5000;
    public static int Hertz = 50;
    public static int defaultRAWLIMIT = 128;
    public static int WINDOW = 128;
    public static String SERVERIP = "200.239.153.214";
    public static String URLADD = "http://" + SERVERIP + "/roadie/addarray.php";


    public static String deviceID;
    public static int SensorFrequency;
    public static int uploadKey = 0;
    public static int success = 0;
    public static int serviceRunning = 0;
    public static int DataReader = 0;
    public static int CheckConnection = 0;
    public static int CCSeconds = 5;
    public static int DBEmpty = 0;
    public static int minDistance = 100;
    public static int sensorChangedCount = 0;
    public static int treatedCount = 0;
    public static int tempSegmentSize = (int) Constants.minDistance / 20;

    public static final int PROCESS_OVER = 1;
    public static final int UPDATE_MEDIA = 2;
    public static final int UPDATE_STDV = 3;

}

