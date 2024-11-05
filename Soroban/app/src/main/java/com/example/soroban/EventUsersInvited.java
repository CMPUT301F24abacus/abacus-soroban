package com.example.soroban;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EventUsersInvited extends AppCompatActivity {
    private Button sendMessage;
    private Button withdrawInvite;

    /**
     * Called when this activity is first created.
     * @param savedInstanceState
     * Author: Aaryan Shetty
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_entrants_invited);

        // Assign button variables to views
        sendMessage = findViewById(R.id.buttonSendMessageToUsers);
        withdrawInvite = findViewById(R.id.buttonWithdrawUsersInvite);

        // Set up reactions for when the buttons are clicked
        sendMessage.setOnClickListener(v -> {
            Toast.makeText(this, "WIP - Send message popup", Toast.LENGTH_SHORT).show();
        });

        withdrawInvite.setOnClickListener(v -> {
            Toast.makeText(this, "WIP - Implement withdrawing invites from users", Toast.LENGTH_SHORT).show();
        });
    }
}
