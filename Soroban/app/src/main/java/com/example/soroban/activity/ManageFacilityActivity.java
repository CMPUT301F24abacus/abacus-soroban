package com.example.soroban.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.R;

public class ManageFacilityActivity extends AppCompatActivity {

    private EditText editTextFacilityName, editTextFacilityDetails;
    private Button buttonSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_facility);

        // Initialize views
        editTextFacilityName = findViewById(R.id.editTextFacilityName);
        editTextFacilityDetails = findViewById(R.id.editTextFacilityDetails);
        buttonSaveChanges = findViewById(R.id.buttonCreateFacility);

        // Retrieve the facility data from the intent
        Intent intent = getIntent();
        String facilityName = intent.getStringExtra("facilityName");
        String facilityDetails = intent.getStringExtra("facilityDetails");

        // Set the facility data in the EditText fields
        editTextFacilityName.setText(facilityName != null ? facilityName : "");
        editTextFacilityDetails.setText(facilityDetails != null ? facilityDetails : "");

        // Set up the button click listener to save changes
        buttonSaveChanges.setOnClickListener(v -> {
            String updatedFacilityName = editTextFacilityName.getText().toString().trim();
            String updatedFacilityDetails = editTextFacilityDetails.getText().toString().trim();

            // Validate that the facility name is not empty
            if (updatedFacilityName.isEmpty()) {
                Toast.makeText(this, "Facility name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create an intent to return the updated data
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedFacilityName", updatedFacilityName);
            resultIntent.putExtra("updatedFacilityDetails", updatedFacilityDetails);
            setResult(RESULT_OK, resultIntent);
            finish(); // Close the activity and return to the previous one
        });
    }
}
