package com.example.soroban.activity;

import static com.example.soroban.fragment.ViewProfileFragment.getDefaultPictureID;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
//import androidx.navigation.ui.AppBarConfiguration;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.fragment.ViewProfileFragment;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;
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
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
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

        // References for waitlisted and confirmed events ListViews
        waitlistedEventsListView = findViewById(R.id.list_waitlisted_events);
        confirmedEventsListView = findViewById(R.id.list_confirmed_events);

        // Set up ArrayAdapters for both ListViews
        waitlistedAdapter = new EventArrayAdapter(this,waitlistedEventsListData);
        confirmedAdapter = new EventArrayAdapter(this, confirmedEventsListData);

        waitlistedEventsListView.setAdapter(waitlistedAdapter);
        confirmedEventsListView.setAdapter(confirmedAdapter);


        waitlistedEventsListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Log.d("Firestore", "Intent Has Begun!");
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

        setupNavMenu(navigationView, appUser);
        setupButtons();

        updateNavigationDrawer(); // Update navigation drawer with user info
    }

    private void setupNavMenu(NavigationView navigationView, User appUser) {
        Menu menu = navigationView.getMenu();

        if (appUser.getAdminCheck()) {
            menu.getItem(3).setVisible(true);
        }

        if (appUser.getFacility() != null) {
            menu.getItem(1).setVisible(true);
        }
        else {
            menu.getItem(2).setVisible(true);
        }

        menu.getItem(0).getSubMenu().getItem(0).setChecked(true);
    }

    private void setupButtons() {
        Button scanQrCode = findViewById(R.id.btn_scan_qr_code);
        ImageView profileIcon = findViewById(R.id.icon_profile);
        ImageView notificationsIcon = findViewById(R.id.icon_notifications);
        ImageView preferencesIcon = findViewById(R.id.icon_settings);

        // Set up click listeners for buttons
        scanQrCode.setOnClickListener(v -> {
            Intent intent = new Intent(this, QrCodeScanActivity.class);
            intent.putExtra("appUser", appUser);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            //Action for Profile Icon
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

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Get the ID of the selected menu item
        int itemId = item.getItemId();
        // Handle operations related to selected menu item
        if (itemId == R.id.user_dashboard) {
            // Do nothing
        } else if (itemId == R.id.user_profile) {
            ViewProfileFragment dialogFragment = ViewProfileFragment.newInstance(appUser);
            dialogFragment.show(getSupportFragmentManager(), "View profile");
        } else if (itemId == R.id.user_invitations) {
            Intent intent = new Intent(this, InvitationActivity.class);
            intent.putExtra("appUser", appUser);
            startActivity(intent);
        } else if (itemId == R.id.user_scan_qr) {
            Intent intent = new Intent(this, QrCodeScanActivity.class);
            intent.putExtra("appUser", appUser);
            startActivity(intent);
        } else if (itemId == R.id.user_preferences) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            intent.putExtra("appUser", appUser);
            startActivity(intent);
        } else if (itemId == R.id.org_dashboard) {
            Intent organizerIntent = new Intent(this, OrganizerDashboardActivity.class);
            organizerIntent.putExtra("appUser", appUser); // Pass the appUser object
            organizerIntent.putExtra("facilityName", appUser.getFacility() != null ? appUser.getFacility().getName() : ""); // Pass facility name if available
            startActivity(organizerIntent);
        } else if (itemId == R.id.org_manage_facility) {
            Intent intent = new Intent(this, FacilityDisplayActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser",appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
        } else if (itemId == R.id.org_create_event) {
            Intent intent = new Intent(this, CreateEventActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser",appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
        } else if (itemId == R.id.org_create_facility) {
            Intent intent = new Intent(this, CreateFacilityActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser",appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
        } else if (itemId == R.id.admin_dashboard) {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("appUser", appUser);
            intent.putExtras(args);
            startActivity(intent);
        } else {
            return false;
        }

        // Close the drawer after handling the click
        drawerLayout.closeDrawers();
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(false);
        return true;
    }

    // Method to update the profile image and username in the navigation drawer
    private void updateNavigationDrawer() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0); // Get the header view

        // Get references to the TextView and ImageView for the profile
        TextView usernameTextView = headerView.findViewById(R.id.profile_name);
        ImageView profileImageView = headerView.findViewById(R.id.profile_pic);

        // Set the username dynamically
        usernameTextView.setText(appUser.getFirstName() + " " + appUser.getLastName());

        // Fetch and set the profile image using Firebase
        FirebaseDatabase.getInstance().getReference("users")
                .child(appUser.getDeviceId())
                .child("profileImageUrl")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String imageUrl = snapshot.getValue(String.class);
                    if (imageUrl != null) {
                        Glide.with(this)
                                .load(imageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(profileImageView);
                    } else {
                        // If no image URL, generate default image based on first letter of first name
                        int defaultImageID = getDefaultPictureID(appUser.getFirstName(), this);
                        profileImageView.setImageResource(defaultImageID);
                    }
                })
                .addOnFailureListener(e -> {
                    // If fetching fails, set default profile picture
                    int defaultImageID = getDefaultPictureID(appUser.getFirstName(), this);
                    profileImageView.setImageResource(defaultImageID);
                });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}