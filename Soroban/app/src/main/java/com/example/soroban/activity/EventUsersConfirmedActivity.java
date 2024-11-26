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
import com.example.soroban.adapter.UserArrayAdapter;
import com.example.soroban.fragment.SendMessageFragment;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;
import com.example.soroban.model.UserList;

/**
 * Displays the list of users who have confirmed their participation
 * in a selected event. The organizer can send messages to these attendees through the interface provided.
 * @author Aaryan Shetty
 * @see Event
 * @see User
 * @see FireBaseController
 * @see UserArrayAdapter
 * @see SendMessageFragment
 */
public class EventUsersConfirmedActivity extends AppCompatActivity {
    private User appUser;
    private Event selectedEvent;
    private FireBaseController fireBaseController;
    private ListView listView;
    private UserList listData;
    private UserArrayAdapter listAdapter;
    private Button sendMessage;

    /**
     * Called when this activity is first created.
     * @param savedInstanceState the saved state of the activity
     * @throws IllegalArgumentException if required arguments (e.g., appUser or selectedEvent) are missing.
     * Author: Aaryan Shetty
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_entrants_confirmed);

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
        listData = selectedEvent.getAttendees();

        // Reference to list view
        listView = findViewById(R.id.usersFinalListView);

        // Setup adapter for list view
        listAdapter = new UserArrayAdapter(this,listData);
        listView.setAdapter(listAdapter);

        // Assign button variables to views
        sendMessage = findViewById(R.id.buttonSendMessageToUsers);

        // Set up reactions for when the buttons are clicked
        sendMessage.setOnClickListener(v -> {
            SendMessageFragment fragment = SendMessageFragment.newInstance(selectedEvent,appUser, "attendees");
            fragment.show(getSupportFragmentManager(), "Send Message");
        });
    }
}
