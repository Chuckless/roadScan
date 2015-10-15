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
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_IMEI = "IMEI";

    private static DatabaseHandler mInstance = null;

    private SQLiteDatabase db = null;

    public static DatabaseHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHandler(context.getApplicationContext(),
                    DATABASE_NAME, DATABASE_VERSION);
        }
        return mInstance;
    }

    private DatabaseHandler(final Context context, final String name, final int version) {
        super(context, name, null, version);
        db = getWritableDatabase();
    }

    @Override
    public synchronized void close() {
        if (mInstance != null)
            db.close();
    }

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
        String CREATE_PAVEMENT_TABLE = "CREATE TABLE " + TABLE_PAVEMENT + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_IRI + " TEXT," +
                KEY_LAT + " TEXT," +
                KEY_LONG + " TEXT," +
                KEY_IMEI + " TEXT" +
                ")";
        db.execSQL(CREATE_PAVEMENT_TABLE);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAVEMENT);
        onCreate(db);
    }


    // Adding new contact
    public void addPavement(Pavement pavement) {
        Log.i("DBLog", "Adicionou pavement!");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IRI, pavement.getIRI());
        values.put(KEY_LAT, pavement.getLatitude());
        values.put(KEY_LONG, pavement.getLongitude());
        values.put(KEY_IMEI, pavement.getIMEI());

        // Inserting Row
        long confirm = db.insert(TABLE_PAVEMENT, null , values);
        Log.i("DBLog", "confirm = " + confirm + " DBCount: " + getPavementCount());

        db.close(); // Closing database connection



        /*Log.i("DBLog","IRI: " + pavement.getIRI() +
                "\nLat: " + pavement.getLatitude() +
                "\nLon: " + pavement.getLongitude() +
                "\nIMEI: "+ pavement.getIMEI());*/
    }

    public void closeDB(){
        //db.close();
    }

    public JSONArray getMultiplePavements(int ROWLIMIT) throws JSONException{
        JSONArray   jsonArr = new JSONArray();

        for(Pavement pav : getAllPavements(ROWLIMIT)){
            JSONObject pavObj = new JSONObject();
            pavObj.put("IRI", pav.getIRI());
            pavObj.put("lat", pav.getLatitude());
            pavObj.put("lon", pav.getLongitude());
            pavObj.put("IMEI", pav.getIMEI());
            jsonArr.put(pavObj);
        }

        return jsonArr;
    }

    // Getting All pavements
    public ArrayList<Pavement> getAllPavements(int ROWLIMIT) {

        ArrayList<Pavement> pavementList = new ArrayList<Pavement>();
        String selectQuery = "SELECT * FROM " + TABLE_PAVEMENT + " LIMIT "  + ROWLIMIT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Pavement pavement = new Pavement();
                pavement.setId(Integer.parseInt(cursor.getString(0)));
                pavement.setIRI(Double.parseDouble(cursor.getString(1)));
                pavement.setLatitude(Double.parseDouble(cursor.getString(2)));
                pavement.setLongitude(Double.parseDouble(cursor.getString(3)));
                pavement.setIMEI(cursor.getString(4).toString());
                // Adding contact to list
                pavementList.add(pavement);
            } while (cursor.moveToNext());
        }

        // return contact list

        return pavementList;
    }

    public void removeRow(int number){
        //		String removeQuery = "DELETE FROM " + TABLE_PAVEMENT + " limit " + number;
        String select = "SELECT id FROM "+ TABLE_PAVEMENT + " LIMIT " + number;
        String removeQuery = "DELETE FROM " + TABLE_PAVEMENT + " WHERE id IN ("+ select +")";
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL(removeQuery, null);
        db.execSQL(removeQuery);
    }

    // Getting contacts Count
    public int getPavementCount() {
        int count;
        String countQuery = "SELECT  * FROM " + TABLE_PAVEMENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    public void clearDB() {
        String clear = "DELETE FROM " + TABLE_PAVEMENT;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(clear);
    }
}


