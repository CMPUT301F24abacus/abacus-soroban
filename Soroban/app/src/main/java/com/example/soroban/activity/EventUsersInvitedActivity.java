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
import com.example.soroban.model.Notification;
import com.example.soroban.model.User;
import com.example.soroban.model.UserList;

import java.util.Calendar;

/**
 * Displays the list of users invited to a specific event.
 * The organizer can send messages to invited users or withdraw their invitations.
 * @author Aaryan Shetty
 * @author Matthieu Larochelle
 * @see Event
 * @see User
 * @see FireBaseController
 * @see UserArrayAdapter
 * @see SendMessageFragment
 * @see Notification
 */
public class EventUsersInvitedActivity extends AppCompatActivity {
    private User appUser;
    private Event selectedEvent;
    private FireBaseController fireBaseController;
    private ListView listView;
    private UserList listData;
    private UserArrayAdapter listAdapter;
    private Button sendMessage;
    private Button withdrawInvite;

    /**
     * Called when this activity is first created.
     *
     * @param savedInstanceState the saved state of the activity
     * @throws IllegalArgumentException if required arguments (e.g., appUser or selectedEvent) are missing.
     * Author: Aaryan Shetty, Matthieu Larochelle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_entrants_invited);

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
        listData = selectedEvent.getInvitedEntrants();

        // Reference to list view
        listView = findViewById(R.id.usersInvitedView);

        // Setup adapter for list view
        listAdapter = new UserArrayAdapter(this,listData);
        listView.setAdapter(listAdapter);

        // Assign button variables to views
        sendMessage = findViewById(R.id.buttonSendMessageToUsers);
        withdrawInvite = findViewById(R.id.buttonWithdrawUsersInvite);

        // Set up reactions for when the buttons are clicked
        sendMessage.setOnClickListener(v -> {
            SendMessageFragment fragment = SendMessageFragment.newInstance(selectedEvent,appUser, "invited");
            fragment.show(getSupportFragmentManager(), "Send Message");
        });

        withdrawInvite.setOnClickListener(v -> {
            int numberCancelled = selectedEvent.getInvitedEntrants().size();

            // Update Firebase to recognize cancelled users (i.e. all users who had invites)
            for(int i = selectedEvent.getInvitedEntrants().size() - 1; i >= 0 ; i--){
                User user = selectedEvent.getInvitedEntrants().get(i);
                fireBaseController.updateThoseNotGoing(selectedEvent, user);
                fireBaseController.removeInvitedDoc(selectedEvent, user);

                // Update model class
                selectedEvent.addToNotGoing(user);
                selectedEvent.removeFromInvited(user);

                // Notify those invited entrants that they have been cancelled
                Notification newNotif = new Notification("Your invitation has been cancelled.", "", Calendar.getInstance().getTime(), selectedEvent, selectedEvent.getNumberOfNotifications());
                fireBaseController.updateUserNotifications(user, newNotif);
            }

            // Resample entrants
            int numberSampled = selectedEvent.sampleEntrants(numberCancelled).size();
            Toast.makeText(this, numberSampled + " entrants were re-sampled.", Toast.LENGTH_SHORT).show();
            // Schedule a new notification for all relevant users
            selectedEvent.addNumberOfNotifications();

            // Update Firebase to recognize invited users
            for(int i = 0; i < selectedEvent.getInvitedEntrants().size(); i++){
                User user = selectedEvent.getInvitedEntrants().get(i);
                fireBaseController.updateInvited(selectedEvent, user);
                fireBaseController.updateUserInvited(user, selectedEvent);
                fireBaseController.removeFromWaitListDoc(selectedEvent, user);

                // Update model class
                selectedEvent.addInvited(user);
                selectedEvent.removeFromWaitingEntrants(user);

                // Notify those invited entrants that they have been re-sampled
                Notification newNotif = new Notification("You have been re-sampled!", "", Calendar.getInstance().getTime(), selectedEvent, selectedEvent.getNumberOfNotifications());
                fireBaseController.updateUserNotifications(user, newNotif);
            }

            listAdapter.notifyDataSetChanged();

        });
    }
}
