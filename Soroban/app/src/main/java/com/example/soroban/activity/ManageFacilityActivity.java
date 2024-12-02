package com.example.soroban.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.controller.FacilityController;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

/**
 * Allows the user to view and edit the details of an existing facility.
 * The user can update the facility's name and details, and save these changes.
 * @author Matthieu Larochelle
 * @author Ayan Chaudry
 * @see Facility
 * @see User
 * @see FacilityController
 * @see FireBaseController
 * @see OrganizerDashboardActivity
 * @author
 */
public class ManageFacilityActivity extends AppCompatActivity {
    private User appUser;
    private Facility thisFacility;
    private EditText editTextFacilityName, editTextFacilityDetails;
    private Button buttonSaveChanges;
    private Button cancelButton;

    /**
     * Called when the activity is first created.
     * Initializes views, retrieves facility data, and sets up event listeners.
     *
     * @param savedInstanceState The saved state of the activity.
     * @throws IllegalArgumentException if required arguments are not provided.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_facility);

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

        // Initialize views
        editTextFacilityName = findViewById(R.id.editTextFacilityName);
        editTextFacilityDetails = findViewById(R.id.editTextFacilityDetails);
        buttonSaveChanges = findViewById(R.id.buttonCreateFacility);
        cancelButton = findViewById(R.id.cancel_button);

        // Retrieve the facility data from the facility
        thisFacility = appUser.getFacility();

        // Set the facility data in the EditText fields
        editTextFacilityName.setText(thisFacility.getName());
        editTextFacilityDetails.setText(thisFacility.getDetails());

        // Set up the button click listener to cancel changes
        cancelButton.setOnClickListener(v -> {
            // Show Toast message
            Toast.makeText(ManageFacilityActivity.this, "Changes not saved", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ManageFacilityActivity.this, OrganizerDashboardActivity.class);
            intent.putExtra("appUser", appUser);
            startActivity(intent);
        });

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

            // Set up FacilityController
            FacilityController facilityController = new FacilityController(appUser.getFacility());

            facilityController.updateFacility(updatedFacilityName, updatedFacilityDetails);
            fireBaseController.facilityUpdate(appUser.getFacility());

            // Show Toast message
            Toast.makeText(ManageFacilityActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ManageFacilityActivity.this, OrganizerDashboardActivity.class);
            intent.putExtra("appUser", appUser);
            startActivity(intent);
        });
    }
}