package com.example.assignment1_personalmanagementapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity {
    EditText eventName;
    EditText eventLocation;
    DatePicker eventDate;
    TimePicker eventTime;
    EventDatabaseManager eventDB;

    Boolean isUpdate = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        setTitle("Add an event");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        eventDB = new EventDatabaseManager(this);

        eventName = (EditText) findViewById(R.id.editTextEventName);
        eventLocation = (EditText) findViewById(R.id.editTextEventLocation);
        eventDate = (DatePicker) findViewById(R.id.DatePicker);
        eventTime = (TimePicker) findViewById(R.id.TimePicker);

        isUpdate = getIntent().getBooleanExtra("isUpdate", false);  //get isUpdate

        if (isUpdate) {
            int eventID = getIntent().getIntExtra("eventID", 0);    //get parsed id
            eventDB.openReadable();

            eventName.setText(eventDB.getName(String.valueOf(eventID)));
            eventLocation.setText(eventDB.getLocation(String.valueOf(eventID)));

            //get values from datetime
            SimpleDateFormat hourFormatter = new SimpleDateFormat("hh");
            String hourStr = hourFormatter.format(eventDB.getDateTime(String.valueOf(eventID)));

            SimpleDateFormat minuteFormatter = new SimpleDateFormat("mm");
            String minuteStr = minuteFormatter.format(eventDB.getDateTime(String.valueOf(eventID)));

            SimpleDateFormat dayFormatter = new SimpleDateFormat("dd");
            String dayStr = dayFormatter.format(eventDB.getDateTime(String.valueOf(eventID)));

            SimpleDateFormat monthFormatter = new SimpleDateFormat("MM");
            String monthStr = monthFormatter.format(eventDB.getDateTime(String.valueOf(eventID)));

            SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
            String yearStr = yearFormatter.format(eventDB.getDateTime(String.valueOf(eventID)));

            //setting values into time and date picker
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr) - 1;
            int day = Integer.parseInt(dayStr);
            eventDate.init(year, month, day, null);
            eventTime.setHour(Integer.parseInt(hourStr));
            eventTime.setMinute(Integer.parseInt(minuteStr));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /* navigation for activities */
            case android.R.id.home:
                Intent intent1 = new Intent(AddEventActivity.this, EventActivity.class);
                startActivity(intent1);
                finish();
                return true;
            case R.id.button_Done:
                if (!isUpdate)
                    submitForm();
                else
                    submitUpdate();
                Intent intent2 = new Intent(AddEventActivity.this, EventActivity.class);
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void submitForm() {
        int day, month, year;
        int hour, minute;
        hour = eventTime.getHour();
        minute = eventTime.getMinute();

        day = eventDate.getDayOfMonth();
        month = eventDate.getMonth() + 1;   //months start at 0, add 1 to offset this
        year = eventDate.getYear();

        String date = day + "/" + month + "/" + year + " " + hour + ":" + minute + ":" + "00";

        try {   //getting date values using formatter and addrow with dateOBJ
            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date dateObj = formater.parse(date);
            eventDB.addRow(eventName.getText().toString(), eventLocation.getText().toString(), dateObj.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void submitUpdate() {    //same as submitform, except updating row
        int day, month, year;
        int hour, minute;
        hour = eventTime.getHour();
        minute = eventTime.getMinute();

        day = eventDate.getDayOfMonth();
        month = eventDate.getMonth() - 1;
        year = eventDate.getYear();

        String date = day + "/" + month + "/" + year + " " + hour + ":" + minute + ":" + "00";

        try {
            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date dateObj = formater.parse(date);

            eventDB.updateRow(String.valueOf(getIntent().getIntExtra("eventID", 0)), eventName.getText().toString(), eventLocation.getText().toString(), dateObj.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}