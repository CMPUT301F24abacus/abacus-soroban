package com.example.soroban.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.fragment.ViewProfileFragment;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class UserDashboardActivity extends AppCompatActivity {
    private User appUser;
    private FireBaseController fireBaseController;
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
     * Author: Ayan Chaudhry, Matthieu Larochelle, Kevin Li
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

            if(appUser == null ){
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

        // Initialize buttons and
        Button scanQrCode = findViewById(R.id.btn_scan_qr_code);
        ImageView profileIcon = findViewById(R.id.icon_profile);
        ImageView notificationsIcon = findViewById(R.id.icon_notifications);

        // Set up click listeners for buttons
        scanQrCode.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, QrCodeScanActivity.class);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            //Action for Profile Icon
            ViewProfileFragment dialogFragment = ViewProfileFragment.newInstance(appUser);
            dialogFragment.show(getSupportFragmentManager(), "View profile");
        });

        notificationsIcon.setOnClickListener(v -> {
            // Action for notification Icon
            Intent intent = new Intent(UserDashboardActivity.this, NotificationActivity.class);
            startActivity(intent);
        });


        // References for waitlisted and confirmed events ListViews
        waitlistedEventsListView = findViewById(R.id.list_waitlisted_events);
        confirmedEventsListView = findViewById(R.id.list_confirmed_events);

        // Set up ArrayAdapters for both ListViews
        waitlistedAdapter = new EventArrayAdapter(this,waitlistedEventsListData);
        confirmedAdapter = new EventArrayAdapter(this, confirmedEventsListData);

        waitlistedEventsListView.setAdapter(waitlistedAdapter);
        confirmedEventsListView.setAdapter(confirmedAdapter);


    }
}
