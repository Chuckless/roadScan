package com.example.lucas.roadscan;

/**
 * Created by lucas on 06/07/15.
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdater;
import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdaterInterface;
import com.example.lucas.roadscan.Singleton.Constants;

public class ClientServer extends AsyncTask<String, String, String>{

    public JSONParser 		jsonParser = new JSONParser();
    public JSONArray        jsonObj = null;
    public int				size;
    public DatabaseHandler db;
    public ScreenUpdater su;
    public Context context;
    public String URL;
    JSONObject json;

    public ClientServer(int size, Context context){
        db = DatabaseHandler.getInstance(context);
        this.context = context;
        this.size = size;
        URL = Constants.URLADD;
    }

    @Override
    protected String doInBackground(String... args) {

        try {
            this.jsonObj = db.DBtoJSON(size);          //JsonOBJ
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(jsonObj != null){  //Envia o JsonOBJ completo

            Log.d("clientServer", "Realizando httpRequest...");
            try {
                json = new JSONObject();
                json = jsonParser.makeHttpRequest(URL, "POST", jsonObj);
            }catch(Exception e){
                Log.d("clientServer", "bizius");
                Constants.success = 0;
                Constants.uploadKey = 0;
                Thread.currentThread().interrupt();
            }
            if(json != null) {
                try {
                    Log.d("clientServer", "success: " + json.getString("success"));

                    if (json.getString("success").equals("1")) { //Insersao feita corretamente no servidor
                        Constants.success = 1;
                        Log.d("clientServer", "Envio feito com sucesso.");

                    } else {
                        Log.d("clientServer", "Envio falhou " + json.getString("success"));
                        Constants.success = 0;
                    }
                    Log.d("clientServer", "json = " + json.toString());
                    //Thread.sleep(10);
                    Constants.uploadKey = 0;
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Constants.success = 0;
                    Constants.uploadKey = 0;
                }
            }else{
                Constants.success = 0;
                Constants.uploadKey = 0;
            }
        }


        return null;
    }
}


