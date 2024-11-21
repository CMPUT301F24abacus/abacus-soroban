package com.example.soroban.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.R;
import com.example.soroban.model.User;

public class AdminDashboardActivity extends AppCompatActivity {
    private User appUser;
    private Button browseEventBtn;
    private Button browseProfileBtn;
    private Button browseImageBtn;
    private Button browseFacilityBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_dashboard);

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


        browseEventBtn = findViewById(R.id.browse_event_btn);
        browseProfileBtn = findViewById(R.id.browse_profile_btn);
        browseFacilityBtn = findViewById(R.id.browse_facility_btn);
        browseImageBtn = findViewById(R.id.browse_image_btn);

        browseEventBtn.setOnClickListener( v -> {
            Intent intent;
            intent = new Intent(AdminDashboardActivity.this, AdminBrowseEventActivity.class);
            Bundle argsEvent = new Bundle();
            argsEvent.putSerializable("appUser",appUser);
            intent.putExtras(argsEvent);
            startActivity(intent);
        });

        browseProfileBtn.setOnClickListener( v -> {
            Intent intent;
            intent = new Intent(AdminDashboardActivity.this, AdminBrowseProfileActivity.class);
            startActivity(intent);
        });

        browseFacilityBtn.setOnClickListener( v -> {
            Intent intent;
            intent = new Intent(AdminDashboardActivity.this, AdminBrowseFacilityActivity.class);
            startActivity(intent);
        });

        browseImageBtn.setOnClickListener( v -> {
            Intent intent;
            intent = new Intent(AdminDashboardActivity.this, AdminBrowseImagesActivity.class);
            startActivity(intent);
        });


    }

}
