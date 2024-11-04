package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.soroban.activity.UserDashboardActivity;
import com.example.soroban.model.User;


public class MainActivity extends AppCompatActivity {
    private User appUser;
    private FireBaseController firebaseController;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState
     * Author: Ayan Chaudhry, Matthieu Larochelle, Kevin Li
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        firebaseController = new FireBaseController();

        // Get Android Device Id.
        // Reference: https://www.geeksforgeeks.org/how-to-fetch-device-id-in-android-programmatically/
        appUser = new User(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        // Add the use into the Firebase Database
        firebaseController.fetchUserDoc(appUser);

        // Set window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get reference to the button
        Button btnOpenDashboard = findViewById(R.id.btn_open_dashboard);

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
    }
}
