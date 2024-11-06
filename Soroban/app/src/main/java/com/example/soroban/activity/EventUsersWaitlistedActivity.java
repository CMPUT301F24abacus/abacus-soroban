package com.example.soroban.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.R;


public class EventUsersWaitlistedActivity extends AppCompatActivity {
    private Button sendMessage;
    private Button rejectUsers;
    private Button inviteUsers;

    /**
     * Called when this activity is first created.
     * @param savedInstanceState
     * Author: Aaryan Shetty
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_entrants_waitlist);

        // Assign button variables to views
        sendMessage = findViewById(R.id.buttonSendMessageToUsers);
        rejectUsers = findViewById(R.id.buttonRejectUsers);
        inviteUsers = findViewById(R.id.buttonInviteUsers);

        // Set up reactions for when the buttons are clicked
        sendMessage.setOnClickListener(v -> {
            Toast.makeText(this, "WIP - Send message popup", Toast.LENGTH_SHORT).show();
        });

        rejectUsers.setOnClickListener(v -> {
            Toast.makeText(this, "WIP - Implement rejecting users from event", Toast.LENGTH_SHORT).show();
        });

        inviteUsers.setOnClickListener(v -> {
            Toast.makeText(this, "WIP - Implement sending users an invite", Toast.LENGTH_SHORT).show();
        });

    }
}
