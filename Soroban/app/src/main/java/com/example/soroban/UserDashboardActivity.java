/**
 * Represents the user's dashboard activity, which is supposed to be the main screen for the users.
 * Author: Ayan Chaudhry
 * Resources used: ChatGPT for code assistance
 * @version 1.0
 */

package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.EventRegistrationActivity;

public class UserDashboardActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     * @param savedInstanceState
     * Author: Ayan Chaudhry
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_dashboard);
        /**
         * Set up button actions.
         * Buttons are: Scan QR Code, Join Waiting List, Manage Facility
         */

        Button scanQrCode = findViewById(R.id.btn_scan_qr_code);
        Button joinWaitingList = findViewById(R.id.btn_join_waiting_list);
        Button manageFacility = findViewById(R.id.btn_manage_facility);

        /**
         * Set up button click listeners.
         */
        scanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for Scan QR Code


                Intent intent = new Intent(UserDashboardActivity.this, EventRegistrationActivity.class);
                startActivity(intent); // Redirect to EventRegistrationActivity
            }
        });

        joinWaitingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for Join Waiting List
            }
        });

        manageFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for Create/Manage Facility
            }
        });
    }
}
