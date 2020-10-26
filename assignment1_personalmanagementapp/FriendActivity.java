package com.example.assignment1_personalmanagementapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    DatabaseManager mydManager;
    DatabaseManager.SQLHelper helper;
    ListView friendsList;
    CheckBox checkBox;
    TextView textView_Edit;
    TextView textView_Remove;
    View rowView;
    boolean canEditRow = false;
    private List<String> output = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        setTitle("Friends list");   //set title to Friends List

        //Action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //add the back button to actionbar

        mydManager = new DatabaseManager(this); //declare databaseManager
        helper = new DatabaseManager.SQLHelper(this);   //delcare dbHelper
        friendsList = (ListView) findViewById(R.id.ListView_UpcomingEvents);

        //clearRec();
        showRec();  //show records
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //home button pressed -> finish activity
                finish();
                return true;
            case R.id.addButton:    //go to new addFriendActivity
                Intent intent = new Intent(FriendActivity.this, AddFriendActivity.class);
                startActivity(intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean showRec() {
        mydManager.openReadable();

        ArrayList<String> tableContent = mydManager.retrieveRows(); //retrieve all rows from the database
        ArrayAdapter<String> arrAdapter = new CustomAdapter(this, tableContent);    //custom adapter to customise data into listview
        friendsList.setAdapter(arrAdapter); //set adapter to listview

        mydManager.close();
        return true;
    }

    public boolean clearRec() { //clears all records from db
        mydManager.clearRecords();
        return true;
    }

    public void editMode(List<String> values) { //when edit button pressed, shows checkboxes
        canEditRow = !canEditRow;

        if (canEditRow) {
            textView_Remove.setVisibility(View.VISIBLE);
            for (int i = 0; i <= friendsList.getLastVisiblePosition() - friendsList.getFirstVisiblePosition(); i++) {
                CheckBox mCheckBox = (CheckBox) friendsList.getChildAt(i).findViewById(R.id.addTask_Checkbox);
                mCheckBox.setVisibility(View.VISIBLE);
            }
        } else {
            textView_Remove.setVisibility(View.GONE);
            for (int i = 0; i <= friendsList.getLastVisiblePosition() - friendsList.getFirstVisiblePosition(); i++) {
                CheckBox mCheckBox = (CheckBox) friendsList.getChildAt(i).findViewById(R.id.addTask_Checkbox);
                mCheckBox.setVisibility(View.GONE);
            }
        }
    }

    class CustomAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;

        public CustomAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.rowlayout, values);
            this.context = context;
            this.values = values;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rowlayout, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.label);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.friendImage);
            checkBox = (CheckBox) rowView.findViewById(R.id.addTask_Checkbox);
            checkBox.setVisibility(View.GONE);

            String info = values.get(position); //get the info from the arraylist, note: arraylist gets all columns from row

            String id = ""; //extract id portion
            for (int i = 0; i < values.get(position).length(); i++) {
                if (info.charAt(i) == '|') {
                    for (int n = 0; n < i; n++) { id += info.charAt(n); }
                    break;
                }
            }

            boolean found = false;
            String info1 = "";  //save everything besides id in info1
            for (int i = 0; i < values.get(position).length(); i++) {
                if (found) { info1 += info.charAt(i); }
                if (info.charAt(i) == '|') { found = true; }
            }

            textView.setText(info1);    //set text to info1, e.i. everything besides id

            try {   //try to setimage
                Bitmap image = null;
                image = helper.getImage(id);
                imageView.setImageBitmap(image);
            } catch (Exception e) { Log.e("insert image error: ", e.toString()); }


            /* ---  DELETION OF FRIEND ONCLICK LISTENER  --- */
            textView_Remove = (TextView) findViewById(R.id.textView_Remove);
            textView_Remove.setVisibility(View.GONE);
            textView_Remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                        loops through visible rows in listview, and if row is checked remove row
                     */
                    for(int i = 0; i <= friendsList.getLastVisiblePosition() - friendsList.getFirstVisiblePosition(); i++) {
                        CheckBox mCheckBox = (CheckBox) friendsList.getChildAt(i).findViewById(R.id.addTask_Checkbox);
                        if (mCheckBox.isChecked()) {
                            output.add(values.get(i));

                        } else {
                            output.remove(values.get(i));
                        }
                    }

                    for (int i = 0; i < output.size(); i++) { mydManager.clearRecord(mydManager.getID(output.get(i))); }

                    ArrayList<String> tableContent = mydManager.retrieveRows();
                    ArrayAdapter<String> arrAdapter = new CustomAdapter(FriendActivity.this, tableContent);
                    friendsList.setAdapter(arrAdapter);
                }
            });

            //changes the editMode (showing checkboxes next to friends and remove button)
            textView_Edit = (TextView) findViewById(R.id.textView_Edit);
            textView_Edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editMode(values);
                }
            });

            friendsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                    editMode(values);
                    return true;
                }
            });

            /*   UPDATING FRIEND   */
            //when an item in listview is clicked execute code below
            friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String item = (String) friendsList.getAdapter().getItem(i); //get the info of the row
                    int id = mydManager.getID(item);    //get the id of the row
                    Log.e("getID id: ", String.valueOf(id));
                    Intent intent = new Intent(FriendActivity.this, AddFriendActivity.class);   //declare new intent
                    intent.putExtra("friendID", id);    //parse id
                    intent.putExtra("isUpdate", true);  //parse isUpdating friend
                    startActivity(intent);; //start activity
                    finish();
                }
            });

            return rowView;
        }
    }
}