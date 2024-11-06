package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.activity.EventEntrantsListActivity;

public class OrganizerEventViewDetailsActivity extends AppCompatActivity {
    private ImageView viewQRcode;
    private ImageView eventGeolocation;
    private TextView eventTitle;
    private TextView eventDescription;
    private ImageView eventPoster;
    private TextView eventEntrantLimit;
    private Button geoReq;
    private Button autoReplace;

    private Button viewEntrants;
    private Button editEvent;

    /**
     * Called when this activity is first created.
     * @param savedInstanceState
     * Author: Aaryan Shetty
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_view_event_details);

        // Assign button variables to views
        viewQRcode = findViewById(R.id.buttonScanQRCode);
        eventGeolocation = findViewById(R.id.buttonEventGeolocation);
        eventTitle = findViewById(R.id.eventNameTitle);
        eventDescription = findViewById(R.id.eventDescriptionText);
        eventPoster = findViewById(R.id.eventPosterImage);
        eventEntrantLimit = findViewById(R.id.eventEntrantLimitText);
        geoReq = findViewById(R.id.eventGeoReqSwitch);
        autoReplace = findViewById(R.id.eventAutoReplaceSwitch);

        viewEntrants = findViewById(R.id.buttonViewEntrants);
        editEvent = findViewById(R.id.buttonEditEventDetails);

        // Set up listeners for applicable buttons
        viewQRcode.setOnClickListener(v -> {
            Toast.makeText(this, "WIP - Implement viewing event QR code", Toast.LENGTH_SHORT).show();
        });

        eventGeolocation.setOnClickListener(v -> {
            Toast.makeText(this, "WIP - Implement viewing where entrants are located", Toast.LENGTH_SHORT).show();
        });

        viewEntrants.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerEventViewDetailsActivity.this, EventEntrantsListActivity.class);
            startActivity(intent);
        });

        editEvent.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerEventViewDetailsActivity.this, OrganizerEventEditDetailsActivity.class);
            startActivity(intent);
        });
    }
}
