package com.example.lucas.roadscan;

/**
 * Created by lucas on 06/07/15.
 */
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdater;
import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdaterInterface;
import com.example.lucas.roadscan.Processing.DataTreatment;
import com.example.lucas.roadscan.Singleton.Constants;
import com.example.lucas.roadscan.Singleton.DataContainer;
import com.example.lucas.roadscan.Singleton.fileManager;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import android.os.Handler;


/**
 * Created by lucas on 24/06/15.
 */
public class DataReader{
    private DataContainer dc = null;
    private DatabaseHandler db = null;

    public fileManager fm;
    public ScreenUpdater su;
    public Context context;
    public DecimalFormat df;
    public Pavement pavAux, repPavement;
    float  stdv, mean, spdmean, stdvMean, allStdvMean, hPeaksMean, lPeaksMean, magnitude;
    float lastStdv = 0;
    Location prev, actual;
    Handler h;

    float distance, totalDistance, distanceAcc;
    int databaseSize, usingBuffer, peaksctrl,actualDcCount = 0,lastDcCount = 0, nextIsBlack = 0;
    int speedCtrl = 10, sameDcCount = 0;
    static LinkedList<Pavement> segment = new LinkedList<>();
    static LinkedList<Pavement> pavBuffer = new LinkedList<>();
    static LinkedList<LinkedList<Float>> lastStdvs = new LinkedList<>();
    static LinkedList<Float> allLastStdv = new LinkedList<>();
    static LinkedList<Float> highPeaks = new LinkedList<>();
    static LinkedList<Float> lowPeaks = new LinkedList<>();

    int processed;

