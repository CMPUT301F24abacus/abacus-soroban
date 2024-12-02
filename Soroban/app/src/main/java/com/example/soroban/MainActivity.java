package com.example.soroban;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.soroban.activity.CreateAccountActivity;
import com.example.soroban.activity.UserDashboardActivity;
import com.example.soroban.model.User;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity"; // For logging
    private User appUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase and UI initialization
        FireBaseController firebaseController = new FireBaseController(this);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button button = findViewById(R.id.buttonContinue);
        button.setClickable(false);

        appUser = new User(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        firebaseController.initialize(progressBar, appUser, button);

        // Notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askNotificationPermission();
        }

        button.setOnClickListener(v -> {
            String firstName = appUser.getFirstName();
            String lastName = appUser.getLastName();
            String email = appUser.getEmail();

            if (firstName != null && lastName != null && email != null) {
                // Profile is complete, redirect to UserDashboardActivity
                Log.d(TAG, "User profile is complete. Redirecting to UserDashboardActivity.");
                openActivity(UserDashboardActivity.class);
            } else {
                // Profile is incomplete, redirect to CreateAccountActivity
                Log.d(TAG, "User profile is incomplete. Redirecting to CreateAccountActivity.");
                openActivity(CreateAccountActivity.class);
            }
        });

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
