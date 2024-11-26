package com.example.soroban.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.R;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.User;

import java.util.Calendar;

/**
 * Serves as the main dashboard for organizers.
 * This activity allows the user to view hosted events, create new events, or manage their facility profile.
 * @author
 * @see CreateEventActivity
 * @see FacilityDisplayActivity
 * @see EventArrayAdapter
 * @see Event
 * @see User
 * @see OrganizerEventViewDetailsActivity
 */
public class OrganizerDashboardActivity extends AppCompatActivity {

    private User appUser;
    private TextView facilityNameTextView;
    private ListView eventsListView;
    private Button createEventButton;
    private Button goToFacilityButton;
    private EventArrayAdapter eventAdapter;
    private EventList events;

    private static final int CREATE_EVENT_REQUEST_CODE = 1;
    private static final int EDIT_FACILITY_REQUEST_CODE = 2;

    /**
     * Called when this activity is first created.
     * Initializes views, sets up event listeners, and populates the organizer's events.
     *
     * @param savedInstanceState The saved state of the activity.
     * @throws IllegalArgumentException if required arguments are not provided.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_dashboard);

        // Get arguments passed from previous activity.
        // Reference: https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter
        Bundle args = getIntent().getExtras();

        // Initialize appUser for this activity.
        if(args != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
            }

            if(appUser == null ){
                throw new IllegalArgumentException("Must pass object of type User to initialize appUser.");
            }
        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }


        // Initialize views
        facilityNameTextView = findViewById(R.id.facilityNameTextView);
        eventsListView = findViewById(R.id.eventsListView);
        createEventButton = findViewById(R.id.createEventButton);
        goToFacilityButton = findViewById(R.id.btn_go_to_facility);

        // Set facility name dynamically
        String facilityName = getIntent().getStringExtra("facilityName");
        facilityNameTextView.setText(facilityName != null ? facilityName : "My Facility");

        // Initialize events list
        events = appUser.getHostedEvents();

        // Set up RecyclerView
        eventAdapter = new EventArrayAdapter(this, events);
        eventsListView.setAdapter(eventAdapter);

        // Set up click listener for organizer's events
        eventsListView.setOnItemClickListener((parent,view,position,id)->{
            Intent intent = new Intent(OrganizerDashboardActivity.this, OrganizerEventViewDetailsActivity.class);
            Event selectedEvent = events.get(position);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser", appUser);
            newArgs.putSerializable("selectedEvent", selectedEvent);
            intent.putExtras(newArgs);
            startActivity(intent);
        });

        // Set up "Create Event" button click listener
        createEventButton.setOnClickListener(v -> {
            Intent intent;
            if(appUser.getFacility() != null){
                intent = new Intent(OrganizerDashboardActivity.this, CreateEventActivity.class);
            }else{
                // The user must create their facility in order to organize events
                intent = new Intent(OrganizerDashboardActivity.this, CreateFacilityActivity.class);
            }
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser",appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
        });

        // Set up "Go to My Facility" button click listener
        goToFacilityButton.setOnClickListener(v -> {
            Intent intent;
            if(appUser.getFacility() != null){
                intent = new Intent(OrganizerDashboardActivity.this, FacilityDisplayActivity.class);
            }else{
                // The user must first create their facility
                intent = new Intent(OrganizerDashboardActivity.this, CreateFacilityActivity.class);
            }
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser",appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
        });
    }
}
