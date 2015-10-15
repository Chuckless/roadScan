package com.example.lucas.roadscan;

/**
 * Created by lucas on 06/07/15.
 */
import android.content.Context;
import android.location.Location;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdater;
import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdaterInterface;
import com.example.lucas.roadscan.Processing.DataTreatment;
import com.example.lucas.roadscan.Singleton.Constants;
import com.example.lucas.roadscan.Singleton.DataContainer;
import com.example.lucas.roadscan.Singleton.GPS;
import com.example.lucas.roadscan.Singleton.fileManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Handler;


/**
 * Created by lucas on 24/06/15.
 */
public class DataReader implements Runnable{
    DataContainer dc;
    DatabaseHandler db;
    CheckConnection cc;
    ClientServer cs;
    fileManager fm;
    ScreenUpdater su;
    GPS gps;
    Context context;
    DecimalFormat df;
    float distance;
    LinkedList<PavementAux> segment = new LinkedList<PavementAux>();
    LinkedList<PavementAux> smallSegment = new LinkedList<PavementAux>();
    LinkedList<Float> lastMeans = new LinkedList<Float>();
    int smallSegmentPosition = 0;
    int lastMeansPosition = 0;
    int trigger = 0;

    PavementAux pavAux;
    Handler handler;

    public DataReader(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        dc = DataContainer.getInstance();
        db = DatabaseHandler.getInstance(context);
        cc = CheckConnection.getInstance(context);
        fm = fileManager.getInstance();
        gps = GPS.getInstance(context);
        df = new DecimalFormat("0.0000");


        Location prev = new Location("");
        Location actual;
        actual = null;
        distance = 0;
        smallSegmentPosition = 0;
        lastMeansPosition = 0;
        trigger = 0;



        while (true) {
            if(gps.location != null) {
                if (dc.getCount() > 0) {
                    pavAux = dc.getRawData();
                    if (prev.getLatitude() != 0.0) {
                        pavAux = DataTreatment.virtualRotation(pavAux);
                        pavAux.setZ(Math.abs(pavAux.getZ()));



                        actual.setLatitude(pavAux.getLat());
                        actual.setLongitude(pavAux.getLong());

                        segment.add(pavAux);

                        smallSegment.add(smallSegmentPosition, pavAux);
                        smallSegmentPosition++;

                        if(smallSegment.size() == Constants.tempSegmentSize){
                            Log.d("DataReader", "Entrou!");

                            float mean = DataTreatment.getMean(smallSegment);

                            float stdv = DataTreatment.getVariance(mean, smallSegment);

                            try {
                                fm.writeToFile(pavAux, mean, stdv);
                                fm.writeToSQLFile(pavAux);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            lastMeans.add(mean);
                            smallSegment.clear();

                            if(lastMeans.size() > 1){
                                if(checkTrigger(lastMeans)){
                                    trigger++;
                                    if(trigger == 2){
                                        trigger = 0;
                                        Log.d("DataReader", "TRIGGER!");
                                    }
                                }else{
                                    trigger = 0;

                                }
                            }

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            smallSegmentPosition = 0;
                        }else{
                            try {
                                fm.writeToFile(pavAux);
                                fm.writeToSQLFile(pavAux);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                        //distance += prev.distanceTo(actual);
                        distance += 0.5;
                        Log.d("DataReader", "Z Value: " + pavAux.getZ());
                        Log.d("DataReader", "Distance: " + distance);



                        if(distance >= Constants.minDistance) {
                            distance = 0;

                            //DataTreatment.getMinsMaxs(segment);

                            float mean = DataTreatment.getMean(segment);
                            su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                            su.setTask(Constants.UPDATE_MEDIA);
                            su.execute(""+df.format(mean));


                            float dev = DataTreatment.getVariance(mean, segment);
                            su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                            su.setTask(Constants.UPDATE_STDV);
                            su.execute(""+df.format(dev));

                            segment.clear();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        prev = actual;
                    } else {

                        prev.setLatitude(pavAux.getLat());
                        prev.setLongitude(pavAux.getLong());

                        segment.add(pavAux);

                        actual = new Location("");
                    }
                }

            }



            if (Constants.success == 1) {
                toDelete();
                Constants.success = 0;
            }

           /* if(dc.getRawData().size() > 3 * Constants.RAWLIMIT){
                Constants.RAWLIMIT = 2 * Constants.RAWLIMIT;
            }*/

            if (cc.isHaveConnectedMobile() || cc.isHaveConnectedWifi()) {

                if (db.getPavementCount() >= 3 * Constants.DBLIMIT) {
                    if(Constants.uploadKey == 0) {
                        Constants.DBLIMIT = 2 * Constants.DBLIMIT;
                    }
                }

                if (db.getPavementCount() >= Constants.DBLIMIT) {
                    // toUpload(Constants.DBLIMIT);
                }

                if (db.getPavementCount() < Constants.DBLIMIT && Constants.serviceRunning == 0) {
                    //toUpload(db.getPavementCount());
                    Constants.DBLIMIT = Constants.defaultDBLIMIT;
                }


            }

            if (dc.getCount() == 0 && Constants.serviceRunning == 0) {
                if(!(db.getPavementCount() == 0)) {
                    Constants.DRSLEEP = 1000;
                    Log.d("DataReader", "Serviço atualizando a cada " + (Constants.DRSLEEP / 1000) + " segundos");
                }else{
                    if(Constants.DRSLEEP != 5000) {
                        su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                        su.setTask(Constants.PROCESS_OVER);
                        su.execute("The processing is over");
                    }
                    Constants.DRSLEEP = 5000;
                    Log.d("DataReader", "Serviço atualizando a cada " + (Constants.DRSLEEP / 1000) + " segundos");

                }
            }else{
                Constants.DRSLEEP = 0;
            }

            try {
                Thread.sleep(Constants.DRSLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

    private Boolean checkTrigger(LinkedList<Float> lastMeans) {
        float mean1, mean2;
        mean2 = lastMeans.getLast();
        mean1 = lastMeans.get(lastMeans.indexOf(lastMeans.getLast()) - 1);

        if(Math.abs(mean2 - mean1) > Math.abs(1.5 * mean1) ){
            return true;
        }else{
            return false;
        }
    }


    private void toDelete() {
        if(db.getPavementCount() < Constants.DBLIMIT){
            Log.d("DataReader", "Removendo " + db.getPavementCount() + " linhas do DB. Restante: 0");
            db.removeRow(db.getPavementCount());
            Constants.DBLIMIT = Constants.defaultDBLIMIT;
            Constants.DBEmpty = 1;
        }else{
            Log.d("DataReader", "Removendo " + Constants.DBLIMIT + " linhas do DB. Restante: " + (db.getPavementCount() - Constants.DBLIMIT));
            db.removeRow(Constants.DBLIMIT);
            Constants.DBEmpty = 0;
        }
    }

    private void toUpload(int howMany) {
        try {
            if (Constants.uploadKey == 0) {
                Constants.uploadKey = 1;
                JSONArray jsonObj;
                jsonObj = db.getMultiplePavements(howMany);

                cs = new ClientServer(jsonObj, howMany, context);
                cs.execute();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
