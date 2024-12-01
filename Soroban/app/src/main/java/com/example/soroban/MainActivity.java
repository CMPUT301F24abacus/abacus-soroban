///**

package com.example.soroban;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.soroban.activity.AdminDashboardActivity;
import com.example.soroban.activity.CreateFacilityActivity;
import com.example.soroban.activity.OrganizerDashboardActivity;
import com.example.soroban.activity.UserDashboardActivity;
import com.example.soroban.model.User;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private User appUser;
    private FireBaseController firebaseController;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Firebase and UI initialization
        firebaseController = new FireBaseController(this);
        Button btnOpenAdminDashboard = findViewById(R.id.btn_open_admin_dashboard);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button btnOpenDashboard = findViewById(R.id.btn_open_dashboard);
        Button btnOpenOrganizerDashboard = findViewById(R.id.btn_open_organizer_dashboard);

        // Handle passed arguments
        Bundle args = getIntent().getExtras();
        if (args != null) {
            appUser = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    ? args.getSerializable("appUser", User.class)
                    : (User) args.getSerializable("appUser");
        }

        if (appUser == null) {
            appUser = new User(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
            appUser.setAdminCheck(false);
            firebaseController.initialize(progressBar, btnOpenDashboard, btnOpenOrganizerDashboard, appUser, btnOpenAdminDashboard);
        }

        // Notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askNotificationPermission();
        }

        // Adjust layout for system bars
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Navigation drawer item selection
        navigationView.setNavigationItemSelectedListener(this::handleNavigation);

        // Button click listeners
        btnOpenDashboard.setOnClickListener(v -> openActivity(UserDashboardActivity.class));
        btnOpenOrganizerDashboard.setOnClickListener(v -> openActivity(OrganizerDashboardActivity.class));
        btnOpenAdminDashboard.setOnClickListener(v -> openActivity(AdminDashboardActivity.class));
    }

    /**
     * Handle navigation drawer item clicks.
     *
     * @param item the selected menu item
     */
    private boolean handleNavigation(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_user_dashboard) {
            openActivity(UserDashboardActivity.class);
        } else if (id == R.id.nav_organizer_dashboard) {
            openActivity(OrganizerDashboardActivity.class);
        } else if (id == R.id.nav_admin_dashboard) {
            openActivity(AdminDashboardActivity.class);
        }
        drawerLayout.closeDrawers();
        return true;
    }

    /**
     * Open the specified activity and pass the current user object.
     *
     * @param activityClass the target activity class
     */
    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        intent.putExtra("appUser", appUser);
        startActivity(intent);
    }

    /**
     * Ask for notification permissions if not already granted.
     */
    private void askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d("MainActivity", "Notification permission granted");
                } else {
                    Log.d("MainActivity", "Notification permission denied");
                }
            }).launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}













// * The MainActivity class serves as the entry point for the application, initializing the user
// * and navigating to appropriate dashboards based on user actions.
// *
// * <p>Features include requesting permissions, initializing a user in Firebase, and enabling
// * navigation to user, organizer, and admin dashboards.</p>
// *
// * @author Ayan Chaudhry
// * @author Matthieu
// * @author Kevin Li
// * @see User
// * @see FireBaseController
// * @see UserDashboardActivity
// * @see OrganizerDashboardActivity
// * @see AdminDashboardActivity
// * @see CreateFacilityActivity
// */
//package com.example.soroban;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ProgressBar;
//
//import androidx.activity.EdgeToEdge;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//
//import com.example.soroban.activity.AdminDashboardActivity;
//import com.example.soroban.activity.CreateFacilityActivity;
//import com.example.soroban.activity.OrganizerDashboardActivity;
//import com.example.soroban.activity.UserDashboardActivity;
//import com.example.soroban.model.User;
//
//
//public class MainActivity extends AppCompatActivity {
//    private User appUser;
//    private FireBaseController firebaseController;
//    private ActivityResultLauncher<String> requestPersmissionLauncher;
//
//    /**
//     * Called when the activity is first created.
//     * @param savedInstanceState
//     * @Author: Ayan Chaudhry, Matthieu Larochelle, Kevin Li
//     *
//     */
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//
//        Button btnOpenAdminDashboard = findViewById(R.id.btn_open_admin_dashboard);
//        ProgressBar progressBar = findViewById(R.id.progressBar);
//        // Get reference to the button
//        Button btnOpenDashboard = findViewById(R.id.btn_open_dashboard);
//        Button btnOpenOrganizerDashboard = findViewById(R.id.btn_open_organizer_dashboard);
//        firebaseController = new FireBaseController(this);
//
//        Bundle args = getIntent().getExtras();
//        if(args != null){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                appUser = args.getSerializable("appUser", User.class);
//            }else{
//                appUser = (User) args.getSerializable("appUser");
//            }
//        }
//
//        if(args == null || appUser == null){
//            // Get Android Device Id.
//            // Reference: https://www.geeksforgeeks.org/how-to-fetch-device-id-in-android-programmatically/
//            appUser = new User(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
//
//            // Add the user into the Firebase Database
//            firebaseController.initialize(progressBar, btnOpenDashboard, btnOpenOrganizerDashboard, appUser, btnOpenAdminDashboard);
//        }
//
//
//        // Ask for notification permissions
//        requestPersmissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
//            if(isGranted){
//                // App can post notifications
//            }else{
//                // Inform user app will not show notifications
//            }
//        });
//        askNotificationPermission();
//
//        // Set window insets for system bars
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//
//        // Set up a click listener to navigate to UserDashboardActivity
//        btnOpenDashboard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to UserDashboardActivity
//                Intent intent = new Intent(MainActivity.this, UserDashboardActivity.class);
//                Bundle args = new Bundle();
//                args.putSerializable("appUser",appUser);
//                intent.putExtras(args);
//                startActivity(intent);
//            }
//        });
//
//        // Set up a click listener to navigate to OrganizerDashboardActivity
//
//        btnOpenOrganizerDashboard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to OrganizerDashboardActivity
//                Intent intent;
//                intent = new Intent(MainActivity.this, OrganizerDashboardActivity.class);
//                intent.putExtra("appUser", appUser);
//                startActivity(intent);
//            }
//        });
//
//        btnOpenAdminDashboard.setOnClickListener( v -> {
//            Intent intent;
//            intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
//            Bundle argsAdmin = new Bundle();
//            argsAdmin.putSerializable("appUser",appUser);
//            intent.putExtras(argsAdmin);
//            startActivity(intent);
//        });
//
//    }
//
//    /**
//     * Ask user for notification permissions.
//     * @Author: Matthieu Larochelle
//     * @version: 1.0
//     */
//    // Reference: https://firebase.google.com/docs/cloud-messaging/android/client?hl=en&authuser=0&_gl=1*neb8gy*_up*MQ..*_ga*MTczOTA1OTE3OS4xNzMwNTg0MzQx*_ga_CW55HF8NVT*MTczMDU4NDM0MC4xLjEuMTczMDU5MDcyNy41MS4wLjA.
//    private void askNotificationPermission(){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
//                // App can show notifications
//            }else if(shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)){
//                // Prompt user to accept or decline notifications,
//            }else{
//                // Directly ask for permission
//                requestPersmissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
//            }
//        }
//    }
//}
