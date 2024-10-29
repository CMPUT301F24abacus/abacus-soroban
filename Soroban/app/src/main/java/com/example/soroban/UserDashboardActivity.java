package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class UserDashboardActivity extends AppCompatActivity {
    private ListView waitlistedEventsListView;
    private ListView confirmedEventsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_dashboard);

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
