package com.example.lucas.roadscan;

import android.content.Context;


import android.os.Handler;
import android.util.Log;

import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdater;
import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdaterInterface;
import com.example.lucas.roadscan.Singleton.Constants;

import java.util.logging.LogRecord;

/**
 * Created by lucas on 04/03/16.
 */
public class cloudService {

    private static cloudService singleton = null;
    private  Handler handler;
    private Context context;
    private DatabaseHandler db;
    private int databaseSize, howMany;
    private ClientServer cs;
    private ScreenUpdater su;
    private CheckConnection cc;

    public static cloudService getInstance(Context context){
        if(singleton == null){
            singleton = new cloudService(context);
        }

        return singleton;
    }

    public static cloudService getInstance(){
        if(singleton != null){
            return singleton;
        }
        return null;
    }


    public cloudService(Context context){
        this.context = context;
        db = DatabaseHandler.getInstance(context);
        cc = CheckConnection.getInstance(context);
        Log.d("cloud", "construtor");
        startHandler();
    }

    public void startHandler(){
        handler = new Handler();
        handler.post(r);
    }


        Runnable r = new Runnable() {
            @Override
            public void run() {

                /*if(databaseSize == 0 && Constants.serviceRunning == 0){
                    Constants.DataReader = 0;
                    Thread.currentThread().interrupt();
                }*/

                if (Constants.success == 1) {
                    toDelete(howMany);
                    Constants.success = 0;
                }

                databaseSize = db.getPavementCount();

                if (cc.haveInternet() && MainActivity.checkUpload.isChecked()) {
                    if (databaseSize >= 30) {
                            howMany = databaseSize;
                            if(howMany > 50){
                                howMany = 50;
                            }
                        Log.d("cloud", "upload");
                            toUpload(howMany);
                    }else if(databaseSize > 0 && Constants.serviceRunning == 0){
                        toUpload(db.getPavementCount());

                    }else{
                        Log.d("cloud", "banco vazio");
                    }
                }else{
                    Log.d("cloud", "sem net");
                }

                su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
                su.setTask(Constants.UPDATE_COUNT);
                su.execute("" + db.getPavementCount());

                    handler.postDelayed(r, 5000);
            }
        };

    private void toDelete(int howMany) {

        db.removeRow(howMany);

        su = new ScreenUpdater(context, (ScreenUpdaterInterface) context);
        su.setTask(Constants.UPDATE_COUNT);
        su.execute("" + db.getPavementCount());
    }

    public void toUpload(int howMany) {
        if (Constants.uploadKey == 0) {
            Constants.uploadKey = 1;
            cs = new ClientServer(howMany, context);
            cs.execute();
        }

    }

}
