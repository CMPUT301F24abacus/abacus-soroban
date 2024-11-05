package com.example.soroban.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.R;

/**
 * this activity is responsible for handling the event registration process.
 * we are using this in a rather tricky way as this screen alternatively shows the register and unregister buttons depending
 * on the registration status of the user.
 * the screen is also responsible for updating the UI when the user registers or unregisters from the event.
 * things shown on this screen: event details. event poster, event date, event time, event location, event description.
 */
public class EventRegistrationActivity extends AppCompatActivity {

    private Button registerButton;
    private Button unregisterButton;
    private boolean isRegistered; // This will store the event registration status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);

        // Find buttons
        registerButton = findViewById(R.id.btn_register);
        unregisterButton = findViewById(R.id.btn_unregister);

        // Assume we have a way to determine if the user is registered for the event
        // For now, let's mock this with a variable
        isRegistered = getIntent().getBooleanExtra("isRegistered", false);

        // Show the appropriate button based on registration status
        if (isRegistered) {
            unregisterButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.GONE);
        } else {
            registerButton.setVisibility(View.VISIBLE);
            unregisterButton.setVisibility(View.GONE);
        }

        // Set up button click listeners
        registerButton.setOnClickListener(v -> {
            // Handle registration logic here
            Toast.makeText(this, "Registered for the event!", Toast.LENGTH_SHORT).show();
            // Update the UI
            registerButton.setVisibility(View.GONE);
            unregisterButton.setVisibility(View.VISIBLE);
            isRegistered = true; // Update status
        });

        unregisterButton.setOnClickListener(v -> {
            // Handle unregistration logic here
            Toast.makeText(this, "Unregistered from the event!", Toast.LENGTH_SHORT).show();
            // Update the UI
            unregisterButton.setVisibility(View.GONE);
            registerButton.setVisibility(View.VISIBLE);
            isRegistered = false; // Update status
        });
    }
}