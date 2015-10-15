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

        import com.example.lucas.roadscan.Singleton.Constants;

public class ClientServer extends AsyncTask<String, String, String>{

    public JSONParser 		jsonParser = new JSONParser();
    public JSONArray        jsonObj = null;
    public int				size;
    private DatabaseHandler db;

    public ClientServer(JSONArray jsonObj, int size, Context context) {
        this.jsonObj = jsonObj;           //JsonOBJ
        this.size = size;
        db = DatabaseHandler.getInstance(context);
    }

    @Override
    protected String doInBackground(String... args) {
        if(jsonObj != null){  //Envia o JsonOBJ completo
            try {
                Log.d("clientServer", "Realizando httpRequest...");
                JSONObject json = jsonParser.makeHttpRequest(Constants.URLADD, "POST", jsonObj);
                Log.d("clientServer", "success: " + json.getString("success"));

                if(json.getString("success").equals("1")){ //Insersao feita corretamente no servidor
                    Constants.success = 1;
                    Log.d("clientServer", "Envio feito com sucesso. Removendo as primeiras " + size + " linhas");
                }else {
                    Log.d("clientServer", json.getString("success"));
                }
                Log.d("clientServer", "json = " + json.toString());
                Thread.sleep(10);
                Constants.uploadKey = 0;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        return null;
    }
}


