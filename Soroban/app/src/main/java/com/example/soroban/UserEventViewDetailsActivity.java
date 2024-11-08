package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.model.Event;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserEventViewDetailsActivity extends AppCompatActivity {
    private Event event;
    private TextView eventName;
    private TextView eventDate;
    private ImageView eventPoster;
    private TextView eventDescription;
    private Button joinWaitingList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_event_view_details);

        // Retrieve event data from intent
        event = (Event) getIntent().getSerializableExtra("eventData");
        if (event == null) {
            Toast.makeText(this, "Event data not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventPoster = findViewById(R.id.eventPoster);
        eventDescription = findViewById(R.id.eventDescription);
        joinWaitingList = findViewById(R.id.buttonJoinWaitingList);

        // Set event details
        eventName.setText(event.getEventName());
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(event.getEventDate());
        eventDate.setText(formattedDate);
        eventDescription.setText(event.getEventDetails());

        // Placeholder for event poster (assuming a method to set image exists)
        eventPoster.setImageDrawable(getResources().getDrawable(R.drawable.ic_event_image));

        // Handle Join Waiting List button click
        joinWaitingList.setOnClickListener(v -> {

            // implementation in progress, update Firebase
            Toast.makeText(this, "Added to waiting list", Toast.LENGTH_SHORT).show();
        });
    }
}
