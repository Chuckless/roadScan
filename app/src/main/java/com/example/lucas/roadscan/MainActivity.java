package com.example.lucas.roadscan;

        import android.app.Activity;
        import android.content.Intent;

        import android.os.*;
        import android.provider.Settings.Secure;

        import android.view.Menu;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.CompoundButton;
        import android.widget.EditText;

        import android.widget.Switch;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.lucas.roadscan.GPS.CalibrateGPS;

        import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdaterInterface;
        import com.example.lucas.roadscan.Singleton.Constants;
        import com.example.lucas.roadscan.Singleton.GPS;
        import com.example.lucas.roadscan.Singleton.fileManager;

        import java.io.IOException;


public class MainActivity extends Activity implements ScreenUpdaterInterface {

    static TextView IRI, lat, lng, speed, dist, textCount, textUploaded;
    private GPS gps;
    private CalibrateGPS cGPS;
    private CheckConnection cc;

    static EditText editIp, editRow;
    static Switch mySwitch;
    static Button changeBtn, uploadBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants.deviceID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        gps = GPS.getInstance(MainActivity.this);

        if(!gps.isGPSEnabled)
            gps.buildAlertMessageNoGps();
        else{
            cGPS = new CalibrateGPS(MainActivity.this);
            cGPS.execute();
        }



        Constants.SensorFrequency = (1/Constants.Hertz)*1000000;

        mySwitch 	    = (Switch)   findViewById(R.id.switch1);


        lat 		= (TextView) findViewById(R.id.textLat);
        lng 		= (TextView) findViewById(R.id.textLng);
        speed 		= (TextView) findViewById(R.id.textSpeed);
        dist 		= (TextView) findViewById(R.id.textDist);
        textCount   = (TextView) findViewById(R.id.textCount);
        textUploaded= (TextView) findViewById(R.id.textUploaded);


        editIp      = (EditText) findViewById(R.id.editIp);
        editRow     = (EditText) findViewById(R.id.editRow);
        changeBtn   = (Button)   findViewById(R.id.changeBtn);
        uploadBtn   = (Button)   findViewById(R.id.btnUpload);



        lat.      setVisibility(View.INVISIBLE);
        lng.      setVisibility(View.INVISIBLE);
        speed.    setVisibility(View.INVISIBLE);
        dist.     setVisibility(View.INVISIBLE);
        textCount.setVisibility(View.INVISIBLE);
        textUploaded.setVisibility(View.INVISIBLE);


        editIp.   setText(Constants.SERVERIP);
        editRow.  setText("" + Constants.DBLIMIT);

        uploadBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db = DatabaseHandler.getInstance(MainActivity.this);
                db.clearDB();
                Constants.DBEmpty = 1;
                Constants.DBLIMIT = Constants.defaultDBLIMIT;
            }
        });

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    lat.   setVisibility(View.VISIBLE);
                    lng.   setVisibility(View.VISIBLE);
                    speed. setVisibility(View.VISIBLE);
                    dist.  setVisibility(View.VISIBLE);

                    textCount.setVisibility(View.VISIBLE);
                    textUploaded.setVisibility(View.VISIBLE);

                }else{
                    lat.   setVisibility(View.INVISIBLE);
                    lng.   setVisibility(View.INVISIBLE);
                    speed. setVisibility(View.INVISIBLE);
                    dist.  setVisibility(View.INVISIBLE);

                    textCount.setVisibility(View.INVISIBLE);
                    textUploaded.setVisibility(View.INVISIBLE);

                    textCount.setText("DBCount: ");
                    textUploaded.setText("RawCount: ");
                }
            }
        });


        changeBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Constants.SERVERIP = editIp.getText().toString();
                Constants.DBLIMIT = Integer.parseInt(editRow.getText().toString());

                Constants.URLADD     = "http://" + Constants.SERVERIP + "/roadie/addarray.php";
                editIp.   setText(Constants.SERVERIP);
                editRow.setText(""+Constants.DBLIMIT);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(this.getCurrentFocus());
        try {
            fileManager.closeDBfile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to start the service
    public void startService(View view) {
        if(!gps.isGPSEnabled)
            gps.buildAlertMessageNoGps();
        else {
            Constants.DRSLEEP = 10;
            cc = CheckConnection.getInstance(this);
            cc.isConnected();

            startService(new Intent(getBaseContext(), MyService.class));
            Constants.serviceRunning = 1;

            if(Constants.DataReader == 0) {
                DataReader dr = new DataReader(MainActivity.this);
                Thread thread = new Thread(dr);
                thread.start();
                Constants.DataReader = 1;
            }

            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to stop the service
    public void stopService(View view) {
        if(Constants.serviceRunning == 1) {
            stopService(new Intent(getBaseContext(), MyService.class));
            Constants.serviceRunning = 0;
            Constants.CheckConnection = 0;

            cc.stopChecking();
            cc.killInstance();

            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "Service's not running!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void Toaster(String string) {
        Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateMeanTV(String string) {
        textCount.setText("Media: " + string);
    }

    @Override
    public void updateDevTV(String string) {
        textUploaded.setText("Desv: " + string);
    }



}