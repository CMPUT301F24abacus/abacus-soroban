package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FacilityDisplayActivity extends AppCompatActivity {

    private TextView tvFacilityName, tvFacilityDetails;
    private ImageButton btnEditFacility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_display);

        // Initialize views
        tvFacilityName = findViewById(R.id.tv_facility_name);
        tvFacilityDetails = findViewById(R.id.tv_facility_details);
        TextView btnEditFacility = findViewById(R.id.tv_edit_facility);


        // Retrieve facility data (you can replace this with real data from your database or model)
        String facilityName = "MyFacility"; // Replace with actual facility name data
        String facilityDetails = "Details about the facility."; // Replace with actual facility details

        // Set the facility data
        tvFacilityName.setText(facilityName);
        tvFacilityDetails.setText(facilityDetails);

        // Set up the edit button click listener
        btnEditFacility.setOnClickListener(v -> {
            // Navigate to Edit Facility screen
            Intent intent = new Intent(FacilityDisplayActivity.this, ManageFacilityActivity.class);
            intent.putExtra("facilityName", facilityName);
            intent.putExtra("facilityDetails", facilityDetails);
            startActivity(intent);
        });
    }
}
