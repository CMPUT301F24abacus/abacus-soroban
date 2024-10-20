package com.example.soroban;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class UserDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_dashboard);

        // Set up button actions
        Button scanQrCode = findViewById(R.id.btn_scan_qr_code);
        Button joinWaitingList = findViewById(R.id.btn_join_waiting_list);
        Button manageFacility = findViewById(R.id.btn_manage_facility);

        scanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for Scan QR Code
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
