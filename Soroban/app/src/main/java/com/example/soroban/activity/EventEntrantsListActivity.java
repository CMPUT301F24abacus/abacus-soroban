package com.example.soroban.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.Notification;
import com.example.soroban.model.User;

import java.util.Calendar;

/**
 * Allows event organizers to view and manage different categories of entrants
 * (waitlisted, invited, confirmed, and cancelled) for a selected event.
 * It also provides functionality for sampling entrants and sending notifications based on the results.
 * @author Matthieu Larochelle
 * @author Aaryan Shetty
 * @see Event
 * @see User
 * @see FireBaseController
 * @see Notification
 * @see EventUsersWaitlistedActivity
 * @see EventUsersInvitedActivity
 * @see EventUsersCancelledActivity
 * @see EventUsersConfirmedActivity
 */
public class EventEntrantsListActivity extends AppCompatActivity {
    private Event selectedEvent;
    private User appUser;
    private FireBaseController fireBaseController;
    private Button usersWaitlisted;
    private Button usersInvited;
    private Button usersCancelled;
    private Button usersConfirmed;
    private Button sampleEntrants;

    /**
     * Called when this activity is first created.
     * @param savedInstanceState
     * Author: Matthieu Larochelle, Aaryan Shetty
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_entrant_list_buttons);

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

        // Assign button variables to views
        usersWaitlisted = findViewById(R.id.buttonWaitlistedUsers);
        usersInvited = findViewById(R.id.buttonInvitedUsers);
        usersCancelled = findViewById(R.id.buttonCancelledUsers);
        usersConfirmed = findViewById(R.id.buttonConfirmedUsers);
        sampleEntrants = findViewById(R.id.buttonSampleUsers);

        // Set up reactions for when the buttons are clicked
        usersWaitlisted.setOnClickListener(v -> {
            Intent intent = new Intent(EventEntrantsListActivity.this, EventUsersWaitlistedActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("selectedEvent", selectedEvent);
            newArgs.putSerializable("appUser", appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
        });

        usersInvited.setOnClickListener(v -> {
            Intent intent = new Intent(EventEntrantsListActivity.this, EventUsersInvitedActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("selectedEvent", selectedEvent);
            newArgs.putSerializable("appUser", appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
        });

        usersCancelled.setOnClickListener(v -> {
            Intent intent = new Intent(EventEntrantsListActivity.this, EventUsersCancelledActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("selectedEvent", selectedEvent);
            newArgs.putSerializable("appUser", appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
        });

        usersConfirmed.setOnClickListener(v -> {
            Intent intent = new Intent(EventEntrantsListActivity.this, EventUsersConfirmedActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("selectedEvent", selectedEvent);
            newArgs.putSerializable("appUser", appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
        });

        sampleEntrants.setOnClickListener(v ->{
            int numberSampled = selectedEvent.sampleEntrants(selectedEvent.getSampleSize()).size();
            Toast.makeText(this, numberSampled + " entrant(s) sampled successfully.", Toast.LENGTH_SHORT).show();
            // Schedule a new notification for all relevant users

            // Update Firebase to recognize invited users
            for(int i = 0; i < selectedEvent.getInvitedEntrants().size(); i++){
                User user = selectedEvent.getInvitedEntrants().get(i);
                fireBaseController.updateInvited(selectedEvent, user);
                fireBaseController.updateUserInvited(user, selectedEvent);
                fireBaseController.removeFromWaitListDoc(selectedEvent, user);

                // Update model class 
                selectedEvent.addInvited(user);
                selectedEvent.removeFromWaitingEntrants(user);

                // Notify those invited entrants that they have been sampled
                Notification newNotif = new Notification("You won the draw!", "", Calendar.getInstance().getTime(), selectedEvent, selectedEvent.getNumberOfNotifications());
                fireBaseController.updateUserNotifications(user, newNotif);
            }

            // Notify those still on waiting list that they have not been sampled
            for(int i = 0; i < selectedEvent.getWaitingEntrants().size(); i++){
                Notification newNotif = new Notification("You were not selected in the draw.", "", Calendar.getInstance().getTime(), selectedEvent,selectedEvent.getNumberOfNotifications());
                fireBaseController.updateUserNotifications(selectedEvent.getWaitingEntrants().get(i), newNotif);
            }

        });

    }
}
