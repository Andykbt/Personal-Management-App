package com.example.assignment1_personalmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

/*
    ALL IMAGES FOUND AT:
    calendar: https://www.flaticon.com/free-icon/calendar_2983719?term=calendar&page=1&position=28
    to-do list: https://www.flaticon.com/free-icon/to-do-list_3014777
    profile icon: https://www.flaticon.com/free-icon/user_747545
    smiley icon: https://www.flaticon.com/free-icon/happy_3084424?term=smiley&page=1&position=26
    add icon: https://www.flaticon.com/free-icon/add_929409?term=plus&page=1&position=14
 */
public class MainActivity extends AppCompatActivity {

    CardView cardView_ToDo;
    CardView cardView_FriendsList;
    CardView cardView_Events;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Cards on click start new activity
        cardView_FriendsList = (CardView) findViewById(R.id.card_view_outer_friend);
        cardView_FriendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FriendActivity.class);
                startActivity(intent);
            }
        });

        cardView_ToDo = (CardView) findViewById(R.id.card_view_outer_todo);
        cardView_ToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ToDoActivity.class);
                startActivity(intent);
            }
        });

        cardView_Events = (CardView) findViewById(R.id.card_view_outer_event);
        cardView_Events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                startActivity(intent);
            }
        });
    }
}