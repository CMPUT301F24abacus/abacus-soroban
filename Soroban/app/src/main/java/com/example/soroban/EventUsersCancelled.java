package com.example.soroban;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EventUsersCancelled extends AppCompatActivity {
    private Button sendMessage;

    /**
     * Called when this activity is first created.
     * @param savedInstanceState
     * Author: Aaryan Shetty
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_entrants_cancelled);

        // Assign button variables to views
        sendMessage = findViewById(R.id.buttonSendMessageToUsers);

        // Set up reactions for when the buttons are clicked
        sendMessage.setOnClickListener(v -> {
            Toast.makeText(this, "WIP - Send message popup", Toast.LENGTH_SHORT).show();
        });
    }
}
