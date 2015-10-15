package com.example.lucas.roadscan.Singleton;

/**
 * Created by lucas on 06/07/15.
 */

import android.util.Log;

import com.example.lucas.roadscan.Pavement;
import com.example.lucas.roadscan.PavementAux;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Created by lucas on 24/06/15.
 */
public class DataContainer {
    private static DataContainer singleton = null;
    //private ArrayList<PavementAux> rawData = new ArrayList<PavementAux>();
    private LinkedList<PavementAux> rawData = new LinkedList<PavementAux>();
    // private LinkedList<LinkedList<PavementAux>> splitedRawData = new LinkedList<LinkedList<PavementAux>>();


    protected DataContainer(){}

    public static DataContainer getInstance(){
        if(singleton == null){
            singleton = new DataContainer();
        }
        return singleton;
    }

    /*public LinkedList<PavementAux> getSplitedRawData() {
        //Log.d("DataContainer", "Size: " + rawData.size());
        //return (LinkedList<PavementAux>) rawData.clone();
        if(splitedRawData.size() > 0)
            return splitedRawData.pollFirst();
        return null;
    }*/

    public PavementAux getRawData(){
        return rawData.pollFirst();
    }

    public int getCount(){
        return rawData.size();
    }

    public ArrayList<PavementAux> getFirsts(int howMany){
        ArrayList<PavementAux> auxData = new ArrayList<PavementAux>();
        //Log.d("DataContainer", "Comecou copia");
        for(int i = 0 ; i < howMany ; i++){
            auxData.add(rawData.pollFirst());
        }
        //Log.d("DataContainer", "Terminou copia");
        return auxData;
    }

    public void addData(PavementAux rawPavement){
        //rawData.add(rawPavement);
        // Log.d("DataContainer", "ADICIONOU 1 "+rawData.size());
        rawData.addLast(rawPavement);
    }

    public void deleteFirst(){
        //Log.d("DataContainer", "REMOVEU 1 "+rawData.size());
        rawData.removeFirst();
    }

    public void deleteFirsts(int howMany) {
        if(howMany > rawData.size()) {
            howMany = rawData.size() - 1;
        }
        Log.d("DataContainer", ""+howMany + " RAW DELETED");
        for (int i = 0; i < howMany; i++) {
            rawData.removeFirst();
        }

        Log.d("DataContainer", "DC count: " + rawData.size());
    }
}