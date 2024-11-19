package com.example.soroban.activity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.adapter.UserArrayAdapter;
import com.example.soroban.fragment.SendMessageFragment;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.User;
import com.example.soroban.model.UserList;

import java.util.Objects;


public class EventUsersWaitlistedActivity extends AppCompatActivity {
    private User appUser;
    private Event selectedEvent;
    private FireBaseController fireBaseController;
    private ListView listView;
    private UserList listData;
    private UserArrayAdapter listAdapter;
    private Button sendMessage;
    private Button rejectUsers;
    private Button inviteUsers;

    /**
     * Called when this activity is first created.
     * @param savedInstanceState
     * Author: Aaryan Shetty, Matthieu Larochelle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_entrants_waitlist);

        Bundle args = getIntent().getExtras();

        // Initialize appUser and selected Event for this activity.
        if(args != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                selectedEvent = args.getSerializable("selectedEvent", Event.class);
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
                selectedEvent = (Event) args.getSerializable("selectedEvent");
            }

            if(appUser == null || selectedEvent == null){
                throw new IllegalArgumentException("Must pass object of type User and Event to initialize appUser.");
            }else{
                selectedEvent = appUser.getHostedEvents().find(selectedEvent);
            }

        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        fireBaseController = new FireBaseController(this);

        // Grab event data from appUser
        listData = selectedEvent.getWaitingEntrants();

        // Reference to list view
        listView = findViewById(R.id.usersWaitlistView);

        // Setup adapter for list view
        listAdapter = new UserArrayAdapter(this,listData);
        listView.setAdapter(listAdapter);

        // Assign button variables to views
        sendMessage = findViewById(R.id.buttonSendMessageToUsers);

        // Set up reactions for when the buttons are clicked
        sendMessage.setOnClickListener(v -> {
            SendMessageFragment fragment = SendMessageFragment.newInstance(selectedEvent,appUser, "waitList");
            fragment.show(getSupportFragmentManager(), "Send Message");
        });


    }
}
