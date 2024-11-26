package com.example.soroban;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.soroban.activity.AdminDashboardActivity;
import com.example.soroban.activity.CreateFacilityActivity;
import com.example.soroban.activity.OrganizerDashboardActivity;
import com.example.soroban.activity.UserDashboardActivity;
import com.example.soroban.model.User;


public class MainActivity extends AppCompatActivity {
    private User appUser;
    private FireBaseController firebaseController;
    private ActivityResultLauncher<String> requestPersmissionLauncher;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState
     * @Author: Ayan Chaudhry, Matthieu Larochelle, Kevin Li
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnOpenAdminDashboard = findViewById(R.id.btn_open_admin_dashboard);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        // Get reference to the button
        Button btnOpenDashboard = findViewById(R.id.btn_open_dashboard);
        Button btnOpenOrganizerDashboard = findViewById(R.id.btn_open_organizer_dashboard);
        firebaseController = new FireBaseController(this);

        Bundle args = getIntent().getExtras();
        if(args != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
            }
        }

        if(args == null || appUser == null){
            // Get Android Device Id.
            // Reference: https://www.geeksforgeeks.org/how-to-fetch-device-id-in-android-programmatically/
            appUser = new User(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

            // Add the user into the Firebase Database
            firebaseController.initialize(progressBar, btnOpenDashboard, btnOpenOrganizerDashboard, appUser, btnOpenAdminDashboard);
        }


        // Ask for notification permissions
        requestPersmissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
            if(isGranted){
                // App can post notifications
            }else{
                // Inform user app will not show notifications
            }
        });
        askNotificationPermission();

        // Set window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Set up a click listener to navigate to UserDashboardActivity
        btnOpenDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to UserDashboardActivity
                Intent intent = new Intent(MainActivity.this, UserDashboardActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("appUser",appUser);
                intent.putExtras(args);
                startActivity(intent);
            }
        });

        // Set up a click listener to navigate to OrganizerDashboardActivity

        btnOpenOrganizerDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to OrganizerDashboardActivity
                Intent intent;
                intent = new Intent(MainActivity.this, OrganizerDashboardActivity.class);
                intent.putExtra("appUser", appUser);
                startActivity(intent);
            }
        });

        btnOpenAdminDashboard.setOnClickListener( v -> {
            Intent intent;
            intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
            Bundle argsAdmin = new Bundle();
            argsAdmin.putSerializable("appUser",appUser);
            intent.putExtras(argsAdmin);
            startActivity(intent);
        });

    }

    /**
     * Ask user for notification permissions.
     * @Author: Matthieu Larochelle
     * @version: 1.0
     */
    // Reference: https://firebase.google.com/docs/cloud-messaging/android/client?hl=en&authuser=0&_gl=1*neb8gy*_up*MQ..*_ga*MTczOTA1OTE3OS4xNzMwNTg0MzQx*_ga_CW55HF8NVT*MTczMDU4NDM0MC4xLjEuMTczMDU5MDcyNy41MS4wLjA.
    private void askNotificationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                // App can show notifications
            }else if(shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)){
                // Prompt user to accept or decline notifications,
            }else{
                // Directly ask for permission
                requestPersmissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
