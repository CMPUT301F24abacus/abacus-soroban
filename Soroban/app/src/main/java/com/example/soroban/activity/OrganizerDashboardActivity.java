package com.example.soroban.activity;

import static com.example.soroban.fragment.ViewProfileFragment.getDefaultPictureID;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.soroban.activity.EventDetailsActivity;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

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
    private FireBaseController fireBaseController;
    private FirebaseFirestore db;
    private User appUser;
    private TextView facilityNameTextView;
    private ListView eventsListView;
    private Button createEventButton;
    private Button goToFacilityButton;
    private EventArrayAdapter eventAdapter;
    private EventList events;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

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

        db = FirebaseFirestore.getInstance();
        fireBaseController = new FireBaseController(this);

        // Initialize views
        facilityNameTextView = findViewById(R.id.facilityNameTextView);
        eventsListView = findViewById(R.id.eventsListView);
        createEventButton = findViewById(R.id.createEventButton);
        goToFacilityButton = findViewById(R.id.btn_go_to_facility);

        // Set facility name dynamically
        Facility facility =  appUser.getFacility();
        if(facility != null){
            facilityNameTextView.setText(facility.getName());
        }else{
            facilityNameTextView.setText("My Facility");
        }

        // Initialize events list
        events = new EventList();

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


        // Add snapshot listener for Firestore
        db.collection("users").document(appUser.getDeviceId()).collection("hostedEvents").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    events.clear();
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
                        events.addEvent(event);
                    }
                    eventAdapter.notifyDataSetChanged();
                }
            }
        });

        setupNavMenu(navigationView, appUser);
        updateNavigationDrawer();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(OrganizerDashboardActivity.this, UserDashboardActivity.class);
                Bundle newArgs = new Bundle();
                newArgs.putSerializable("appUser",appUser);
                intent.putExtras(newArgs);
                startActivity(intent);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
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

        menu.getItem(1).getSubMenu().getItem(0).setChecked(true);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Get the ID of the selected menu item
        int itemId = item.getItemId();
        // Handle operations related to selected menu item
        if (itemId == R.id.user_dashboard) {
            Intent intent = new Intent(this, UserDashboardActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser",appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
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
            // Do nothing
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
        navigationView.getMenu().getItem(1).getSubMenu().getItem(0).setChecked(true);
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
