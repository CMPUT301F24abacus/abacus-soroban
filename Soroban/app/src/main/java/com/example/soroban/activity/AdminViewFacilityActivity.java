package com.example.soroban.activity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

public class AdminViewFacilityActivity extends AppCompatActivity {
    private User appUser;
    private Facility selectedFacility;
    private TextView facilityNameText;
    private TextView facilityDetailsText;
    private TextView facilityOwnerText;
    private TextView violationText;
    private Button deleteFacilityBtn;
    private FireBaseController firebaseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view_facility);

        Bundle args = getIntent().getExtras();

        // Initialize appUser and selected Event for this activity.
        if (args != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                selectedFacility = args.getSerializable("selectedFacility", Facility.class);
                appUser = args.getSerializable("appUser", User.class);
            } else {
                appUser = (User) args.getSerializable("appUser");
                selectedFacility = (Facility) args.getSerializable("selectedFacility");
            }

            if (appUser == null || selectedFacility == null) {
                throw new IllegalArgumentException("Must pass object of type User and Facility to initialize appUser.");
            }

        } else {
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        firebaseController = new FireBaseController(this);

        // Initialize buttons, etc.
        facilityNameText = findViewById(R.id.tvad_facility_name);
        facilityDetailsText = findViewById(R.id.tvad_facility_details);
        facilityOwnerText = findViewById(R.id.tvad_facility_owner);
        violationText = findViewById(R.id.violation_tv);
        deleteFacilityBtn = findViewById(R.id.delete_facility_button);

        facilityNameText.setText(selectedFacility.getName());
        facilityDetailsText.setText(selectedFacility.getDetails());
        // Changed to username if necessary
        facilityOwnerText.setText("ID: " + selectedFacility.getOwner().getDeviceId() + "\nName: " + selectedFacility.getOwner().getFirstName());

    }
}
