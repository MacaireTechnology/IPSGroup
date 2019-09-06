package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME="ipsgroup.db";
    // User table name
    private static final String TABLE_CLOCK_IN_OUT="IPSGROUP";

    private static final String COLUMN_USER_ID            ="user_id";
    private static final String COLUMN_EMP_ID             ="emp_id";
    private static final String COLUMN_CLOCK_TYPE         ="clock_type";
    private static final String COLUMN_CLOCK_IN_ID        ="clock_in_id";
    private static final String COLUMN_IMAGE_URI          ="img_uri";
    private static final String COLUMN_DATE_TIME          ="date_time";
    private static final String COLUMN_LATITUDE           ="latitude";
    private static final String COLUMN_LONGITUDE          ="longitude";
    private static final String COLUMN_CURRENT_LOCATION   ="currentLocation";

    // create a sql query
    private static String CREATE_CLOCK_IN_OUT_TABLE ="create table "+TABLE_CLOCK_IN_OUT + " (" +COLUMN_USER_ID+ " integer primary key autoincrement ,"
            +COLUMN_EMP_ID + " text, "+COLUMN_CLOCK_TYPE + " text, "+COLUMN_CLOCK_IN_ID+" text, "+
            COLUMN_IMAGE_URI+" text, "+COLUMN_DATE_TIME+" text, "
            +COLUMN_LATITUDE +" text, "+COLUMN_LONGITUDE+" text, "+COLUMN_CURRENT_LOCATION+" text "+")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CLOCK_IN_OUT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_CLOCK_IN_OUT);
    }

    public void SaveClockINOUT(ClockInOutTime clockInOutTime){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_EMP_ID, clockInOutTime.getEmp_id());
        cv.put(COLUMN_CLOCK_TYPE, clockInOutTime.getClock_type());
        cv.put(COLUMN_CLOCK_IN_ID, clockInOutTime.getClock_in_id());
        cv.put(COLUMN_IMAGE_URI, clockInOutTime.getImg_uri());
        cv.put(COLUMN_DATE_TIME, clockInOutTime.getDate_time());
        cv.put(COLUMN_LATITUDE, clockInOutTime.getLatitude());
        cv.put(COLUMN_LONGITUDE, clockInOutTime.getLongitude());
        cv.put(COLUMN_CURRENT_LOCATION, clockInOutTime.getCurrentLocation());

        sqLiteDatabase.insert(TABLE_CLOCK_IN_OUT, null, cv);
        sqLiteDatabase.close();
    }
    public List<ClockInOutTime> getClockInOut(){

        String[] columns={
                COLUMN_USER_ID,
                COLUMN_EMP_ID,
                COLUMN_CLOCK_TYPE,
                COLUMN_CLOCK_IN_ID,
                COLUMN_IMAGE_URI,
                COLUMN_DATE_TIME,
                COLUMN_LATITUDE,
                COLUMN_LONGITUDE,
                COLUMN_CURRENT_LOCATION
        };

        List<ClockInOutTime> clockInOutTimes =new ArrayList<ClockInOutTime>();
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(TABLE_CLOCK_IN_OUT,columns,null,null,null,null,null);

        if(cursor.moveToFirst()){
            do{
                ClockInOutTime clockInOutTime=new ClockInOutTime();

                clockInOutTime.setId(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                clockInOutTime.setEmp_id(cursor.getString(cursor.getColumnIndex(COLUMN_EMP_ID)));
                clockInOutTime.setClock_type(cursor.getString(cursor.getColumnIndex(COLUMN_CLOCK_TYPE)));
                clockInOutTime.setClock_in_id(cursor.getString(cursor.getColumnIndex(COLUMN_CLOCK_IN_ID)));
                clockInOutTime.setImg_uri(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URI)));
                clockInOutTime.setDate_time(cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TIME)));
                clockInOutTime.setLatitude(cursor.getString(cursor.getColumnIndex(COLUMN_LATITUDE)));
                clockInOutTime.setLongitude(cursor.getString(cursor.getColumnIndex(COLUMN_LONGITUDE)));
                clockInOutTime.setCurrentLocation(cursor.getString(cursor.getColumnIndex(COLUMN_CURRENT_LOCATION)));

                clockInOutTimes.add(clockInOutTime);
            }while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return clockInOutTimes;
    }

    public void deleteColor(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CLOCK_IN_OUT+ " WHERE "+ COLUMN_USER_ID +" = '"+value+"'");
        db.close();
    }

}
