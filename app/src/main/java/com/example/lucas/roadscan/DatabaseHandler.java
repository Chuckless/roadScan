package com.example.lucas.roadscan;

/**
 * Created by lucas on 06/07/15.
 */

        import java.util.ArrayList;
        import java.util.List;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.SQLException;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.provider.Settings.NameValueTable;
        import android.util.Log;

        import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdater;
        import com.example.lucas.roadscan.InterfaceUpdate.ScreenUpdaterInterface;
        import com.example.lucas.roadscan.Singleton.Constants;

public class DatabaseHandler extends SQLiteOpenHelper{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static int DBCount, uploaded;

    // Database Name
    private static final String DATABASE_NAME = "pavementManager";

    // Pavement table name
    private static final String TABLE_PAVEMENT = "pavement";

    // Pavement Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_IRI = "IRI";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "long";
    private static final String KEY_IMEI = "IMEI";
    private static final String KEY_SPD = "speed";
    private static final String KEY_FLAG = "flag";
    private static final String KEY_BRNG = "brng";

    private static DatabaseHandler mInstance = null;

    private SQLiteDatabase db;
    public ScreenUpdater su;
    private Context mContext;

    public static DatabaseHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHandler(context.getApplicationContext(),
                    DATABASE_NAME, DATABASE_VERSION);
        }
        return mInstance;
    }

    private DatabaseHandler(final Context context,final String name,final int version) {
        super(context, name, null, version);
        mContext = context;
        //db = this.getWritableDatabase();
    }

    /*@Override
    public synchronized void close() {
        if (mInstance != null)
            db.close();
    }*/

    public static int getUploaded() {
        return uploaded;
    }

    public static int getDBCount() {
        return DBCount;
    }

    public static void setDBCount(int DBCount) {
        DatabaseHandler.DBCount = DBCount;
    }

    public static void setUploaded(int uploaded) {
        DatabaseHandler.uploaded = uploaded;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PAVEMENT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PAVEMENT + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_IRI + " TEXT," +
                KEY_LAT + " TEXT," +
                KEY_LON + " TEXT," +
                KEY_IMEI + " TEXT," +
                KEY_SPD + " TEXT," +
                KEY_BRNG + " TEXT, " +
                KEY_FLAG + " TEXT" +
                ")";
        db.execSQL(CREATE_PAVEMENT_TABLE);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAVEMENT);
        onCreate(db);
    }


    // Adding new pavement
    public void addPavement(Pavement pavement) {

        Log.i("DBLog", "Adicionou pavement!");
        db = this.getWritableDatabase();
        /*Log.i("DBLog", "db value: " + db.toString());
        Log.i("DBLog", "db path: " + db.getPath());*/

        ContentValues values = new ContentValues();
        values.put(KEY_IRI, pavement.getStdv());
        values.put(KEY_LAT, pavement.getLat());
        values.put(KEY_LON, pavement.getLong());
        values.put(KEY_IMEI, pavement.getIMEI());
        values.put(KEY_SPD, pavement.getSpd());
        values.put(KEY_BRNG, pavement.getBearing());
        values.put(KEY_FLAG, pavement.getFlag());


        Log.i("DBLog", "values: " + values.toString());

        // Inserting Row
        long confirm = db.insert(TABLE_PAVEMENT, null, values);
        Log.i("DBLog", "confirm = " + confirm + " DBCount: " + getPavementCount());

       // db.close(); // Closing database connection

        /*Log.i("DBLog","IRI: " + pavement.getIRI() +
                "\nLat: " + pavement.getLatitude() +
                "\nLon: " + pavement.getLongitude() +
                "\nIMEI: "+ pavement.getIMEI());*/
    }

    public JSONArray DBtoJSON(int ROWLIMIT) throws JSONException{
        JSONArray   jsonArr = new JSONArray();

        for(Pavement pav : getAllPavements(ROWLIMIT)){
            JSONObject pavObj = new JSONObject();
            pavObj.put(KEY_IRI, pav.getStdv());
            pavObj.put(KEY_LAT, pav.getLat());
            pavObj.put(KEY_LON, pav.getLong());
            pavObj.put(KEY_IMEI, pav.getIMEI());
            pavObj.put(KEY_SPD, pav.getSpd());
            pavObj.put(KEY_BRNG, pav.getBearing());
            pavObj.put(KEY_FLAG, pav.getFlag());
            jsonArr.put(pavObj);
        }

        return jsonArr;
    }

    // Getting All pavements
    public ArrayList<Pavement> getAllPavements(int ROWLIMIT) {

        ArrayList<Pavement> pavementList = new ArrayList<Pavement>();
        String selectQuery = "SELECT * FROM " + TABLE_PAVEMENT + " LIMIT "  + ROWLIMIT;

        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                try {
                    Pavement pavement = new Pavement();

                    pavement.setId(Integer.parseInt(cursor.getString(0)));
                    pavement.setStdv(Float.parseFloat(cursor.getString(1)));
                    pavement.setLat(Double.parseDouble(cursor.getString(2)));
                    pavement.setLng(Double.parseDouble(cursor.getString(3)));
                    pavement.setIMEI(cursor.getString(4).toString());
                    pavement.setSpd(Float.parseFloat(cursor.getString(5)));
                    pavement.setBearing(Float.parseFloat(cursor.getString(6)));
                    pavement.setFlag(Integer.parseInt(cursor.getString(7)));
                    // Adding pavement to list
                    pavementList.add(pavement);
                }catch(NullPointerException e){

                }
            } while (cursor.moveToNext());
        }
        //db.close();
        // return contact list

        return pavementList;
    }

    public void removeRow(int number){
        //		String removeQuery = "DELETE FROM " + TABLE_PAVEMENT + " limit " + number;
        String select = "SELECT id FROM "+ TABLE_PAVEMENT + " LIMIT " + number;
        String removeQuery = "DELETE FROM " + TABLE_PAVEMENT + " WHERE id IN ("+ select +")";
        db = this.getWritableDatabase();
        //db.execSQL(removeQuery, null);
        db.execSQL(removeQuery);
        //db.close();
    }

    // Getting contacts Count
    public int getPavementCount() {
        int count;
        String countQuery = "SELECT  * FROM " + TABLE_PAVEMENT;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();

        cursor.close();
        //db.close();
        // return count
        //Log.d("DBLog", "DBSize: " + count);
        return count;
    }

    public void clearDB() {
        String clear = "DELETE FROM " + TABLE_PAVEMENT + " WHERE id > 0";
        db = this.getWritableDatabase();
        db.execSQL(clear);
        //db.close();
    }
}


