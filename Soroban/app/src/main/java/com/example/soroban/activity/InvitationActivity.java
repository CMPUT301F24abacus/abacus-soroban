/**
 * Author: Ayan Chaudhry, Matthieu
 * References: ChatGPT, Stack Overflow, Geeks for Geeks
 * this class contains all the functionalities related to Notifications for the User
 */

package com.example.soroban.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.fragment.AcceptInviteFragment;
import com.example.soroban.fragment.DialogFragmentListener;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.User;

public class InvitationActivity extends AppCompatActivity implements DialogFragmentListener {
    private User appUser;
    private FireBaseController fireBaseController;
    private ListView notififcationsView;
    private EventArrayAdapter adapter;
    private EventList dataList;
    private Button muteAllButton;
    private Button clearAllButton;
    private ActivityResultLauncher<String> requestPersmissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitations);

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

        // Grab event data from appUser
        dataList = appUser.getInvitedEvents();
        adapter = new EventArrayAdapter(this, dataList);

        // Reference for notifications list view
        notififcationsView = findViewById(R.id.notifications_view);
        notififcationsView.setAdapter(adapter);

        // Set up listener for notifications view
        notififcationsView.setOnItemClickListener((adapterView, view, position, id) -> {
            Event selectedEvent = dataList.get(position);
            AcceptInviteFragment fragment = AcceptInviteFragment.newInstance(selectedEvent,appUser);
            fragment.show(getSupportFragmentManager(), "View invite");
        });


    }

    @Override
    public void updateAdapter() {
        adapter.notifyDataSetChanged();
    }

}
