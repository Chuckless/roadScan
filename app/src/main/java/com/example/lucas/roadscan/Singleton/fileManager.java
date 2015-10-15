package com.example.lucas.roadscan.Singleton;

/**
 * Created by lucas on 06/07/15.
 */
import android.os.Environment;

import com.example.lucas.roadscan.PavementAux;

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
    private FileOutputStream fos = null;
    private static FileOutputStream fos2 = null;
    private OutputStreamWriter osw = null;
    private static OutputStreamWriter osw2 = null;
    private String fileName, filePath;
    private int cont;
    private static fileManager singleton = null;

    protected fileManager(){
        date = new Date();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        extSD = Environment.getExternalStorageDirectory();

        try {
            fos2 = new FileOutputStream(extSD.toString() + "/" + "DBversion - " + dateFormat.format(date) + ".txt");
            osw2 = new OutputStreamWriter(fos2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        cont = 0;
    };

    public static fileManager getInstance(){
        if(singleton == null){
            singleton = new fileManager();
        }
        return singleton;
    }


    public void openFile(){
        if(!fileStatus()) {
            date = new Date();
            fileName = dateFormat.format(date) + " - " + timeFormat.format(date) + ".txt";
            filePath = extSD.toString() + "/" + fileName;
            try {
                fos = new FileOutputStream(filePath, true);
                osw = new OutputStreamWriter(fos);
                writeHeader();
                writeToSQLFile("\n\n-------- NEW FILE - " + fileName + "------------\n\n");
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

    void writeHeader() throws IOException {
        if(fileStatus() && isExternalStorageWritable()) {
            if(isExternalStorageWritable()) {
                osw.append("Time X Y Z Lat Long IMEI time distance (mean) (stdv)");
                osw.append("\n");
            }
        }
    }

    public void writeToSQLFile(String string){
        try {
            osw2.append(string);
            osw2.append("\n");
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToSQLFile(PavementAux p){
        try {
            osw2.append("INSERT INTO pavement(IRI, latitude,longitude, IMEI ) VALUES('" + p.getZ() + "', '" + p.getLat() + "', '" + p.getLong() + "', 'b743a276e3339589');");
            osw2.append("\n");
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(PavementAux p) throws IOException {
        date = new Date();
        if(fileStatus() && isExternalStorageWritable()) {
            osw.append("" + cont + ", " + p.getX() + ", " + p.getY() + ", " + p.getZ() + ", " + p.getLat() + ", " + p.getLong() + ", " + Constants.deviceID + ", " + timeFormat.format(date) +", " + p.getDistance());
            osw.append("\n");
            osw.flush();
            cont++;
        }else{
            openFile();
            writeToFile(p);
        }
    }

    public void writeToFile(PavementAux p, float mean, float stdv) throws IOException {
        date = new Date();
        if(fileStatus() && isExternalStorageWritable()) {
            osw.append("" + cont + ", " + p.getX() + ", " + p.getY() + ", " + p.getZ() + ", " + p.getLat() + ", " + p.getLong() + ", " + Constants.deviceID + ", " +
                        timeFormat.format(date) +", " + p.getDistance() + ", " + mean + ", " + stdv);
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
        if(osw == null && fos == null) return false; //Arquivo fechado = False;
        else return true; //Arquivo aberto = True;
    }

    public void closeFile() throws IOException{
        if(fileStatus()) {
            osw.flush();
            osw.close();
            fos.close();

            osw = null;
            fos = null;

            cont = 0;
        }
    }

    public static void closeDBfile() throws IOException {
        if(osw2 != null && fos2 != null) {
            osw2.flush();
            osw2.close();
            fos2.close();

            osw2 = null;
            fos2 = null;
        }
    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
