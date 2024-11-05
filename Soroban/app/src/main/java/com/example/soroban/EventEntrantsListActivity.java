package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EventEntrantsListActivity extends AppCompatActivity {
    private Button usersWaitlisted;
    private Button usersInvited;
    private Button usersCancelled;
    private Button usersConfirmed;

    /**
     * Called when this activity is first created.
     * @param savedInstanceState
     * Author: Aaryan Shetty
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_entrant_list_buttons);

        // Assign button variables to views
        usersWaitlisted = findViewById(R.id.buttonWaitlistedUsers);
        usersInvited = findViewById(R.id.buttonInvitedUsers);
        usersCancelled = findViewById(R.id.buttonCancelledUsers);
        usersConfirmed = findViewById(R.id.buttonConfirmedUsers);

        // Set up reactions for when the buttons are clicked
        usersWaitlisted.setOnClickListener(v -> {
            Intent intent = new Intent(EventEntrantsListActivity.this, EventUsersWaitlisted.class);
            startActivity(intent);
        });

        usersInvited.setOnClickListener(v -> {
            Intent intent = new Intent(EventEntrantsListActivity.this, EventUsersInvited.class);
            startActivity(intent);
        });

        usersCancelled.setOnClickListener(v -> {
            Intent intent = new Intent(EventEntrantsListActivity.this, EventUsersCancelled.class);
            startActivity(intent);
        });

        usersConfirmed.setOnClickListener(v -> {
            Intent intent = new Intent(EventEntrantsListActivity.this, EventUsersConfirmed.class);
            startActivity(intent);
        });

    }
}
