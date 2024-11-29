package com.example.soroban.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import com.example.soroban.R;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.fragment.ViewProfileFragment;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.User;
import com.google.android.material.navigation.NavigationView;

/**
 * Displays the User Dashboard with functionalities like viewing events,
 * navigating to other dashboards, and managing user preferences.
 */
public class UserDashboardActivity extends AppCompatActivity {

    private User appUser;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private EventList waitlistedEventsListData;
    private EventList confirmedEventsListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_dashboard);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Drawer Layout and Toggle
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Navigation Menu Setup
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Get the appUser object
        Bundle args = getIntent().getExtras();
        if (args != null) {
            appUser = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    ? args.getSerializable("appUser", User.class)
                    : (User) args.getSerializable("appUser");

            if (appUser == null) {
                throw new IllegalArgumentException("Must pass User object to initialize appUser.");
            }
        } else {
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        // Initialize Events Data
        waitlistedEventsListData = appUser.getWaitList();
        confirmedEventsListData = appUser.getRegisteredEvents();

        setupButtons();
        setupEventLists();
    }

    private void setupButtons() {
        Button scanQrCode = findViewById(R.id.btn_scan_qr_code);
        ImageView profileIcon = findViewById(R.id.icon_profile);
        ImageView notificationsIcon = findViewById(R.id.icon_notifications);
        ImageView preferencesIcon = findViewById(R.id.icon_settings);

        scanQrCode.setOnClickListener(v -> {
            Intent intent = new Intent(this, QrCodeScanActivity.class);
            intent.putExtra("appUser", appUser);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            ViewProfileFragment dialogFragment = ViewProfileFragment.newInstance(appUser);
            dialogFragment.show(getSupportFragmentManager(), "View profile");
        });

        notificationsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, InvitationActivity.class);
            intent.putExtra("appUser", appUser);
            startActivity(intent);
        });

        preferencesIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, PreferencesActivity.class);
            intent.putExtra("appUser", appUser);
            startActivity(intent);
        });
    }

    private void setupEventLists() {
        ListView waitlistedEventsListView = findViewById(R.id.list_waitlisted_events);
        ListView confirmedEventsListView = findViewById(R.id.list_confirmed_events);

        EventArrayAdapter waitlistedAdapter = new EventArrayAdapter(this, waitlistedEventsListData);
        EventArrayAdapter confirmedAdapter = new EventArrayAdapter(this, confirmedEventsListData);

        waitlistedEventsListView.setAdapter(waitlistedAdapter);
        confirmedEventsListView.setAdapter(confirmedAdapter);

        waitlistedEventsListView.setOnItemClickListener((adapterView, view, index, id) -> {
            Intent intent = new Intent(this, UserEventActivity.class);
            intent.putExtra("appUser", appUser);
            intent.putExtra("event", waitlistedEventsListData.get(index));
            startActivity(intent);
        });

        confirmedEventsListView.setOnItemClickListener((adapterView, view, index, id) -> {
            Intent intent = new Intent(this, UserEventActivity.class);
            intent.putExtra("appUser", appUser);
            intent.putExtra("event", confirmedEventsListData.get(index));
            startActivity(intent);
        });
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Get the ID of the selected menu item
        int itemId = item.getItemId();

        // Check which menu item was selected
        if (itemId == R.id.nav_user_dashboard) {
            // Navigate to User Dashboard
            startActivity(new Intent(this, UserDashboardActivity.class));
        } else if (itemId == R.id.nav_organizer_dashboard) {
            // Navigate to Organizer Dashboard
            Intent organizerIntent = new Intent(this, OrganizerDashboardActivity.class);
            organizerIntent.putExtra("appUser", appUser); // Pass the appUser object
            organizerIntent.putExtra("facilityName", appUser.getFacility() != null ? appUser.getFacility().getName() : ""); // Pass facility name if available
            startActivity(organizerIntent);

        } else if (itemId == R.id.nav_admin_dashboard) {
            // Navigate to Admin Dashboard
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("appUser", appUser); // user is the instance of User class
            intent.putExtras(args);
            startActivity(intent);

        } else {
            // Handle any unexpected cases
            return false;
        }

        // Close the drawer after handling the click
        drawerLayout.closeDrawers();
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}








