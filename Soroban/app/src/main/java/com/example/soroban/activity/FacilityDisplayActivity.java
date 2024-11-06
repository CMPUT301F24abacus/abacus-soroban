package com.example.soroban.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.R;

public class FacilityDisplayActivity extends AppCompatActivity {

    private TextView facilityNameTextView;
    private TextView facilityDetailsTextView;
    private TextView editFacilityButton;

    private static final int EDIT_FACILITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_display);

        // Initialize views with correct IDs from the XML layout
        facilityNameTextView = findViewById(R.id.tv_facility_name);
        facilityDetailsTextView = findViewById(R.id.tv_facility_details);
        editFacilityButton = findViewById(R.id.tv_edit_facility);

        // Get facility data from intent
        Intent intent = getIntent();
        String facilityName = intent.getStringExtra("facilityName");
        String facilityDetails = intent.getStringExtra("facilityDetails");

        // Set initial facility data
        facilityNameTextView.setText(facilityName != null ? facilityName : "My Facility");
        facilityDetailsTextView.setText(facilityDetails != null ? facilityDetails : "No details available");

        // Set up the "Edit Facility" text view as a clickable button to open ManageFacilityActivity
        editFacilityButton.setOnClickListener(v -> {
            Intent editIntent = new Intent(FacilityDisplayActivity.this, ManageFacilityActivity.class);
            editIntent.putExtra("facilityName", facilityNameTextView.getText().toString());
            editIntent.putExtra("facilityDetails", facilityDetailsTextView.getText().toString());
            startActivityForResult(editIntent, EDIT_FACILITY_REQUEST_CODE);
        });
    }

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
