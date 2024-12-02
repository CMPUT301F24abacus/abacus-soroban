package com.example.soroban.activity;

import static com.example.soroban.fragment.ViewProfileFragment.getDefaultPictureID;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.soroban.R;
import com.example.soroban.fragment.ViewProfileFragment;
import com.example.soroban.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Serves as the main navigation hub for administrators.
 * Provides buttons to navigate to different browsing activities: Events, Profiles, Facilities, and Images.
 * @author Kevin Li
 * @see AdminBrowseEventActivity
 * @see AdminBrowseProfileActivity
 * @see AdminBrowseFacilityActivity
 * @see AdminBrowseImagesActivity
 * @see User
 */
public class AdminDashboardActivity extends AppCompatActivity {
    private User appUser;
    private Button browseEventBtn;
    private Button browseProfileBtn;
    private Button browseImageBtn;
    private Button browseFacilityBtn;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    /**
     * Called when the activity is first created.
     * Initializes the user interface and passes the appUser data to subsequent activities.
     *
     * @param savedInstanceState the saved state of the activity.
     * @throws IllegalArgumentException if required arguments are missing or incorrect.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_dashboard);

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


        browseEventBtn = findViewById(R.id.browse_event_btn);
        browseProfileBtn = findViewById(R.id.browse_profile_btn);
        browseFacilityBtn = findViewById(R.id.browse_facility_btn);
        browseImageBtn = findViewById(R.id.browse_image_btn);

        browseEventBtn.setOnClickListener( v -> {
            Intent intent;
            intent = new Intent(AdminDashboardActivity.this, AdminBrowseEventActivity.class);
            Bundle argsEvent = new Bundle();
            argsEvent.putSerializable("appUser",appUser);
            intent.putExtras(argsEvent);
            startActivity(intent);
        });

        browseProfileBtn.setOnClickListener( v -> {
            Intent intent;
            intent = new Intent(AdminDashboardActivity.this, AdminBrowseProfileActivity.class);
            Bundle argsPr = new Bundle();
            argsPr.putSerializable("appUser",appUser);
            intent.putExtras(argsPr);
            startActivity(intent);
        });

        browseFacilityBtn.setOnClickListener( v -> {
            Intent intent;
            intent = new Intent(AdminDashboardActivity.this, AdminBrowseFacilityActivity.class);
            Bundle argsFc = new Bundle();
            argsFc.putSerializable("appUser",appUser);
            intent.putExtras(argsFc);
            startActivity(intent);
        });

        browseImageBtn.setOnClickListener( v -> {
            Intent intent;
            intent = new Intent(AdminDashboardActivity.this, AdminBrowseImagesActivity.class);
            Bundle argsIg = new Bundle();
            argsIg.putSerializable("appUser",appUser);
            intent.putExtras(argsIg);
            startActivity(intent);
        });

        setupNavMenu(navigationView, appUser);
        updateNavigationDrawer();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(AdminDashboardActivity.this, UserDashboardActivity.class);
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

        menu.getItem(3).getSubMenu().getItem(0).setChecked(true);
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
            // Do nothing
        } else {
            return false;
        }

        // Close the drawer after handling the click
        drawerLayout.closeDrawers();
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.getMenu().getItem(3).getSubMenu().getItem(0).setChecked(true);
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
