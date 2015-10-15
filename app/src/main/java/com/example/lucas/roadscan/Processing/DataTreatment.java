package com.example.lucas.roadscan.Processing;


import android.util.Log;

import com.example.lucas.roadscan.Pavement;
import com.example.lucas.roadscan.PavementAux;
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
    private static ArrayList <Float> maxs;
    private static ArrayList <Float> mins;

    public static ArrayList<Float> getMaxs() {
        return maxs;
    }

    public static ArrayList<Float> getMins() {
        return mins;
    }

    public static PavementAux virtualRotation(PavementAux pavement) {

        R = pavement.getR();
        oldX = pavement.getX();
        oldY = pavement.getY();
        oldZ = pavement.getZ();

        newX = R[0] * oldX + R[1] * oldY + R[2] * oldZ;
        newY = R[3] * oldX + R[4] * oldY + R[5] * oldZ;
        newZ = R[6] * oldX + R[7] * oldY + R[8] * oldZ;

        pavement.setX(newX);
        pavement.setY(newY);
        pavement.setZ(newZ);

        return pavement;
    }

    public static float getVariance(float mean, LinkedList<PavementAux> list){

        float temp = 0;
        for(PavementAux p : list){
            float z = p.getZ();
            temp += (mean-z)*(mean-z);
        }

        Log.d("DataTreatment", "Variancia: " + temp / list.size());
        Log.d("DataTreatment", ".");
        Log.d("DataTreatment", ".");
        return temp/list.size();
    }

    public static float  getMean(LinkedList<PavementAux> list){
        float sum = 0;

        Log.d("DataTreatment", "ListSize: " + list.size());

        for(PavementAux p : list){
            sum += Math.abs(p.getZ());
//            sum += p.getZ();
        }

        if(list.size() == Constants.tempSegmentSize){
            Log.d("DataTreatment", "Media: " + sum / list.size());
        }
        else{
            Log.d("DataTreatment", "MediaGrande: " + sum / list.size());
        }
        return sum/list.size();
    }

    public static ArrayList<Float> getMinsMaxs(LinkedList<PavementAux> list) {
        maxs = new ArrayList<Float>();
        mins = new ArrayList<Float>();
        float min, max;
        int m = 0;
        min = list.getFirst().getZ();
        max = list.getFirst().getZ();

        if (max > min)
            m = 1;
        else
            m = -1;
        for(int i = 0 ; i < list.size() ; i ++){
            if (m == 1) {

            }else{

            }

        }


        return null;
    }
}


