package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ManageFacilityActivity extends AppCompatActivity {

    private EditText editFacilityName, editFacilityDetails;
    private Button btnSaveFacility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_facility);

        editFacilityName = findViewById(R.id.editFacilityName);
        editFacilityDetails = findViewById(R.id.editFacilityDetails);
        btnSaveFacility = findViewById(R.id.btnSaveFacility);

        // Retrieve existing facility data from Intent if available
        String facilityName = getIntent().getStringExtra("facilityName");
        String facilityDetails = getIntent().getStringExtra("facilityDetails");

        // Populate fields if data exists
        if (facilityName != null) editFacilityName.setText(facilityName);
        if (facilityDetails != null) editFacilityDetails.setText(facilityDetails);

        btnSaveFacility.setOnClickListener(v -> saveFacility());
    }

    private void saveFacility() {
        String facilityName = editFacilityName.getText().toString().trim();
        String facilityDetails = editFacilityDetails.getText().toString().trim();

        // Input validation
        if (facilityName.isEmpty()) {
            editFacilityName.setError("Facility name cannot be empty");
            editFacilityName.requestFocus();
            return;
        }

        if (facilityDetails.isEmpty()) {
            editFacilityDetails.setError("Facility details cannot be empty");
            editFacilityDetails.requestFocus();
            return;
        }

        // Save facility to database or storage (placeholder code)
        // Example: DatabaseHelper.saveFacility(facilityName, facilityDetails);

        // Display a success message
        Toast.makeText(this, "Facility saved!", Toast.LENGTH_SHORT).show();

        // Pass the edited facility data back to the calling activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedFacilityName", facilityName);
        resultIntent.putExtra("updatedFacilityDetails", facilityDetails);
        setResult(RESULT_OK, resultIntent);

        finish(); // Close the activity after saving
    }
}
