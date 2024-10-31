package com.example.soroban;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class UserDashboardActivity extends AppCompatActivity {
    private User appUser;
    private ListView waitlistedEventsListView;
    private EventList waitlistedEventsListData;
    private EventArrayAdapter waitlistedAdapter;
    private ListView confirmedEventsListView;
    private EventList confirmedEventsListData;
    private EventArrayAdapter confirmedAdapter;
    private FirebaseFirestore db;
    private CollectionReference userRf;

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

        db = FirebaseFirestore.getInstance();
        userRf = db.collection("users");

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

        // Grab event data from appUser
        waitlistedEventsListData = appUser.getWaitList();
        confirmedEventsListData = appUser.getRegisteredEvents();

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

        // References for waitlisted and confirmed events ListViews
        waitlistedEventsListView = findViewById(R.id.list_waitlisted_events);
        confirmedEventsListView = findViewById(R.id.list_confirmed_events);

        // Set up ArrayAdapters for both ListViews
        waitlistedAdapter = new EventArrayAdapter(this,waitlistedEventsListData);
        confirmedAdapter = new EventArrayAdapter(this, confirmedEventsListData);

        waitlistedEventsListView.setAdapter(waitlistedAdapter);
        confirmedEventsListView.setAdapter(confirmedAdapter);

        // Notification icon click listener
        findViewById(R.id.icon_notifications).setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        userRf.document(appUser.getDeviceId()).collection("waitList").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    waitlistedEventsListData.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {

                        String eventName = doc.getString("eventName");
                        Date eventDate = doc.getDate("eventDate");
                        Date drawDate = doc.getDate("drawDate");
                        Integer sampleSize = (Integer) doc.get("sampleSize");
                        Log.d("Firestore", "Event fetched");
                        waitlistedEventsListData.add(new Event(appUser, appUser.getFacility(), eventName, eventDate, drawDate, sampleSize));
                    }
                    waitlistedAdapter.notifyDataSetChanged();
                }
            }
        });

        userRf.document(appUser.getDeviceId()).collection("registeredEvents").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    confirmedEventsListData.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {

                        String eventName = doc.getString("eventName");
                        Date eventDate = doc.getDate("eventDate");
                        Date drawDate = doc.getDate("drawDate");
                        Integer sampleSize = (Integer) doc.get("sampleSize");
                        Log.d("Firestore", "Event fetched");
                        confirmedEventsListData.add(new Event(appUser, appUser.getFacility(), eventName, eventDate, drawDate, sampleSize));
                    }
                    confirmedAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}
