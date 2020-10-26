package com.example.assignment1_personalmanagementapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EventDatabaseManager extends AppCompatActivity {
    public static final String DB_NAME = "EventDatabase";
    public static final String DB_TABLE ="EventInfo";
    public static final int DB_VERSION = 3;
    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE
            + " (EventID INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Location TEXT, Date_Time LONG);";

    private SQLHelper helper;
    private SQLiteDatabase db;
    private Context context;

    public EventDatabaseManager(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();
    }

    public EventDatabaseManager openReadable() throws android.database.SQLException {
        helper = new SQLHelper(context);
        db = helper.getReadableDatabase();
        return this;
    }

    public void close() { helper.close(); }

    /*   CLEARS ALL RECORDS IN DB   */
    public void clearRecords() {
        db = helper.getWritableDatabase();
        db.delete(DB_TABLE, null, null);
    }

    /*   CLEAR ROW GIVEN ID IN DB   */
    public boolean clearRecord(String id) {
        db = helper.getWritableDatabase();
        return db.delete(DB_TABLE, "EventID" + "=" + id, null) > 0;
    }

    /*   ADD ROW IN DB   */
    public boolean addRow(String name, String location, long date_time) {
        synchronized (this.db) {
            ContentValues newEvent = new ContentValues();
            newEvent.put("Name", name);
            newEvent.put("Location", location);
            newEvent.put("Date_Time", date_time);

            Log.e("event name", name);
            Log.e("event location", location);
            Log.e("event date_time", String.valueOf(date_time));

            try {
                db.insertOrThrow(DB_TABLE, null, newEvent);
            } catch (Exception e) {
                Log.e("Error in inserting rows", e.toString());
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    /*   RETRIEVE ALL ROWS IN DB   */
    public ArrayList<String> retrieveRows() {
        ArrayList<String> eventRows = new ArrayList<String>();
        String[] columns = {"EventID", "Name", "Location"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            eventRows.add(cursor.getInt(0) + cursor.getString(1) + " @ " + cursor.getString(2) + " on ");
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return eventRows;
    }

    /*   UPDATE ONLY IDS OF EACH ROW IN DB   */
    public ArrayList<String> retrieveIDs() {
        ArrayList<String> eventRows = new ArrayList<String>();
        String[] columns = {"EventID"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            eventRows.add(Integer.toString(cursor.getInt(0)));
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return eventRows;
    }

    /*   UPDATE ROW IN DB   */
    public boolean updateRow(String id, String Name, String Location, long dateTime) {
        synchronized (this.db) {
            ContentValues updateEvent = new ContentValues();
            updateEvent.put("Name", Name);
            updateEvent.put("Location", Location);
            updateEvent.put("Date_Time", dateTime);

            try {
                db.update(DB_TABLE, updateEvent, "EventID=" + id, null);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Update Row Error", e.toString());
                return false;
            }

            return true;
        }
    }

    /*   GET ID OF ROW IN DB   */
    public int getID(String temp) {
        String id = "";
        for (int n = 0; n < temp.length(); n++) {
            if (temp.charAt(n) == ')')
                break;
            else
                id += temp.charAt(n);
        }
        return Integer.parseInt(id);
    }

    /*   GET NAME OF ROW IN DB   */
    public String getName(String id) {
        String name = "";
        Cursor cursor = db.query(DB_TABLE, new String[] {"EventID", "Name"},
                "EventID" + "=?", new String[] {String.valueOf(id)}, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            name = cursor.getString(1);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return name;
    }

    /*   GET LOCATION OF ROW IN DB   */
    public String getLocation(String id) {
        String location = "";
        Cursor cursor = db.query(DB_TABLE, new String[] {"EventID", "Location"},
                "EventID" + "=?", new String[] {String.valueOf(id)}, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            location = cursor.getString(1);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return location;
    }

    /*   GET DATETIME OF ROW IN DB   */
    public long getDateTime(String id) {
        String dateTime = "0";
        Cursor cursor = db.query(DB_TABLE, new String[] {"EventID", "Date_Time"},
                "EventID" + "=?", new String[] {String.valueOf(id)}, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            //Log.e("", cursor.getString(1));
            dateTime = cursor.getString(1);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        //Log.e("GETDATETIME", dateTime);
        return Long.parseLong(dateTime);
    }


    public static class SQLHelper extends SQLiteOpenHelper {
        public SQLHelper(Context c) {
            super(c, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("FriendInfo table", "Upgrading database i.e. dropping table and recreating it");
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }
}

