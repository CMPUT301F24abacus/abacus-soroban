package com.example.soroban;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class UserDashboardActivity extends AppCompatActivity {
    private User appUser;
    private ListView waitlistedEventsListView;
    private ListView confirmedEventsListView;
    /**
     * Called when the activity is first created.
     * @param savedInstanceState
     * Author: Ayan Chaudhry
     *
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_dashboard);

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

            if(appUser == null){
                throw new IllegalArgumentException("Must pass object of type User to initialize appUser.");
            }
        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }
        
        /**
         * Set up button actions.
         * Buttons are: Scan QR Code, Join Waiting List, Manage Facility
         */

        // Initialize buttons
        Button scanQrCode = findViewById(R.id.btn_scan_qr_code);
        Button joinWaitingList = findViewById(R.id.btn_join_waiting_list);
        Button manageFacility = findViewById(R.id.btn_manage_facility);

        // Set up click listeners for buttons
        scanQrCode.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, QrCodeScanActivity.class);
            startActivity(intent);
        });

        joinWaitingList.setOnClickListener(v -> {
            // Action for Join Waiting List
        });

        manageFacility.setOnClickListener(v -> {
            // Action for Manage Facility
        });

        // Initialize ListViews for waitlisted and confirmed events
        waitlistedEventsListView = findViewById(R.id.list_waitlisted_events);
        confirmedEventsListView = findViewById(R.id.list_confirmed_events);

        // Sample data for waitlisted and confirmed events
        ArrayList<String> waitlistedEvents = new ArrayList<>(Arrays.asList(
                "Event Name, Organized by ABC",
                "Event Name, Organized by DEF",
                "Event Name, Organized by GHI"
        ));
        ArrayList<String> confirmedEvents = new ArrayList<>(Arrays.asList(
                "Event Name, Organized by LMN",
                "Event Name, Organized by OPQ",
                "Event Name, Organized by RST"
        ));

        // Set up ArrayAdapters for both ListViews
        ArrayAdapter<String> waitlistedAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, waitlistedEvents);
        ArrayAdapter<String> confirmedAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, confirmedEvents);

        waitlistedEventsListView.setAdapter(waitlistedAdapter);
        confirmedEventsListView.setAdapter(confirmedAdapter);

        // Notification icon click listener
        findViewById(R.id.icon_notifications).setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }
}
