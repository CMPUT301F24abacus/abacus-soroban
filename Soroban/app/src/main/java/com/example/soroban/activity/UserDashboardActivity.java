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
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Map;

/**
 * Displays the user's dashboard with options to scan QR codes,
 * view notifications, manage preferences, and browse waitlisted and confirmed events.
 * References: StackOverflow
 *
 * @author Ayan Chaudhry
 * @author Matthieu Larochelle
 * @author Kevin Li
 * @see QrCodeScanActivity
 * @see InvitationActivity
 * @see PreferencesActivity
 * @see Event
 * @see EventArrayAdapter
 * @see User
 * @see UserEventActivity
 */
public class UserDashboardActivity extends AppCompatActivity {
    private User appUser;
    private FireBaseController fireBaseController;
    private FirebaseFirestore db;
    private ListView waitlistedEventsListView;
    private EventList waitlistedEventsListData;
    private EventArrayAdapter waitlistedAdapter;
    private ListView confirmedEventsListView;
    private EventList confirmedEventsListData;
    private EventArrayAdapter confirmedAdapter;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState the saved state of the activity
     * @throws IllegalArgumentException if the required User object is not passed.
     * Author: Ayan Chaudhry, Matthieu Larochelle, Kevin Li
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

            if(appUser == null ){
                throw new IllegalArgumentException("Must pass object of type User to initialize appUser.");
            }
        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        // Grab event data from appUser
        waitlistedEventsListData = new EventList();
        confirmedEventsListData = new EventList();
        db = FirebaseFirestore.getInstance();
        fireBaseController = new FireBaseController(this);

        /**
         * Set up button actions.
         * Buttons are: Scan QR Code, Join Waiting List, Manage Facility
         */

        // Initialize buttons and
        Button scanQrCode = findViewById(R.id.btn_scan_qr_code);
        ImageView profileIcon = findViewById(R.id.icon_profile);
        ImageView notificationsIcon = findViewById(R.id.icon_notifications);
        ImageView preferencesIcon = findViewById(R.id.icon_settings);

        // Set up click listeners for buttons
        scanQrCode.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, QrCodeScanActivity.class);
            intent.putExtra("appUser", appUser); // Pass the appUser object
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            //Action for Profile Icon
            ViewProfileFragment dialogFragment = ViewProfileFragment.newInstance(appUser);
            dialogFragment.show(getSupportFragmentManager(), "View profile");
        });

        notificationsIcon.setOnClickListener(v -> {
            // Action for notification Icon
            Intent intent = new Intent(UserDashboardActivity.this, InvitationActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser", appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
        });

        preferencesIcon.setOnClickListener(v->{
            // Action for settings Icon
            Intent intent = new Intent(UserDashboardActivity.this, PreferencesActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser", appUser);
            intent.putExtras(newArgs);
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


        waitlistedEventsListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(UserDashboardActivity.this, UserEventActivity.class);
            Event selectedEvent = waitlistedEventsListData.get(i);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("selectedEvent", selectedEvent);
            newArgs.putSerializable("appUser", appUser);
            newArgs.putString("listType", "waitList");
            intent.putExtras(newArgs);
            startActivity(intent);

        });

        confirmedEventsListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(UserDashboardActivity.this, UserEventActivity.class);
            Event selectedEvent = confirmedEventsListData.get(i);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("selectedEvent", selectedEvent);
            newArgs.putSerializable("appUser", appUser);
            newArgs.putString("listType", "registeredEvents");
            intent.putExtras(newArgs);
            startActivity(intent);
        });

        // Add snapshot listener for Firestore
        db.collection("users").document(appUser.getDeviceId()).collection("waitList").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    waitlistedEventsListData.clear();
                    for (QueryDocumentSnapshot document : querySnapshots) {
                        Log.d("Firestore", "Found WaitList Events!");
                        Map<String, Object> eventData = document.getData();
                        String eventName = (String) eventData.get("eventName");
                        Date eventDate = document.getDate("eventDate");
                        Date drawDate = document.getDate("drawDate");
                        Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                        User owner = new User((String) document.get("owner"));
                        fireBaseController.fetchUserDoc(owner);
                        owner.createFacility();
                        Event event = new Event(owner, owner.getFacility(), eventName, eventDate, drawDate, sampleSize);
                        if (eventData.get("maxEntrants") != null) {
                            Integer maxEntrants = ((Long) eventData.get("maxEntrants")).intValue();
                            event.setMaxEntrants(maxEntrants);
                        }
                        if (eventData.get("eventDetails") != null) {
                            event.setEventDetails((String) eventData.get("eventDetails"));
                        }
                        if (eventData.get("posterUrl") != null) {
                            event.setPosterUrl((String) eventData.get("posterUrl"));
                        }
                        waitlistedEventsListData.addEvent(event);
                    }
                    waitlistedAdapter.notifyDataSetChanged();
                }
            }
        });
        db.collection("users").document(appUser.getDeviceId()).collection("registeredEvents").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    confirmedEventsListData.clear();
                    for (QueryDocumentSnapshot document : querySnapshots) {
                        Log.d("Firestore", "Found Registered Events!");
                        Map<String, Object> eventData = document.getData();
                        String eventName = (String) eventData.get("eventName");
                        Date eventDate = document.getDate("eventDate");
                        Date drawDate = document.getDate("drawDate");
                        Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                        User owner = new User((String) document.get("owner"));
                        fireBaseController.fetchUserDoc(owner);
                        owner.createFacility();
                        Event event = new Event(owner, owner.getFacility(), eventName, eventDate, drawDate, sampleSize);
                        if (eventData.get("maxEntrants") != null) {
                            Integer maxEntrants = ((Long) eventData.get("maxEntrants")).intValue();
                            event.setMaxEntrants(maxEntrants);
                        }
                        if (eventData.get("eventDetails") != null) {
                            event.setEventDetails((String) eventData.get("eventDetails"));
                        }
                        if (eventData.get("posterUrl") != null) {
                            event.setPosterUrl((String) eventData.get("posterUrl"));
                        }
                        confirmedEventsListData.addEvent(event);
                    }
                    confirmedAdapter.notifyDataSetChanged();
                }
            }
        });


    }
}
