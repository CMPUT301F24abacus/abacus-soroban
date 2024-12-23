package com.example.soroban.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.R;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

/**
 * Displays the details of a facility associated with the user.
 * The user can view and edit facility information through this activity.
 * @author Matthieu Larochelle
 * @author Ayan Chaudry
 * @see ManageFacilityActivity
 * @see Facility
 * @see User
 */
public class FacilityDisplayActivity extends AppCompatActivity {
    private User appUser;
    private TextView facilityNameTextView;
    private TextView facilityDetailsTextView;
    private Button editFacilityButton;

    private static final int EDIT_FACILITY_REQUEST_CODE = 2;

    /**
     * Called when this activity is first created.
     * Initializes views and displays the facility data.
     *
     * @param savedInstanceState the saved state of the activity.
     * @throws IllegalArgumentException if the appUser is not passed in the intent extras.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_display);

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

        // Initialize views with correct IDs from the XML layout
        facilityNameTextView = findViewById(R.id.tv_facility_name);
        facilityDetailsTextView = findViewById(R.id.tv_facility_details);
        editFacilityButton = findViewById(R.id.edit_facility_button);

        // Get facility data from User
        Facility userFacility = appUser.getFacility();

        // Set initial facility data
        facilityNameTextView.setText(userFacility.getName());
        facilityDetailsTextView.setText(userFacility.getDetails());

        // Set up the "Edit Facility" text view as a clickable button to open ManageFacilityActivity
        editFacilityButton.setOnClickListener(v -> {
            Intent editIntent = new Intent(FacilityDisplayActivity.this, ManageFacilityActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser",appUser);
            editIntent.putExtras(newArgs);
            startActivity(editIntent);
        });
    }

    /**
     * Handles the result returned from the ManageFacilityActivity.
     * Updates the facility information displayed in the TextViews.
     *
     * @param requestCode the request code of the activity.
     * @param resultCode the result code returned by the activity.
     * @param data the intent data containing updated facility information.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_FACILITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Retrieve the updated facility details
            String updatedFacilityName = data.getStringExtra("updatedFacilityName");
            String updatedFacilityDetails = data.getStringExtra("updatedFacilityDetails");

            // Update the TextViews with the new data
            if (updatedFacilityName != null) {
                facilityNameTextView.setText(updatedFacilityName);
            }
            if (updatedFacilityDetails != null) {
                facilityDetailsTextView.setText(updatedFacilityDetails);
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedFacilityName", updatedFacilityName);
            setResult(RESULT_OK, resultIntent);

        }
    }
}