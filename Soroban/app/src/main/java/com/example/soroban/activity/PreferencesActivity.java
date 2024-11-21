package com.example.soroban.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.soroban.R;
import com.example.soroban.model.User;

public class PreferencesActivity extends AppCompatActivity {
    private User appUser;
    private Switch notifSwitch;
    private NotificationManagerCompat notifManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

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

        // Set up notifications manager
        notifManager = NotificationManagerCompat.from(this);


        // Reference for notifications switch
        notifSwitch = findViewById(R.id.notifications_switch);


        notifSwitch.setOnCheckedChangeListener((view,isChecked) ->{
            // Send user to app Notifcations Settings
            Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(settingsIntent);
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        // Update appearance of notifications switch
        boolean notifsEnabled = notifManager.areNotificationsEnabled();
        notifSwitch.setChecked(notifsEnabled);
        if(notifsEnabled){
            notifSwitch.setText("Disable Notifications");
        }
    }

}