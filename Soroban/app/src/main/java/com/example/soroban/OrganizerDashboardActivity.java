package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class OrganizerDashboardActivity extends AppCompatActivity {

    private TextView facilityNameTextView;
    private RecyclerView eventsRecyclerView;
    private Button createEventButton;
    private Button goToFacilityButton;
    private OrganizerEventAdapter eventAdapter;
    private List<OrganizerEvent> events;

    private static final int CREATE_EVENT_REQUEST_CODE = 1;
    private static final int EDIT_FACILITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_dashboard);

        // Initialize views
        facilityNameTextView = findViewById(R.id.facilityNameTextView);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        createEventButton = findViewById(R.id.createEventButton);
        goToFacilityButton = findViewById(R.id.btn_go_to_facility);

        // Set facility name dynamically
        String facilityName = getIntent().getStringExtra("facilityName");
        facilityNameTextView.setText(facilityName != null ? facilityName : "My Facility");

        // Initialize events list (dummy data for now)
        events = new ArrayList<>();
        events.add(new OrganizerEvent("Event 1", "Jan 1, 2024"));
        events.add(new OrganizerEvent("Event 2", "Feb 15, 2024"));

        // Set up RecyclerView
        eventAdapter = new OrganizerEventAdapter(this, events);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(eventAdapter);

        // Set up "Create Event" button click listener
        createEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerDashboardActivity.this, CreateEventActivity.class);
            startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
        });

        // Set up "Go to My Facility" button click listener
        goToFacilityButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerDashboardActivity.this, FacilityDisplayActivity.class);
            intent.putExtra("facilityName", facilityNameTextView.getText().toString());  // Pass current facility name
            startActivityForResult(intent, EDIT_FACILITY_REQUEST_CODE); // Start for result to receive updated name
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_EVENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Handle the result from CreateEventActivity
            String eventName = data.getStringExtra("eventName");
            String eventDate = data.getStringExtra("eventDate");
            events.add(new OrganizerEvent(eventName, eventDate));
            eventAdapter.notifyDataSetChanged();
        } else if (requestCode == EDIT_FACILITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Handle the updated facility name from FacilityDisplayActivity
            String updatedFacilityName = data.getStringExtra("updatedFacilityName");
            if (updatedFacilityName != null) {
                facilityNameTextView.setText(updatedFacilityName); // Update the displayed facility name
            }
        }
    }
}
