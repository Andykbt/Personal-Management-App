package com.example.assignment1_personalmanagementapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class TodoTasksDatabaseManager {
    public static final String DB_NAME = "TaskDatabase";
    public static final String DB_TABLE = "TaskInfo";
    public static final int DB_VERSION = 3;

    public static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE
            + " (taskID INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Location TEXT, Status TEXT);";

    private SQLHelper helper;
    private SQLiteDatabase db;
    private Context context;

    public TodoTasksDatabaseManager(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();
    }

    public TodoTasksDatabaseManager openReadable() throws android.database.SQLException {
        helper = new SQLHelper(context);
        this.db = helper.getWritableDatabase();
        return this;
    }

    public void clearRecords() {    //clears all records in DB
        db = helper.getWritableDatabase();
        db.delete(DB_TABLE, null, null);
    }

    public boolean clearRow(int id) {   //clear record given ID
        db = helper.getWritableDatabase();
        return db.delete(DB_TABLE, "taskID" + "=" + id, null) > 0;
    }

    public ArrayList<String> retrieveInCompletedRows() {    //get all rows in which Status is InComplete
        ArrayList<String> taskRows = new ArrayList<String>();;
        Cursor cursor = db.query(DB_TABLE, new String[] { "taskID", "Name",
                        "Location", "Status"}, "Status" + "=?",
                new String[] {"InComplete"}, null, null, null);
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false) {
            taskRows.add(cursor.getString(0) + ")" + cursor.getString(1) + " @ " + cursor.getString(2) + "|" + cursor.getString(3));
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return taskRows;
    }

    public ArrayList<String> retrieveCompletedRows() {  //get all rows in which Status is Complete
        ArrayList<String> taskRows = new ArrayList<String>();
        Cursor cursor = db.query(DB_TABLE, new String[] { "taskID", "Name",
                        "Location", "Status"}, "Status" + "=?",
                new String[] {"Complete"}, null, null, null);
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false) {
            taskRows.add(cursor.getString(0) + ")" + cursor.getString(1) + "@" + cursor.getString(2) + "|" + cursor.getString(3));
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return taskRows;
    }

    public void close() { db.close(); }

    /*   ADD ROW IN DB   */
    public boolean addRow(String name, String location, String status ) {
        synchronized (this.db) {
            ContentValues newTask = new ContentValues();
            newTask.put("Name", name);
            newTask.put("Location", location);
            newTask.put("Status", status);

            try {
                db.insertOrThrow(DB_TABLE, null, newTask);
            } catch (Exception e) {
                Log.e("Error in inserting rows", e.toString());
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    /*   UPDATE ROW IN DB   */
    public boolean updateRow(String id, String name, String location, String status) {
        synchronized (this.db) {
            ContentValues updateFriend = new ContentValues();
            updateFriend.put("Name", name);
            updateFriend.put("Location", location);
            updateFriend.put("Status", status);

            try {
                db.update(DB_TABLE, updateFriend, "taskID="+id, null);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Update Row Error", e.toString());
                return false;
            }
            return true;
        }
    }

    /*   GET ID OF A ROW   */
    public int getID(String text) {
        String id = "0";

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ')')
                break;
            else
                id += text.charAt(i);
        }
        return Integer.parseInt(id);
    }

    public class SQLHelper extends SQLiteOpenHelper {

        public SQLHelper (Context c) {
            super(c, DB_NAME, null, DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) { db.execSQL(CREATE_TABLE); }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("FriendInfo table", "Upgrading database i.e. dropping table and recreating it");
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }
}
