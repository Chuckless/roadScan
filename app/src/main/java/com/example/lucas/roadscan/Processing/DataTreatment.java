package com.example.lucas.roadscan.Processing;


import android.location.Location;
import android.util.Log;

import com.example.lucas.roadscan.Pavement;
import com.example.lucas.roadscan.Singleton.Constants;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by lucas on 24/06/15.
 */
public class DataTreatment {
    static float newX, newY, newZ;
    static float oldX, oldY, oldZ;
    static float R[] = new float[9];


    public static Pavement virtualRotation(Pavement pavement) {

        R = pavement.getR();
        oldX = pavement.getX();
        oldY = pavement.getY();
        oldZ = pavement.getZ();

        newX = R[0] * oldX + R[1] * oldY + R[2] * oldZ;
        newY = R[3] * oldX + R[4] * oldY + R[5] * oldZ;
        newZ = R[6] * oldX + R[7] * oldY + R[8] * oldZ;

        Log.d("DataTreatment", "X: " + newX + " Y: " + newY + " Z: " + newZ);

        pavement.setX(newX);
        pavement.setY(newY);
        pavement.setZ(newZ);

        /*Log.d("DataTreatment", "X: " + oldX + " Y: " + oldY + " Z: " + oldZ);

        pavement.setX(oldX);
        pavement.setY(oldY);
        pavement.setZ(oldZ);*/

        return pavement;
    }

    public static float getDeviation(float mean, LinkedList<Pavement> list){

        float temp = 0;

        Log.d("DataTreatment", "DMedia: " + mean);
        Log.d("DataTreatment", "DListSize: " + list.size());
        for(Pavement p : list){
            Log.d("DataTreatment", "Z: " + p.getZ());
            temp = temp + ((p.getZ())-mean)*((p.getZ())-mean);
        }

        Log.d("DataTreatment", "DSoma: " + temp);
        temp = temp/(list.size()-1);
        Log.d("DataTreatment", "DDividido: " + temp);
        temp = (float)Math.sqrt(temp);

        Log.d("DataTreatment", "DDesvio: " + temp);
        return temp;
    }

    public static float getSpeedMean(LinkedList<Pavement> list){
        float sum = 0;
        for(Pavement p : list){
            Log.d("DataTreatment", "SPEED" + p.getSpeed());
            sum = sum + p.getSpeed();
        }

        Log.d("DataTreatment" , "SOMA: " + sum);
        Log.d("DataTreatment", "SIZE: " + list.size());
        Log.d("DataTreatment" , "MEDIA: " + sum/list.size());

        float mean = sum/list.size();
        return mean;
    }

    public static float  getMean(LinkedList<Pavement> list){
        float sum = 0;

        Log.d("DataTreatment", "ListSize: " + list.size());

        for(Pavement p : list){
            sum += p.getZ();
        }

        Log.d("DataTreatment", "Media dos Zs: " + sum/list.size());
        return sum/list.size();
    }

    public static float  getMeanFloat(LinkedList<Float> list){
        float sum = 0;

        //Log.d("DataTreatment", "ListSize: " + list.size());

        for(Float f : list){
            sum += f;
        }

        float mean = sum/list.size();

        return mean;
    }

    public static float  getPMeanStdv(LinkedList<Pavement> list){
        float sum = 0;

        //Log.d("DataTreatment", "ListSize: " + list.size());

        for(Pavement p : list){
            sum += p.getStdv();
        }

        float mean = sum/list.size();

        return mean;
    }

}


