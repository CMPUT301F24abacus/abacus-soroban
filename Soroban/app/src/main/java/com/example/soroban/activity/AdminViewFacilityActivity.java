package com.example.soroban.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.fragment.AdminMessageFragment;
import com.example.soroban.fragment.SendMessageFragment;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

/**
 * Displays the details of a selected facility and
 * provides an option for administrators to delete the facility.
 * @author Kevin Li
 * @see AdminBrowseFacilityActivity
 * @see Facility
 * @see User
 * @see FireBaseController
 */
public class AdminViewFacilityActivity extends AppCompatActivity {
    private User appUser;
    private Facility selectedFacility;
    private TextView facilityNameText;
    private TextView facilityDetailsText;
    private TextView facilityOwnerText;
    private TextView violationText;
    private Button deleteFacilityBtn;
    private FireBaseController firebaseController;

    /**
     * Initializes the activity. Sets up the UI components and retrieves
     * the selected facility and admin user passed as arguments.
     *
     * @param savedInstanceState the saved state of the activity.
     * @throws IllegalArgumentException if required arguments are missing.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view_facility);

        Bundle args = getIntent().getExtras();

        // Initialize/Retrieve appUser and selectedFacility from the passed arguments
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

        // Populate facility details
        facilityNameText.setText(selectedFacility.getName());
        if (selectedFacility.getDetails() != null) {
            facilityDetailsText.setText(selectedFacility.getDetails());
        } else {
            facilityDetailsText.setText("No Details Available");
        }
        facilityOwnerText.setText("ID: " + selectedFacility.getOwner().getDeviceId());

        // Set up delete facility button functionality
        deleteFacilityBtn.setOnClickListener(v -> {

            new AlertDialog.Builder(AdminViewFacilityActivity.this)
                    .setTitle("Delete Facility")
                    .setMessage("Would you like to delete \"" + selectedFacility.getName() + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        Log.d("FireStore", "Delete is pressed.");
                        AdminMessageFragment fragment = AdminMessageFragment.newInstance(selectedFacility, appUser);
                        fragment.show(getSupportFragmentManager(), "Send Message");


                    })
                    .setNegativeButton("No", null)
                    .show();

            //finish();

        });


    }
}
