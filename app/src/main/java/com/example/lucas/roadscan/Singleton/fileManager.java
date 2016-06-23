package com.example.lucas.roadscan.Singleton;

/**
 * Created by lucas on 06/07/15.
 */
import android.media.audiofx.EnvironmentalReverb;
import android.os.Environment;
import android.util.Log;

import com.example.lucas.roadscan.Pavement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class fileManager {

    private DateFormat timeFormat;
    private DateFormat dateFormat;
    private Date date;
    private FileWriter out = null;
    private File extSD;

    private OutputStreamWriter osw = null;
    private FileOutputStream fos = null;

    private OutputStreamWriter osw2 = null;
    private FileOutputStream fos2 = null;

    private OutputStreamWriter osw3 = null;
    private FileOutputStream fos3 = null;

    private String fileName, filePath,fileName2;
    private int cont;
    private static fileManager singleton = null;

    protected fileManager(){

        extSD = Environment.getExternalStorageDirectory();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        date = new Date();

        /*openFile();
        openBDFile();*/

        cont = 0;
    };

    public static fileManager getInstance(){
        if(singleton == null){
            singleton = new fileManager();
        }
        return singleton;
    }

    public void closeSingleton() throws IOException {
        if (singleton != null){
            closeFile();
            closeDBfile();
            singleton = null;
        }
    }

    public void openBDFile() {
        if(!fileStatusBD()) {

            date = new Date();
            try {

                fos3 = new FileOutputStream(extSD.toString() + "DBversionSTDV - " + dateFormat.format(date) + ".txt", true);
                osw3 = new OutputStreamWriter(fos3);

                writeToSQLFile("\n\n-------- NEW FILE - " + fileName + "------------\n\n");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            try {
                closeDBfile();
                openFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openFile(){
        if(!fileStatus()) {
            date = new Date();
            fileName = dateFormat.format(date) + " - " + timeFormat.format(date) + " - " + Constants.TXTHint + ".txt";
            fileName2 = dateFormat.format(date) + " - " + timeFormat.format(date) + " - DISCRETE " + Constants.TXTHint + ".txt";

            filePath = extSD.toString() + fileName;
            try {
                fos = new FileOutputStream(filePath, true);
                osw = new OutputStreamWriter(fos, "US-ASCII");

                /*fos2 = new FileOutputStream(extSD.toString() + "/" + fileName2);
                osw2 = new OutputStreamWriter(fos2, "US-ASCII");*/
                writeHeader();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                closeFile();
                openFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void writeToSQLFile(String string) throws IOException {
        if (fileStatusBD() && isExternalStorageWritable()) {
            osw3.append(string);
            osw3.append("\n");
            osw3.flush();
        } else {
            openBDFile();
            writeToSQLFile(string);
        }
    }

    public void writeToSQLFile(Pavement p) throws IOException {
        if(fileStatusBD() && isExternalStorageWritable()) {
            if(p != null) {
                osw3.append("INSERT INTO pavement(IRI, latitude,longitude, IMEI, speed, brng, flag) VALUES ('"
                        + p.getStdv() + "', '" + p.getLocation().getLatitude() + "', '" + p.getLocation().getLongitude() +  "', '"
                        + Constants.deviceID + "', '" + p.getSpeed() + "', '" + p.getBearing() + "' , '" + p.getFlag() +
                        "');");
                osw3.append("\n");
                osw3.flush();
            }
        }else{
            openBDFile();
            writeToSQLFile(p);
        }
    }

    public void closeDBfile() throws IOException {
        if(osw3 != null && fos3 != null) {
            osw3.flush();
            osw3.close();
            fos3.close();

            osw3 = null;
            fos3 = null;
        }

    }


    void writeHeader() throws IOException {
        if(fileStatus() && isExternalStorageWritable()) {
            if(isExternalStorageWritable()) {
                osw.append("Time, Speed, Stdv, Distance");
                osw.append("\n");

                /*osw2.append("Time, Z, Speed, Distance");
                osw2.append("\n");*/
            }
        }
    }


    public void writeToFile2(Pavement p) throws IOException {
        Log.d("FM", "WriteToFile");
        if(isExternalStorageWritable()) {
            if(p != null) {
                Log.d("FM", "Escrevendo " + p.toString());

                osw2.append("" + cont + ", " + p.getZ() + ", " + p.getSpeed() + ", " + p.getDistance());
                osw2.append("\n");
                osw2.flush();
                cont++;
            }
        }else{
            openFile();
            writeToFile(p);
        }
    }

    public void writeToFile(Pavement p) throws IOException {
        Log.d("FM", "WriteToFile");
        if(isExternalStorageWritable() && p != null && fileStatus()) {
            Log.d("FM", "Escrevendo " + p.toString());

            osw.append("" + cont + ", " + p.getSpeed() + ",  " + p.getStdv() + ", " + p.getDistance());
            osw.append("\n");
            osw.flush();
            cont++;
        }else{
            openFile();
            writeToFile(p);
        }
    }

    public void setCont(int cont) {
        this.cont = cont;
    }

    public int getCont() {

        return cont;
    }

    public boolean fileStatus(){
        if(osw == null || fos == null) return false; //Arquivo fechado = False;
        else return true; //Arquivo aberto = True;
    }

    public boolean fileStatusBD(){
        if(osw3 == null || fos3 == null) return false;
        else return true;
    }

    public void closeFile() throws IOException{
        if(fileStatus()) {
            osw.flush();
            osw.close();
            fos.close();

            osw = null;
            fos = null;
        }

        if(osw2 != null){
            osw2.flush();
            osw2.close();
            fos2.close();

            osw2 = null;
            fos2 = null;
        }
        cont = 0;

    }




    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}
