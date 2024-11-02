package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private User appUser;
    private FireBaseController firebaseController;

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
        firebaseController.createUserDb(appUser);

        /**
         * !!! TEMPORARY BAD TEST !!!
         */
        //Event mockEvent = new Event(appUser,appUser.createFacility(),"mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 3);
        //appUser.addToWaitlist(mockEvent);
        /**
         * !!! TEMPORARY BAD TEST !!!
         */

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
                intent.putExtra("appUser",appUser);
                startActivity(intent);
            }
        });
    }
}
