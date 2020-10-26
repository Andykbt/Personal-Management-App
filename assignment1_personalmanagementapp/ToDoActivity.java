package com.example.assignment1_personalmanagementapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class ToDoActivity extends AppCompatActivity {
    TodoTasksDatabaseManager taskManager;
    ListView inCompleteTaskList;
    ListView completeTaskList;
    TextView doneButton;

    ConstraintLayout addTask;
    ConstraintLayout allTasks;
    ConstraintLayout editTasks;

    Boolean isAddTask = false;
    Boolean isEditTask = false;
    Boolean editMode = false;
    TextView editTextView;

    CheckBox checkBox;
    EditText taskName;
    EditText location;
    String taskIsComplete;

    TextView sortTasks;
    TextView removeTasks;

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        setTitle("To do");

        taskManager = new TodoTasksDatabaseManager(this);
        inCompleteTaskList = (ListView) findViewById(R.id.listView_InCompleteTasks);
        completeTaskList = (ListView) findViewById(R.id.listView_CompleteTasks);
        doneButton = (Button) findViewById(R.id.button_Done);

        addTask = (ConstraintLayout) findViewById(R.id.constraintLayout_AddTask);
        addTask.setVisibility(View.GONE);
        allTasks = (ConstraintLayout) findViewById(R.id.constraintLayout_AllTasks);
        editTasks = (ConstraintLayout) findViewById(R.id.constraintLayout_EditTasks);

        checkBox = (CheckBox) findViewById(R.id.addTask_Checkbox);
        taskName = (EditText) findViewById(R.id.editText_TaskName);
        location = (EditText) findViewById(R.id.editText_Location);

        removeTasks = (TextView) findViewById(R.id.textView_TaskDelete);
        removeTasks.setOnClickListener(new View.OnClickListener() { //searches through boths lists and deletes rows that are checked
            @Override
            public void onClick(View view) {
                //goes through all visible rows
                for (int i = 0; i <= inCompleteTaskList.getLastVisiblePosition() - inCompleteTaskList.getFirstVisiblePosition(); i++) {
                    CheckBox mCheckbox = (CheckBox) inCompleteTaskList.getChildAt(i).findViewById(R.id.checkBox_Remove);
                    String item = (String) inCompleteTaskList.getAdapter().getItem(i);
                    if (mCheckbox.isChecked()) {
                        taskManager.clearRow(taskManager.getID(item));  //delete row from DB
                    }
                }
                for (int i = 0; i <= completeTaskList.getLastVisiblePosition() - completeTaskList.getFirstVisiblePosition(); i++) {
                    CheckBox mCheckbox = (CheckBox) completeTaskList.getChildAt(i).findViewById(R.id.checkBox_Remove);
                    String item = (String) completeTaskList.getAdapter().getItem(i);
                    if (mCheckbox.isChecked()) {
                        taskManager.clearRow(taskManager.getID(item));
                    }
                }
                showRec();
            }
        });

        sortTasks = (TextView) findViewById(R.id.textView_TaskSort);
        sortTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //sorting tasks between lists
                //goes through incomplete tasks list and updates row accordingly
                for (int i = 0; i <= inCompleteTaskList.getLastVisiblePosition() - inCompleteTaskList.getFirstVisiblePosition(); i++) {
                    CheckBox mCheckbox = (CheckBox) inCompleteTaskList.getChildAt(i).findViewById(R.id.checkBox_Complete);
                    String item = (String) inCompleteTaskList.getAdapter().getItem(i);
                    if (mCheckbox.isChecked()) {
                        String tName = "";
                        String tLocation = "";
                        for (int n = 0; n < item.length(); n++) {   //getting the name of the event
                            if (item.charAt(n) == ')')
                                for (int j = n + 1; j < item.length(); j++)
                                    if (item.charAt(j) != '@') {
                                        tName += item.charAt(j);
                                    } else {
                                        break;
                                    }
                        }
                        for (int n = 0; n < item.length(); n++) {   //getting the location of the event
                            if (item.charAt(n) == '@')
                                for (int j = n + 1; j < item.length(); j++)
                                    if (item.charAt(j) == '|')
                                        break;
                                    else
                                        tLocation += item.charAt(j);

                        }
                        //update the row
                        taskManager.updateRow(String.valueOf(taskManager.getID(item)), tName, tLocation, "Complete");
                    }
                }
                //goes through complete tasks list and updates row accordingly
                for (int i = 0; i <= completeTaskList.getLastVisiblePosition() - completeTaskList.getFirstVisiblePosition(); i++) {
                    CheckBox mCheckbox = (CheckBox) completeTaskList.getChildAt(i).findViewById(R.id.checkBox_Complete);
                    String item = (String) completeTaskList.getAdapter().getItem(i);
                    if (!mCheckbox.isChecked()) {
                        String tName = "";
                        String tLocation = "";
                        for (int n = 0; n < item.length(); n++) {   //getting the name of the event
                            if (item.charAt(n) == ')')
                                for (int j = n + 1; j < item.length(); j++)
                                    if (item.charAt(j) != '@') {
                                        tName += item.charAt(j);
                                    } else {
                                        break;
                                    }
                        }
                        for (int n = 0; n < item.length(); n++) {   //getting the location of the event
                            if (item.charAt(n) == '@')
                                for (int j = n + 1; j < item.length(); j++)
                                    if (item.charAt(j) == '|')
                                        break;
                                    else
                                        tLocation += item.charAt(j);

                        }
                        //updaing row
                        taskManager.updateRow(String.valueOf(taskManager.getID(item)), tName, tLocation, "InComplete");
                    }
                }
                showRec();
            }
        });

        editTextView = (TextView) findViewById(R.id.textView_Edit);
        editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editMode = !editMode;
                //shows editmode buttons (remove button, sort button)
                for (int i = 0; i <= completeTaskList.getLastVisiblePosition() - completeTaskList.getFirstVisiblePosition(); i++) {
                    CheckBox mCheckbox = (CheckBox) completeTaskList.getChildAt(i).findViewById(R.id.checkBox_Remove);
                    if (editMode) {
                        removeTasks.setVisibility(View.VISIBLE);
                        sortTasks.setVisibility(View.VISIBLE);
                        mCheckbox.setVisibility(View.VISIBLE);
                    }
                    else {
                        removeTasks.setVisibility(View.GONE);
                        sortTasks.setVisibility(View.GONE);
                        mCheckbox.setVisibility(View.GONE);
                    }
                }
                for (int i = 0; i <= inCompleteTaskList.getLastVisiblePosition() - inCompleteTaskList.getFirstVisiblePosition(); i++) {
                    CheckBox mCheckbox = (CheckBox) inCompleteTaskList.getChildAt(i).findViewById(R.id.checkBox_Remove);
                    if (editMode) {
                        removeTasks.setVisibility(View.VISIBLE);
                        sortTasks.setVisibility(View.VISIBLE);
                        mCheckbox.setVisibility(View.VISIBLE);
                    }
                    else {
                        removeTasks.setVisibility(View.GONE);
                        sortTasks.setVisibility(View.GONE);
                        mCheckbox.setVisibility(View.GONE);
                    }
                }
            }
        });

        //Action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showRec();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /* activities navigation */
            case android.R.id.home:
                finish();
                return true;
            case R.id.addButton:
                isAddTask = true;
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                invalidateOptionsMenu();
                taskName.setText("");
                location.setText("");
                checkBox.setChecked(false);
                addTask.setVisibility(View.VISIBLE);
                editTasks.setVisibility(View.GONE);
                allTasks.setVisibility(View.GONE);
                return true;
            case R.id.button_Done:
                isAddTask = false;
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                invalidateOptionsMenu();
                submitForm();
                addTask.setVisibility(View.GONE);
                editTasks.setVisibility(View.VISIBLE);
                allTasks.setVisibility(View.VISIBLE);
                showRec();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isAddTask || isEditTask) {
            menu.findItem(R.id.addButton).setVisible(false);
            menu.findItem(R.id.button_Done).setVisible(true);
        } else {
            menu.findItem(R.id.addButton).setVisible(true);
            menu.findItem(R.id.button_Done).setVisible(false);
        }

        return true;
    }

    public boolean submitForm() {
        if (isEditTask) {
            if (!taskName.getText().toString().matches("")) {   //checks if task name is empty
                taskManager.openReadable();
                if (checkBox.isChecked())
                    taskIsComplete = "Complete";
                else
                    taskIsComplete = "InComplete";
                //update row in DB
                taskManager.updateRow(Integer.toString(id), taskName.getText().toString(), location.getText().toString(), taskIsComplete);
            } else {
                //show toast that task not entered
                Toast.makeText(this, "You did not enter a task", Toast.LENGTH_SHORT).show();
            }

            isEditTask = false;
        } else {
            if (!taskName.getText().toString().matches("")) {
                taskManager.openReadable();

                if (checkBox.isChecked())
                    taskIsComplete = "Complete";
                else
                    taskIsComplete = "InComplete";

                taskManager.addRow(taskName.getText().toString(), location.getText().toString(), taskIsComplete);
            } else {
                Toast.makeText(this, "You did not enter a task", Toast.LENGTH_SHORT).show();
            }
        }

        return false;
    }

    public boolean showRec() {
        taskManager.openReadable();

        ArrayList<String> taskContent = taskManager.retrieveInCompletedRows();
        ArrayAdapter<String> arrAdapter =  new InCompleteTaskAdapter(this, taskContent);
        inCompleteTaskList.setAdapter(arrAdapter);

        ArrayList<String> completedContent = taskManager.retrieveCompletedRows();
        ArrayAdapter<String> arrayAdapter =  new CompleteTaskAdapter(this, completedContent);
        completeTaskList.setAdapter(arrayAdapter);

        return true;
    }

    class CompleteTaskAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;

        public CompleteTaskAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.rowlayout_task, values);
            this.context = context;
            this.values = values;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rowlayout_task, parent, false);

            CheckBox rowCheckbox = (CheckBox) rowView.findViewById(R.id.checkBox_Complete);
            TextView rowTextView = (TextView) rowView.findViewById(R.id.textView_TaskInfo);

            String info = values.get(position);

            String rowInfo = "";
            String rowID = "";
            String isComplete = "";

            for (int i = 0; i < info.length(); i++) {   //get row id
                if (info.charAt(i) != ')')
                    rowID += info.charAt(i);
                else
                    break;
            }

            for (int i = 0; i < info.length(); i++) {   //get row info
                if (info.charAt(i) == ')')
                    for (int n = i + 1; n < info.length(); n++) {
                        if (info.charAt(n) == '|')
                            break;
                        else
                            rowInfo += info.charAt(n);
                    }
            }

            for (int i = 0; i < info.length(); i++) {   //get isComplete
                if (info.charAt(i) == '|') {
                    for (int n = i + 1; n < info.length(); n++)
                        isComplete += info.charAt(n);
                    break;
                }
            }

            rowCheckbox.setChecked(true);   //Complete tasks have checkbox as checked
            rowTextView.setText(rowInfo);   //sets textView

            completeTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    isEditTask = true;
                    /*  edits row if item is clicked */
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    invalidateOptionsMenu();
                    String item = (String) completeTaskList.getAdapter().getItem(i);
                    CheckBox cb = (CheckBox) findViewById(R.id.addTask_Checkbox);
                    cb.setChecked(true);
                    id = taskManager.getID(item);

                    String tName = "";
                    String tLocation = "";
                    for (int n = 0; n < item.length(); n++) {   //get task name
                        if (item.charAt(n) == ')')
                            for (int j = n + 1; j < item.length(); j++)
                                if (item.charAt(j) != '@')
                                    tName += item.charAt(j);
                                else
                                    break;
                    }
                    for (int n = 0; n < item.length(); n++) {   //get locatoin
                        if (item.charAt(n) == '@')
                            for (int j = n + 1; j < item.length(); j++)
                                if (item.charAt(j) == '|')
                                    break;
                                else
                                    tLocation += item.charAt(j);

                    }
                    taskName.setText(tName);
                    location.setText(tLocation);

                    allTasks.setVisibility(View.GONE);
                    editTasks.setVisibility(View.GONE);
                    addTask.setVisibility(View.VISIBLE);
                }
            });

            return rowView;
        }
    }

    class InCompleteTaskAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;

        public InCompleteTaskAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.rowlayout_task, values);
            this.context = context;
            this.values = values;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.rowlayout_task, parent, false);
            CheckBox rowCheckbox = (CheckBox) rowView.findViewById(R.id.checkBox_Complete);
            TextView rowTextView = (TextView) rowView.findViewById(R.id.textView_TaskInfo);
            String info = values.get(position);

            String rowInfo = "";
            String rowID = "";
            String isComplete = "";


            for (int i = 0; i < info.length(); i++) {   //get id
                if (info.charAt(i) != ')')
                    rowID += info.charAt(i);
                else
                    break;
            }

            for (int i = 0; i < info.length(); i++) {   //get row Info
                if (info.charAt(i) == ')')
                    for (int n = i+1; n < info.length(); n++) {
                        if (info.charAt(n) == '|')
                            break;
                        else
                            rowInfo += info.charAt(n);
                    }
            }

            for (int i = 0; i < info.length(); i++) {   //get is Complete
                if (info.charAt(i) == '|') {
                    for (int n = i + 1; n < info.length(); n++)
                        isComplete += info.charAt(n);
                    break;
                }
            }

            rowCheckbox.setChecked(false);  //incomplete tasks will have checkbox unchecked
            rowTextView.setText(rowInfo);   //set textView

            inCompleteTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    isEditTask = true;
                    /*  edits row if item is clicked */
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    invalidateOptionsMenu();
                    String item = (String) inCompleteTaskList.getAdapter().getItem(i);
                    CheckBox cb = (CheckBox) findViewById(R.id.addTask_Checkbox);
                    cb.setChecked(false);
                    id = taskManager.getID(item);

                    String tName = "";
                    String tLocation = "";
                    for (int n = 0; n < item.length(); n++) {
                        if (item.charAt(n) == ')')
                            for (int j = n + 1; j < item.length(); j++)
                                if (item.charAt(j) != '@')
                                    tName += item.charAt(j);
                                else
                                    break;
                    }
                    for (int n = 0; n < item.length(); n++) {
                        if (item.charAt(n) == '@')
                            for (int j = n + 1; j < item.length(); j++)
                                if (item.charAt(j) == '|')
                                    break;
                                else
                                    tLocation += item.charAt(j);

                    }
                    taskName.setText(tName);
                    location.setText(tLocation);

                    allTasks.setVisibility(View.GONE);
                    editTasks.setVisibility(View.GONE);
                    addTask.setVisibility(View.VISIBLE);
                }
            });

            return rowView;
        }
    }
}