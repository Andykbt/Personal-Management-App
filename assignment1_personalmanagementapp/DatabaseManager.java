package com.example.assignment1_personalmanagementapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DatabaseManager {
    public static final String DB_NAME = "dataBase";
    public static final String DB_TABLE = "FriendInfo";
    public static final int DB_VERSION = 2;

    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE
            + " (friendID INTEGER PRIMARY KEY AUTOINCREMENT, fNAME TEXT, lNAME TEXT, Gender TEXT, Age INTEGER, Address TEXT, Image BLOB);";

    private SQLHelper helper;
    private SQLiteDatabase db;
    private Context context;

    public DatabaseManager(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();
        System.out.println("CRETE TABLE NAME: " + CREATE_TABLE);
    }

    public DatabaseManager openReadable() throws android.database.SQLException {
        helper = new SQLHelper(context);
        db = helper.getReadableDatabase();
        return this;
    }

    public void close() { helper.close(); }

    /*   ADDING ROW IN DB   */
    public boolean addRow(String fName, String lName, String gender, String age, String address, byte[] image) {
        synchronized (this.db) {
            ContentValues newFriend = new ContentValues();
            newFriend.put("fNAME", fName);
            newFriend.put("lNAME", lName);
            newFriend.put("Gender", gender);
            newFriend.put("Age", age);
            newFriend.put("Address", address);
            newFriend.put("Image", image);

            Log.e("ADD ROW IMAGE: ", String.valueOf(image));

            try {
                db.insertOrThrow(DB_TABLE, null, newFriend);
            } catch (Exception e) {
                Log.e("Error in inserting rows", e.toString());
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    /*   UPDATE ROW IN DB   */
    public boolean updateRow(String id, String fName, String lName, String gender, String age, String address, byte[] image) {
        synchronized (this.db) {
            ContentValues updateFriend = new ContentValues();
            updateFriend.put("fNAME", fName);
            updateFriend.put("lNAME", lName);
            updateFriend.put("Gender", gender);
            updateFriend.put("Age", age);
            updateFriend.put("Address", address);
            updateFriend.put("Image", image);

            try {
                db.update(DB_TABLE, updateFriend, "friendID=" + id, null);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Update Row Error", e.toString());
                return false;
            }
            return true;
        }
    }

    /*   GET ALL ROWS IN DB   */
    public ArrayList<String> retrieveRows() {
        ArrayList<String> friendRows = new ArrayList<String>();
        String[] columns = {"friendID", "fName", "lNAME", "Gender", "Age", "Address"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);
        cursor.moveToFirst();


        while (cursor.isAfterLast() == false) {
            friendRows.add(cursor.getString(0) + "|" + cursor.getString(1) + " " + cursor.getString(2) +
                    "\n" + cursor.getString(3) + ", " + cursor.getString(4) + "\n" + cursor.getString(5));

            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return friendRows;
    }

    /*   GET ID OF ROW IN DB   */
    public int getID(String text) {
        String id = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '|') {
                for (int n = 0; n < i; n++) { id += text.charAt(n); }
                break;
            }
        }
        return Integer.parseInt(id);
    }

    /*   GET FIRST NAME OF ROW IN DB   */
    public String getFName(String id) {
        String fName = "";
        String[] columns = {"fName"};
        Cursor cursor = db.query(DB_TABLE, new String[] { "friendID",
                        "fName"}, "friendID" + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            fName = cursor.getString(1);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) { cursor.close(); }

        return fName;
    }

    /*   GET LAST NAME OF ROW IN DB   */
    public String getLName(String id) {
        String lName = "";
        String[] columns = {"lName"};
        Cursor cursor = db.query(DB_TABLE, new String[] { "friendID",
                        "lName"}, "friendID" + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            lName = cursor.getString(1);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) { cursor.close(); }

        return lName;
    }

    /*   GET GENDER OF ROW IN DB   */
    public String getGender(String id) {
        String gender = "";
        String[] columns = {"Gender"};
        Cursor cursor = db.query(DB_TABLE, new String[] { "friendID",
                        "Gender"}, "friendID" + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            gender = cursor.getString(1);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) { cursor.close(); }

        return gender;
    }

    /*   GET AGE OF ROW IN DB   */
    public String getAge(String id) {
        String age = "";
        String[] columns = {"Age"};
        Cursor cursor = db.query(DB_TABLE, new String[] { "friendID",
                        "Age"}, "friendID" + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            age = cursor.getString(1);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) { cursor.close(); }

        return age;
    }

    /*   GET ADDRESSOF ROW IN DB   */
    public String getAddress(String id) {
        String address = "";
        String[] columns = {"Address"};
        Cursor cursor = db.query(DB_TABLE, new String[] { "friendID",
                        "Address"}, "friendID" + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            address = cursor.getString(1);
            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) { cursor.close(); }

        return address;
    }

    //retrive all images in DB
    public ArrayList<Bitmap> retrieveImages() {
        ArrayList<Bitmap> images = new ArrayList<>();
        String[] columns = {"Image"};
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null);

        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(0), 0, cursor.getBlob(0).length);
            //System.out.println("retrieve images: " + bitmap);
            images.add(bitmap);

            cursor.moveToNext();
        }
        if (cursor != null && !cursor.isClosed()) { cursor.close(); }

        return images;
    }

    public void clearRecords() {
        db = helper.getWritableDatabase();
        db.delete(DB_TABLE, null, null);
    }

    public boolean clearRecord(int id) {
        db = helper.getWritableDatabase();
        return db.delete(DB_TABLE, "friendID" + "=" + id, null) > 0;
    }

    public static class SQLHelper extends SQLiteOpenHelper {
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

        public Bitmap getImage(String id) { //getting image in DB
            SQLiteDatabase db = this.getReadableDatabase();
            System.out.println("getImage ID: " + id);

            //db.query()
            Cursor cursor = db.query(DB_TABLE, new String[] { "friendID",
                            "Image"}, "friendID" + "=?",
                    new String[] { String.valueOf(id) }, null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();

            byte[] blob = cursor.getBlob(1);
            Bitmap image = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            //Bitmap image = StringToBitMap();
            System.out.println("Bitmap image: " + image);
            return image;
        }

        public String BitMapToString(Bitmap bitmap){    //converting bitmap to string
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b=baos.toByteArray();
            String temp= Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        }

        public Bitmap StringToBitMap(String encodedString){ //converting string to bitmap
            try{
                byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
                Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            }catch(Exception e){
                e.getMessage();
                return null;
            }
        }
    }
}
