package com.example.soroban.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.Notification;
import com.example.soroban.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

/**
 * Handles the display and management of an event the user is either
 * registered for or waitlisted for, allowing the user to unregister or leave the waitlist.
 * @author
 * @see Event
 * @see User
 * @see FireBaseController
 * @see UserDashboardActivity
 * @see Notification
 */
public class UserEventActivity extends AppCompatActivity {
    private Event selectedEvent;
    private User appUser;
    private TextView eventNameTV;
    private TextView eventDetailsTV;
    private Button notifyButton;
    private Button unregisterButton;
    private ImageView eventPoster;
    private ImageView eventQR;
    private FireBaseController fireBaseController;
    String listType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Bundle args = getIntent().getExtras();

        // Initialize appUser and selected Event for this activity.
        if(args != null){
            listType = args.getString("listType");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                selectedEvent = args.getSerializable("selectedEvent", Event.class);
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
                selectedEvent = (Event) args.getSerializable("selectedEvent");
            }

            if(appUser == null || selectedEvent == null){
                throw new IllegalArgumentException("Must pass object of type User and Event to initialize appUser.");
            }

            if(Objects.equals(listType, "waitList")){
                selectedEvent = appUser.getWaitList().find(selectedEvent);
            }else if(Objects.equals(listType, "registeredEvents")){
                selectedEvent = appUser.getRegisteredEvents().find(selectedEvent);
            }

        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        fireBaseController = new FireBaseController(this);

        // Initialize buttons, etc.
        notifyButton = findViewById(R.id.btn_notify_me);
        unregisterButton = findViewById(R.id.btn_register);
        eventDetailsTV = findViewById(R.id.event_details);
        eventNameTV = findViewById(R.id.event_name);
        eventPoster = findViewById(R.id.event_image);
        eventQR = findViewById(R.id.event_qr_code);

        // Change the text
        if(Objects.equals(listType, "waitList")){
            unregisterButton.setText("Leave wait list");
        }else if(Objects.equals(listType, "registeredEvents")){
            unregisterButton.setText("Unregister");
        }
        eventNameTV.setText(selectedEvent.getEventName());
        String eventDetails = "Event Date: " + selectedEvent.getEventDate().toString() + "\nEvent Details: " + selectedEvent.getEventDetails();
        eventDetailsTV.setText(eventDetails);
        // Still a work in progress
        if (selectedEvent.getQRCode() != null) {
            eventQR.setImageBitmap(selectedEvent.getQRCode());
        }
        // Finish when event posters are uploadable
        // FirebaseDatabase.getInstance().getReference("events").child(...).get()

        // Remove Event from User waitlist
        unregisterButton.setOnClickListener( v -> {
            if (listType.equals("waitList")) {
                appUser.removeFromWaitlist(selectedEvent); // Technically this should be done via UserController; this can be amended later as in this cas it is a formality
                fireBaseController.removeFromWaitListDoc(selectedEvent,appUser);
            } else if (listType.equals("registeredEvents")) {
                appUser.removeRegisteredEvent(selectedEvent); // Technically this should be done via UserController; this can be amended later as in this cas it is a formality
                fireBaseController.removeAttendeeDoc(selectedEvent,appUser);
                fireBaseController.updateThoseNotGoing(selectedEvent,appUser);

                // Re-sample a new user
                ArrayList<Integer> reSampledIndices = selectedEvent.sampleEntrants(1); // Re-sample one user

                // If a User was re-sampled
                if(reSampledIndices.size() == 1){
                    User user = selectedEvent.getInvitedEntrants().get(reSampledIndices.get(0));
                    fireBaseController.updateInvited(selectedEvent, user);
                    fireBaseController.updateUserInvited(user, selectedEvent);
                    fireBaseController.removeFromWaitListDoc(selectedEvent, user);

                    // Notify invited entrant that they have been re-sampled
                    Notification newNotif = new Notification("You have be re-sampled!", "", Calendar.getInstance().getTime(), selectedEvent, selectedEvent.getNumberOfNotifications());
                    fireBaseController.updateUserNotifications(user, newNotif);
                }
                // Else there are no other waiting entrants
            }
            Intent intent = new Intent(UserEventActivity.this, UserDashboardActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser",appUser);
            intent.putExtras(newArgs);
            startActivity(intent);

        });
    }
}
