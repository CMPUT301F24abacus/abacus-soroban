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

/**
 * Handles all functionalities related to notifications for the user.
 * Users can view, manage, and respond to event invitations in this activity.
 * References: ChatGPT, Stack Overflow, Geeks for Geeks
 *
 * @author Ayan Chaudhry
 * @author Matthieu Larochelle
 * @see Event
 * @see AcceptInviteFragment
 * @see EventArrayAdapter
 */
public class InvitationActivity extends AppCompatActivity implements DialogFragmentListener {
    private User appUser;
    private FireBaseController fireBaseController;
    private ListView notififcationsView;
    private EventArrayAdapter adapter;
    private EventList dataList;
    private Button muteAllButton;
    private Button clearAllButton;
    private ActivityResultLauncher<String> requestPersmissionLauncher;

    /**
     * Called when the activity is first created.
     * Initializes views, data, and event listeners.
     *
     * @param savedInstanceState The saved state of the activity.
     * @throws IllegalArgumentException if appUser is not provided in the intent extras.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitations);

        // Retrieve arguments passed from the previous activity.
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

    /**
     * Updates the adapter for the ListView.
     * Called to refresh the displayed data.
     */
    @Override
    public void update() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void returnResult (boolean result) {
    }

}
