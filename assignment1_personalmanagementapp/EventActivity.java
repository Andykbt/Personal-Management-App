package com.example.assignment1_personalmanagementapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    Boolean canEditRow = false;
    ListView upComingEventsList;
    ListView pastEventsList;
    EventDatabaseManager eventDB;
    TextView textView_Remove;
    TextView textView_Edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        setTitle("Events");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventDB = new EventDatabaseManager(this);
        upComingEventsList = (ListView) findViewById(R.id.ListView_UpcomingEvents);
        pastEventsList = (ListView) findViewById(R.id.ListView_PastEvents);
        textView_Remove = (TextView) findViewById(R.id.textView_EventRemove);
        textView_Remove.setVisibility(View.GONE);
        textView_Edit = (TextView) findViewById(R.id.textView_EventEdit);

        showRec();
    }

    public void editMode(List<String> values) {
        canEditRow = !canEditRow;   //alternate editmode variable
        if (canEditRow) //shows remove button if editmode is true
            textView_Remove.setVisibility(View.VISIBLE);
        else
            textView_Remove.setVisibility(View.GONE);

        /*   show boxes in upcomingeventslist if editmode true   */
        for(int i = 0; i <= upComingEventsList.getLastVisiblePosition() - upComingEventsList.getFirstVisiblePosition(); i++) {
            CheckBox mCheckbox = (CheckBox) upComingEventsList.getChildAt(i).findViewById(R.id.checkBox_Event);
            if (canEditRow)
                mCheckbox.setVisibility(View.VISIBLE);
            else
                mCheckbox.setVisibility(View.GONE);
        }

        /*   show boxes in pasteventslist if editmode true   */
        for (int i = 0; i <= pastEventsList.getLastVisiblePosition() - pastEventsList.getFirstVisiblePosition(); i++) {
            CheckBox mCheckbox = (CheckBox) pastEventsList.getChildAt(i).findViewById(R.id.checkBox_Event);
            if (canEditRow)
                mCheckbox.setVisibility(View.VISIBLE);
            else
                mCheckbox.setVisibility(View.GONE);
        }
    }

    public void showRec() {
        eventDB.openReadable();

        ArrayList<String> eventContent = eventDB.retrieveIDs(); //get all ids from database
        ArrayList<String> pastEventsArray = new ArrayList<String>();    //declare array for pastEvents
        ArrayList<String> upComingEventsArray = new ArrayList<String>();    //declare array for futureEvents

        Date d = Calendar.getInstance().getTime();  //gets current time
        long currentTime = d.getTime();

        //loops through all events, if events time is more than current time, it is upcoming event, else it is past event
        for (int i = 0; i < eventContent.size(); i++) {
            long eventRowDateTime = eventDB.getDateTime(eventContent.get(i));
            if (eventRowDateTime < currentTime) {
                pastEventsArray.add(eventContent.get(i));
            } else
                upComingEventsArray.add(eventContent.get(i));
        }

        //array adapters for both upcoming events and past events
        ArrayAdapter<String> upcomingAdapter = new CustomAdapter(EventActivity.this, upComingEventsArray, true);
        ArrayAdapter<String> pastAdapter = new CustomAdapter(EventActivity.this, pastEventsArray, false);

        //setting listView adapters
        upComingEventsList.setAdapter(upcomingAdapter);
        pastEventsList.setAdapter(pastAdapter);
    }

    public boolean clearRec() { //clears records from db
        eventDB.clearRecords();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        /* navigation for activities */
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.addButton:
                Intent intent = new Intent(EventActivity.this, AddEventActivity.class);
                startActivity(intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) { //set the menu layout
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public class CustomAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;
        private boolean isUpcoming;

        public CustomAdapter(Context context, ArrayList<String> values, boolean isUpcoming) {
            super(context, R.layout.rowlayout, values);
            this.context = context;
            this.values = values;
            this.isUpcoming = isUpcoming;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rowlayout_event, parent, false);

            //Getting info from the array
            String eventName = eventDB.getName(values.get(position));
            String eventLocation = eventDB.getLocation(values.get(position));
            long eventTime = eventDB.getDateTime(values.get(position));

            //getting containers for row
            TextView textView = (TextView) rowView.findViewById(R.id.textView_EventName);
            TextView dateView = (TextView) rowView.findViewById(R.id.textView_EventTime);
            CheckBox rowEventCheckbox = (CheckBox) rowView.findViewById(R.id.checkBox_Event);
            rowEventCheckbox.setVisibility(View.GONE);  //hide the checkbox for deletion

            //setting dataformater
            SimpleDateFormat postFormatter = new SimpleDateFormat("hh:mm a\ndd/MM/yyyy ");
            String newDateStr = postFormatter.format(eventTime);

            TextView hidden = (TextView) rowView.findViewById(R.id.textView_Hidden);
            hidden.setText(values.get(position) + ")" + eventName + " @ " + eventLocation);
            textView.setText(eventName + " @ " + eventLocation);
            dateView.setText(newDateStr);

            textView_Remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Integer> deleteIDs = new ArrayList<>();
                    //search through upcomingEventsList and pastEventsList, if row checkbox is checked, get ID and save to deleteIDs;
                    for (int i = 0; i <= upComingEventsList.getLastVisiblePosition() - upComingEventsList.getFirstVisiblePosition(); i++) {
                        CheckBox mCheckbox = (CheckBox) upComingEventsList.getChildAt(i).findViewById(R.id.checkBox_Event);
                        TextView mTextView = (TextView) upComingEventsList.getChildAt(i).findViewById(R.id.textView_Hidden);
                        String txt = mTextView.getText().toString();

                        String tmp = mTextView.getText().toString();
                        int id = eventDB.getID(tmp);

                        if (mCheckbox.isChecked()) {
                            deleteIDs.add(id);
                        }

                    }

                    for (int i = 0; i <= pastEventsList.getLastVisiblePosition() - pastEventsList.getFirstVisiblePosition(); i++) {
                        CheckBox mCheckbox = (CheckBox) pastEventsList.getChildAt(i).findViewById(R.id.checkBox_Event);
                        TextView mTextView = (TextView) pastEventsList.getChildAt(i).findViewById(R.id.textView_Hidden);
                        String txt = mTextView.getText().toString();

                        String tmp = mTextView.getText().toString();
                        int id = eventDB.getID(tmp);

                        if (mCheckbox.isChecked()) {
                            deleteIDs.add(id);
                        }
                    }

                    //delete given ids from database
                    for (int i = 0; i < deleteIDs.size(); i++)
                        eventDB.clearRecord(String.valueOf(deleteIDs.get(i)));
                    showRec();  //show records;
                }
            });

            textView_Edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editMode(values);   //editModes
                }
            });

            upComingEventsList.setOnLongClickListener(new AdapterView.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    editMode(values);   //editMode
                    return true;
                }
            });

            upComingEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //updating event
                    String item = (String) upComingEventsList.getAdapter().getItem(i);
                    int id = eventDB.getID(item);   //get the id of the row
                    Intent intent = new Intent(EventActivity.this, AddEventActivity.class);
                    intent.putExtra("eventID", id); //parse id
                    intent.putExtra("isUpdate", true);  //parse isUpdate
                    startActivity(intent);
                    finish();
                }
            });

            pastEventsList.setOnLongClickListener(new AdapterView.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    editMode(values);   //editmode
                    return true;
                }
            });

            pastEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //updating event
                    String item = (String) pastEventsList.getAdapter().getItem(i);
                    int id = eventDB.getID(item);   //get id
                    Intent intent = new Intent(EventActivity.this, AddEventActivity.class);
                    intent.putExtra("eventID", id); //parse id
                    intent.putExtra("isUpdate", true);  //parse isUpdate
                    startActivity(intent);
                    finish();
                }
            });
            return rowView;
        }
    }
}