//
//package com.example.soroban.activity;
//
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ListView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//
//import com.example.soroban.FireBaseController;
//import com.example.soroban.R;
//import com.example.soroban.adapter.EventArrayAdapter;
//import com.example.soroban.fragment.ViewProfileFragment;
//import com.example.soroban.model.Event;
//import com.example.soroban.model.EventList;
//import com.example.soroban.model.User;
//
///**
// * Displays the user's dashboard with options to scan QR codes,
// * view notifications, manage preferences, and browse waitlisted and confirmed events.
// * References: StackOverflow
// *
// * @author Ayan Chaudhry
// * @author Matthieu Larochelle
// * @author Kevin Li
// * @see QrCodeScanActivity
// * @see InvitationActivity
// * @see PreferencesActivity
// * @see Event
// * @see EventArrayAdapter
// * @see User
// * @see UserEventActivity
// */
//public class UserDashboardActivity extends AppCompatActivity {
//    private User appUser;
//    private FireBaseController fireBaseController;
//    private ListView waitlistedEventsListView;
//    private EventList waitlistedEventsListData;
//    private EventArrayAdapter waitlistedAdapter;
//    private ListView confirmedEventsListView;
//    private EventList confirmedEventsListData;
//    private EventArrayAdapter confirmedAdapter;
//
//    /**
//     * Called when the activity is first created.
//     * @param savedInstanceState the saved state of the activity
//     * @throws IllegalArgumentException if the required User object is not passed.
//     * Author: Ayan Chaudhry, Matthieu Larochelle, Kevin Li
//     *
//     */
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.user_activity_dashboard);
//
//
//        // Get arguments passed from previous activity.
//        // Reference: https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter
//        Bundle args = getIntent().getExtras();
//
//        // Initialize appUser for this activity.
//        if(args != null){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                appUser = args.getSerializable("appUser", User.class);
//            }else{
//                appUser = (User) args.getSerializable("appUser");
//            }
//
//            if(appUser == null ){
//                throw new IllegalArgumentException("Must pass object of type User to initialize appUser.");
//            }
//        }else{
//            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
//        }
//
//        // Grab event data from appUser
//        waitlistedEventsListData = appUser.getWaitList();
//        confirmedEventsListData = appUser.getRegisteredEvents();
//
//        /**
//         * Set up button actions.
//         * Buttons are: Scan QR Code, Join Waiting List, Manage Facility
//         */
//
//        // Initialize buttons and
//        Button scanQrCode = findViewById(R.id.btn_scan_qr_code);
//        ImageView profileIcon = findViewById(R.id.icon_profile);
//        ImageView notificationsIcon = findViewById(R.id.icon_notifications);
//        ImageView preferencesIcon = findViewById(R.id.icon_settings);
//
//        // Set up click listeners for buttons
//        scanQrCode.setOnClickListener(v -> {
//            Intent intent = new Intent(UserDashboardActivity.this, QrCodeScanActivity.class);
//            intent.putExtra("appUser", appUser); // Pass the appUser object
//            startActivity(intent);
//        });
//
//        profileIcon.setOnClickListener(v -> {
//            //Action for Profile Icon
//            ViewProfileFragment dialogFragment = ViewProfileFragment.newInstance(appUser);
//            dialogFragment.show(getSupportFragmentManager(), "View profile");
//        });
//
//        notificationsIcon.setOnClickListener(v -> {
//            // Action for notification Icon
//            Intent intent = new Intent(UserDashboardActivity.this, InvitationActivity.class);
//            Bundle newArgs = new Bundle();
//            newArgs.putSerializable("appUser", appUser);
//            intent.putExtras(newArgs);
//            startActivity(intent);
//        });
//
//        preferencesIcon.setOnClickListener(v->{
//            // Action for settings Icon
//            Intent intent = new Intent(UserDashboardActivity.this, PreferencesActivity.class);
//            Bundle newArgs = new Bundle();
//            newArgs.putSerializable("appUser", appUser);
//            intent.putExtras(newArgs);
//            startActivity(intent);
//        });
//
//        // References for waitlisted and confirmed events ListViews
//        waitlistedEventsListView = findViewById(R.id.list_waitlisted_events);
//        confirmedEventsListView = findViewById(R.id.list_confirmed_events);
//
//        // Set up ArrayAdapters for both ListViews
//        waitlistedAdapter = new EventArrayAdapter(this,waitlistedEventsListData);
//        confirmedAdapter = new EventArrayAdapter(this, confirmedEventsListData);
//
//        waitlistedEventsListView.setAdapter(waitlistedAdapter);
//        confirmedEventsListView.setAdapter(confirmedAdapter);
//
//
//        waitlistedEventsListView.setOnItemClickListener((adapterView, view, i, l) -> {
//            Intent intent = new Intent(UserDashboardActivity.this, UserEventActivity.class);
//            Event selectedEvent = waitlistedEventsListData.get(i);
//            Bundle newArgs = new Bundle();
//            newArgs.putSerializable("selectedEvent", selectedEvent);
//            newArgs.putSerializable("appUser", appUser);
//            newArgs.putString("listType", "waitList");
//            intent.putExtras(newArgs);
//            startActivity(intent);
//
//        });
//
//        confirmedEventsListView.setOnItemClickListener((adapterView, view, i, l) -> {
//            Intent intent = new Intent(UserDashboardActivity.this, UserEventActivity.class);
//            Event selectedEvent = confirmedEventsListData.get(i);
//            Bundle newArgs = new Bundle();
//            newArgs.putSerializable("selectedEvent", selectedEvent);
//            newArgs.putSerializable("appUser", appUser);
//            newArgs.putString("listType", "registeredEvents");
//            intent.putExtras(newArgs);
//            startActivity(intent);
//        });
//
//
//    }
//}
