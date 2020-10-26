package com.example.assignment1_personalmanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 0;

    DatabaseManager mydManager;
    ImageView imageView;
    byte[] byteArray = null;
    boolean updateValues;
    int friendID;

    EditText fName;
    EditText lName;
    EditText Gender;
    EditText Age;
    EditText Address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        imageView = (ImageView) findViewById(R.id.friendImage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Add a friend");
        mydManager = new DatabaseManager(this);

        fName = (EditText) findViewById(R.id.editTextTextFirstName);
        lName = (EditText) findViewById(R.id.editTextTextPersonLastName);
        Gender = (EditText) findViewById(R.id.editTextTextPersonGender);
        Age = (EditText) findViewById(R.id.editTextTextPersonAge);
        Address = (EditText) findViewById(R.id.editTextTextPersonAddress);

        updateValues = getIntent().getBooleanExtra("isUpdate", false);  //get isUpdate variable
        friendID = getIntent().getIntExtra("friendID", 0);  //get friendId, if possible

        if (updateValues) { //is the user is updating a friend, execute code below
            mydManager.openReadable();

            /*  insert data from row into editTexts */
            fName.setText(mydManager.getFName(Integer.toString(friendID)));
            fName.setSelectAllOnFocus(true);

            lName.setText(mydManager.getLName(Integer.toString(friendID)));
            lName.setSelectAllOnFocus(true);
            
            Gender.setText(mydManager.getGender(Integer.toString(friendID)));
            Gender.setSelectAllOnFocus(true);

            Age.setText(mydManager.getAge(Integer.toString(friendID)));
            Age.setSelectAllOnFocus(true);

            Address.setText(mydManager.getAddress(Integer.toString(friendID)));
            Address.setSelectAllOnFocus(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /* navigation for activities */
            case android.R.id.home:
                Intent intent1 = new Intent(AddFriendActivity.this, FriendActivity.class);
                startActivity(intent1);
                finish();
                return true;
            case R.id.button_Done:
                submitForm();
                Intent intent2 = new Intent(AddFriendActivity.this, FriendActivity.class);
                startActivity(intent2);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.formdetailsmenu, menu);
        return true;
    }

    public void addImage(View view) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            Log.e("Error", String.valueOf(e));
        }
    }

    /*   getting image from camera   */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            byteArray = extras.getByteArray("picture");
        } else {
            System.out.println("ON ACTIVITY RESULT NOT WORKING");
        }
    }

    public void submitForm() {
        if (updateValues) { //if updating row
            if (byteArray == null) {    //get the image
                ByteArrayOutputStream ba = new ByteArrayOutputStream();
                Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, ba);
                byteArray = ba.toByteArray();
            }
            
            try {   //try to updateRow
                mydManager.updateRow(String.valueOf(friendID), fName.getText().toString(), lName.getText().toString(), Gender.getText().toString(), Age.getText().toString(), Address.getText().toString(), byteArray);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Add update error: ", e.toString());
            }

            finish();
        } else {
            if (!fName.getText().toString().matches("")) {  //check fname is empty
                if (byteArray == null) {    //get image
                    ByteArrayOutputStream ba = new ByteArrayOutputStream();
                    Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, ba);
                    byteArray = ba.toByteArray();
                }

                try {   //try to addRow
                    mydManager.addRow(fName.getText().toString(), lName.getText().toString(), Gender.getText().toString(), Age.getText().toString(), Address.getText().toString(), byteArray);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Add row error: ", String.valueOf(e));
                }
            } else {
                Toast.makeText(this, "You did not enter a friend", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
}