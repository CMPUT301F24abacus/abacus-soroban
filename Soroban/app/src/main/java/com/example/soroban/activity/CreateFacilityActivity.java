package com.example.soroban.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.MainActivity;
import com.example.soroban.R;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

/**
 * Allows users to create and set up their facility profiles.
 * A facility profile is required for organizing events. This provides Firebase integration
 * for saving facility data
 * @author
 * @see Facility
 * @see User
 * @see FireBaseController
 * @see OrganizerDashboardActivity
 */
public class CreateFacilityActivity extends AppCompatActivity {
    private User appUser; // The user creating the facility.

    /**
     * Initializes the activity, retrieves passed arguments, and sets up the UI for facility creation.
     *
     * @param savedInstanceState the saved state of the activity.
     * @throws IllegalArgumentException if required arguments (e.g., appUser) are missing.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_facility);

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

        // Display an initial message to guide the user
        Toast.makeText(this, "Set up your facility profile in order to organize events", Toast.LENGTH_SHORT).show();

        // Initialize views
        EditText editTextFacilityName = findViewById(R.id.editTextFacilityName);
        EditText editTextFacilityDetails = findViewById(R.id.editTextFacilityDetails);
        Button buttonSaveChanges = findViewById(R.id.buttonCreateFacility);

        // Set up the button click listener to save changes
        buttonSaveChanges.setOnClickListener(v -> {
            String updatedFacilityName = editTextFacilityName.getText().toString().trim();
            String updatedFacilityDetails = editTextFacilityDetails.getText().toString().trim();

            // Validate that the facility name is not empty
            if (updatedFacilityName.isEmpty()) {
                Toast.makeText(this, "Facility name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set up FireBase functionality
            FireBaseController fireBaseController = new FireBaseController(this);

            // Create and set up a new facility for the user
            Facility newFacility = appUser.createFacility();
            newFacility.setName(updatedFacilityName);
            newFacility.setDetails(updatedFacilityDetails);

            // Save the facility to Firebase
            fireBaseController.createFacilityDb(newFacility);

            // Navigate to the Organizer Dashboard
            Intent intent = new Intent(CreateFacilityActivity.this, OrganizerDashboardActivity.class);
            intent.putExtra("appUser", appUser);
            startActivity(intent);
        });
    }
}