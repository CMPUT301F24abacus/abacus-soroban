package com.example.soroban.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.User;
import com.google.android.material.navigation.NavigationView;

/**
 * Handles setting up side navigation bar
 * @author Aaryan Shetty
 * @author Ayan Chaudry
 */
public class BaseActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigationItemSelected(item.getItemId());
            drawerLayout.closeDrawers();
            return true;
        });
    }

    /**
     * Sets content view given a layout resource id.
     * @author Aaryan Shetty
     * @author Ayan Chaudry
     * @param layoutResID Id of the resource layout
     */
    @Override
    public void setContentView(int layoutResID) {
        DrawerLayout fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout frameLayout = fullLayout.findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, frameLayout, true);
        super.setContentView(fullLayout);
    }

    /**
     * Handles navigation drawer item selection.
     *
     * @param itemId the selected item's ID
     */
    private void handleNavigationItemSelected(int itemId) {
        Intent intent = null;

        if (itemId == R.id.nav_user_dashboard) {
            intent = new Intent(this, UserDashboardActivity.class);
        } else if (itemId == R.id.nav_organizer_dashboard) {
            intent = new Intent(this, OrganizerDashboardActivity.class);
        } else if (itemId == R.id.nav_admin_dashboard) {
            intent = new Intent(this, AdminDashboardActivity.class);
        }

        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