    public DataReader(Context context) {
        this.context = context;

        dc = DataContainer.getInstance();
        db = DatabaseHandler.getInstance(context);
        fm = fileManager.getInstance();
        df = new DecimalFormat("0.0000");

        prev = new Location("");
        prev.setLatitude(0.0);

        distance = 0;
        totalDistance = 0;
        processed = 0;
        databaseSize = 0;
        usingBuffer = 0;
        peaksctrl = 0;

        for (int i = 0; i < speedCtrl; i++) {
            lastStdvs.addLast(new LinkedList<Float>());
        }


        h = new Handler();
        h.post(r);

    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            if (dc.getCount() > 1) {  //Check if container is not-empty (has something to read)
                Log.d("DataContainer", "coisas a ser lida");
                sameDcCount = 0;
                pavAux = dc.getRawData(); //Get the first pavement from the list

                if(dc.getCount() > 5){
                    Constants.delayTime = 1;
                }

                if (pavAux != null) {
                    processed++;

                    if (processed % 20 == 0 || processed < 50) {
                        su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                        su.setTask(Constants.UPDATE_UPLOADED);
                        su.execute("" + processed);
                    }

                    pavAux = DataTreatment.virtualRotation(pavAux); //Apply virtual rotation
                    //pavAux.setZ(Math.abs(pavAux.getZ())); //Set to absolute value


                    if (prev.getLatitude() == 0.0) { //First case scenarium

                        Log.d("DataReader", "First Case scenarium");
                        prev = pavAux.getLocation();
                        pavAux.setDistance(0);
                        segment.addLast(pavAux);   //Add first pavement to the "segment"
                        lastDcCount = dc.getCount();

                    } else { //Not-first case scenarium

                        actual = pavAux.getLocation();
                        distance = prev.distanceTo(actual);
                        prev = actual;

                        totalDistance += distance;
                        distanceAcc += distance;

                        pavAux.setDistance(totalDistance);
                        segment.addLast(pavAux);    //Add not-first pavement to the "segment"

                        if (distanceAcc >= Constants.minDistance) {
                            distanceAcc = 0;

                            mean = DataTreatment.getMean(segment);
                            stdv = DataTreatment.getDeviation(mean, segment);
                            spdmean = DataTreatment.getSpeedMean(segment);

                            repPavement = new Pavement(pavAux, stdv, spdmean, 1);

                            allLastStdv.addLast(stdv);
                            allStdvMean = DataTreatment.getMeanFloat(allLastStdv);

                            if (allLastStdv.size() >= 4) {
                                allLastStdv.pollFirst();

                                if(spdmean > 70){
                                    Constants.lowLimit = 1.2;
                                }else{
                                    Constants.lowLimit = 0.8;
                                }

                                if (allStdvMean > 3.0) { //PAVIMENTO HORRIVEL

                                    checkBuffer();
                                    repPavement.setFlag(3);

                                    Log.d("DataReader", "BAD > 3, valor: " + allStdvMean);

                                    Constants.actualColor = Constants.RED;

                                    su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                                    su.setTask(Constants.UPDATE_GPS);
                                    su.execute("TERRIBLE ROAD");

                                    db.addPavement(repPavement);

                                } else if (allStdvMean <= Constants.lowLimit) { //PAVIMENTO PERFEITO

                                    checkBuffer();

                                    //Constants.peakFactor = 2.0;
                                    repPavement.setFlag(0);

                                    su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                                    su.setTask(Constants.UPDATE_GPS);
                                    su.execute("SMOOTH ROAD");

                                    Constants.actualColor = Constants.GREEN;

                                    db.addPavement(repPavement);

                                } else {

                                    if (stdv >= lastStdv) {
                                        if (highPeaks.size() >= 4) {

                                            hPeaksMean = DataTreatment.getMeanFloat(highPeaks);

                                            highPeaks.pollFirst();
                                        }
                                        highPeaks.addLast(stdv);

                                    } else {
                                        if (lowPeaks.size() >= 4) {
                                            lPeaksMean = DataTreatment.getMeanFloat(lowPeaks);

                                            lowPeaks.pollFirst();
                                        }
                                        lowPeaks.addLast(stdv);
                                    }

                                    lastStdv = stdv;

                                    if (hPeaksMean - lPeaksMean >= 2) {  //PAVIMENTO HORRIVEL
                                        checkBuffer();
                                        repPavement.setFlag(3);

                                        Log.d("DataReader", "h-p > 1.6, valor: " + (hPeaksMean - lPeaksMean));

                                        Constants.actualColor = Constants.RED;

                                        su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                                        su.setTask(Constants.UPDATE_GPS);
                                        su.execute("TERRIBLE ROAD");

                                        db.addPavement(repPavement);

                                    }else{

                                        if (spdmean <= 20) {
                                            speedCtrl = 0;
                                        } else if (spdmean > 20 && spdmean <= 30) {
                                            speedCtrl = 1;
                                        } else if (spdmean > 30 && spdmean <= 40) {
                                            speedCtrl = 2;
                                        } else if (spdmean > 40 && spdmean <= 50) {
                                            speedCtrl = 3;
                                        } else if (spdmean > 50 && spdmean <= 60) {
                                            speedCtrl = 4;
                                        } else if (spdmean > 60 && spdmean <= 70) {
                                            speedCtrl = 5;
                                        } else if (spdmean > 70 && spdmean <= 80) {
                                            speedCtrl = 6;
                                        } else if (spdmean > 80 && spdmean <= 90) {
                                            speedCtrl = 7;
                                        } else if (spdmean > 90 && spdmean <= 100) {
                                            speedCtrl = 8;
                                        } else if (spdmean > 100) {
                                            speedCtrl = 9;
                                        }


                                        if (lastStdvs.get(speedCtrl).size() >= 7) { //JA POSSUI LEITURAS SUFICIENTES PARA OBTER MÉDIA

                                            stdvMean = DataTreatment.getMeanFloat(lastStdvs.get(speedCtrl));
                                            su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                                            su.setTask(Constants.UPDATE_GPS);
                                            su.execute("");

                                            magnitude = stdv / stdvMean;
                                            Log.d("DataReader", "Magnitude: " + magnitude);
                                            repPavement.setMagnitude(magnitude);

                                            if (magnitude >= 1.5) { //PICO ENCONTRADO
                                                repPavement.setFlag(2);

                                                su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                                                su.setTask(Constants.UPDATE_GPS);
                                                su.execute("PEAK DETECTED!");

                                                Log.d("DataReader", "PEAK DETECTED: " + magnitude);

                                                pavBuffer.addLast(repPavement);
                                                peaksctrl = Constants.peaksCtrl;

                                            } else { //NAO PICO
                                                //Constants.peakFactor = 1.5;
                                                repPavement.setFlag(1);

                                                lastStdvs.get(speedCtrl).pollFirst();
                                                lastStdvs.get(speedCtrl).addLast(stdv);

                                                if (peaksctrl == 0) { //SEM PICOS RECENTES, TRECHO BOM

                                                    db.addPavement(repPavement);

                                                } else if (peaksctrl > 0) { //DENTRO DE ANALISE DE TRECHO
                                                    pavBuffer.addLast(repPavement);
                                                    peaksctrl--;

                                                    if (peaksctrl == 0) { //FIM DE TRECHO RUIM OU APENAS PICO SOZINHO
                                                        checkBuffer();
                                                    }
                                                }
                                            }

                                            if(pavBuffer.size() > Constants.peaksCtrl + 1){
                                                su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                                                su.setTask(Constants.UPDATE_GPS);
                                                su.execute("BAD ROAD");

                                                Constants.actualColor = Constants.ORANGE;
                                            }else if(nextIsBlack == 0){
                                                su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                                                su.setTask(Constants.UPDATE_GPS);
                                                su.execute("NORMAL ROAD");

                                                Constants.actualColor = Constants.YELLOW;
                                            }
                                            nextIsBlack = 0;

                                            try {
                                                if (MainActivity.checkTXT.isChecked()) {
                                                    Log.d("DataReader", "Escrevendo em arquivo");
                                                    fm.writeToFile(repPavement);
                                                    fm.writeToSQLFile(repPavement);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        } else { //PRIMEIRAS LEITURAS - AINDA NAO POSSUI MÉDIA A SER COMPARADA
                                            lastStdvs.get(speedCtrl).addLast(stdv);
                                            checkBuffer();

                                            Constants.actualColor = Constants.WHITE;
                                            Log.d("DataReader", "WHITE FALTA DE LEITURA");

                                            su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                                            su.setTask(Constants.UPDATE_GPS);
                                            su.execute("GATHERING NEW SPEED INFO");
                                        }
                                    }
                                }
                            }
                            segment.clear();
                        }

                        if (Constants.showValues == 1) {
                            su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                            su.setTask(Constants.UPDATE_DEVMEAN);
                            su.execute("" + df.format(allStdvMean));

                            su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                            su.setTask(Constants.UPDATE_STDV);
                            su.execute("" + df.format(stdv));

                        }

                    }

                }
            } else{
                Log.d("DataContainer", "nada a ser lido");
                sameDcCount++;
                if(sameDcCount > 2 && sameDcCount < 100){
                    Constants.delayTime++;
                    Log.d("DataContainer", "delayTime: " + Constants.delayTime);
                }else if(sameDcCount >= 100){
                    Constants.delayTime = 1000;
                }
            }

            su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
            su.setTask(Constants.UPDATE_COLOR);
            su.execute(String.valueOf(Constants.actualColor));

            if(Constants.HALT == 1 && dc.getCount() == 1){
                stopReading();
            }else {
                h.postDelayed(r, Constants.delayTime);
            }
        }
    };

    public void stopReading() {
        Log.d("DataReader", "Stop reading");
        if(h != null) {
            h.removeCallbacks(r);
            h = null;
        }
        try {
            fm.closeSingleton();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkBuffer(){
        if(pavBuffer.size() > Constants.peaksCtrl + 1){ //FIM DE TRECHO RUIM

            for(Pavement p : pavBuffer){ //SETANDO FLAG DE TODOS PAVEMENTS PARA 2 (RUIM)
                p.setFlag(2);
                db.addPavement(p);
            }


        }else if(pavBuffer.size() <= (Constants.peaksCtrl + 1) && pavBuffer.size() > 0){ //APENAS PICO SOZINHO
            Log.d("DataReader", "PEAK ALONE");
            for(Pavement p : pavBuffer){ //INSERINDO NO BANCO COMO TRECHO NORMAL
                if(p.getMagnitude() >= 2.2){
                    nextIsBlack = 1;
                    Constants.actualColor = Constants.BLACK;
                    p.setFlag(4);
                }
                db.addPavement(p);
            }
        }

        peaksctrl = 0;
        pavBuffer.clear();
    }

}
