package com.example.soroban.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.R;

public class AdminDashboardActivity extends AppCompatActivity {
    private Button browseEventBtn;
    private Button browseProfileBtn;
    private Button browseImageBtn;
    private Button browseFacilityBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_dashboard);

        browseEventBtn = findViewById(R.id.browse_event_btn);
        browseProfileBtn = findViewById(R.id.browse_profile_btn);
        browseFacilityBtn = findViewById(R.id.browse_facility_btn);
        browseImageBtn = findViewById(R.id.browse_image_btn);

        browseEventBtn.setOnClickListener( v -> {
            Intent intent;
            intent = new Intent(AdminDashboardActivity.this, AdminBrowseEventActivity.class);
            startActivity(intent);
        });


    }

}
