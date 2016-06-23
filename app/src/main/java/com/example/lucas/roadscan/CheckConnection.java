package com.example.lucas.roadscan;

/**
 * Created by lucas on 06/07/15.
 */

        import android.content.Context;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Handler;
        import android.util.Log;

        import com.example.lucas.roadscan.Singleton.Constants;


public class CheckConnection {
    private Handler handler;
    private Runnable r;
    private Context mContext;
    static boolean haveConnectedMobile = false;
    static boolean haveConnectedWifi = false;
    public static CheckConnection mInstance = null;


    public static CheckConnection getInstance(Context context){
        if (mInstance == null){
            mInstance = new CheckConnection(context.getApplicationContext());
        }

        return mInstance;
    }


    private CheckConnection (final Context context){
        mContext = context;
        isConnected();
    }

    public synchronized  void killInstance(){
        mInstance = null;
    }

    void isConnected(){
        handler = new Handler();
        r = new Runnable(){
            @Override
            public void run() {
                Log.d("conec", "CheckConnection inicializado");
                handler.postDelayed(this, Constants.CCSeconds * 1000);
                ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
                NetworkInfo[] netInfo = cm.getAllNetworkInfo();
                for(NetworkInfo ni : netInfo){
                    if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                        if (ni.isConnected()){
                            if(haveConnectedMobile == false){
                                haveConnectedMobile = true;
                                Log.d("conec", "Conectou 3G");
                            }
                        }
                        else{
                            if(haveConnectedMobile == true){
                                haveConnectedMobile = false;

                                Log.d("conec", "Desconectou 3G");
                            }

                        }
                    if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                        if(ni.isConnected()){
                            if(haveConnectedWifi == false){
                                haveConnectedWifi = true;
                                Log.d("conec", "Conectou Wifi");
                            }
                        }else{
                            if(haveConnectedWifi == true){
                                haveConnectedWifi = false;
                                Log.d("conec", "Desconectou Wifi");
                            }

                        }
                }
            }
        };handler.postDelayed(r, 1000);
    }

    public void stopChecking(){
        if(handler != null) {
            handler.removeCallbacks(r);
            Log.d("conec", "Terminando serviço de checagem de conexao");
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Runnable getR() {
        return r;
    }

    public void setR(Runnable r) {
        this.r = r;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public boolean haveInternet(){
        return isHaveConnectedMobile() | isHaveConnectedWifi();
    }

    public boolean isHaveConnectedMobile() {
        return haveConnectedMobile;
    }

    public static void setHaveConnectedMobile(boolean haveConnectedMobile) {
        CheckConnection.haveConnectedMobile = haveConnectedMobile;
    }

    public boolean isHaveConnectedWifi() {
        return haveConnectedWifi;
    }

    public static void setHaveConnectedWifi(boolean haveConnectedWifi) {
        CheckConnection.haveConnectedWifi = haveConnectedWifi;
    }

}
