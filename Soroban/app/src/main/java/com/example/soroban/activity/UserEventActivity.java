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
import com.example.soroban.model.User;

import java.util.Objects;

public class UserEventActivity extends AppCompatActivity {
    private Event selectedEvent;
    private User appUser;
    private TextView eventNameTV;
    private TextView eventDetailsTV;
    private Button notifyButton;
    private Button unregisterButton;
    private ImageView eventPoster;
    private ImageView eventQR;
    private FireBaseController firebaseController;
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

        firebaseController = new FireBaseController(this);

        // Initialize buttons, etc.
        notifyButton = findViewById(R.id.btn_notify_me);
        unregisterButton = findViewById(R.id.btn_register);
        eventDetailsTV = findViewById(R.id.event_details);
        eventNameTV = findViewById(R.id.event_name);
        eventPoster = findViewById(R.id.event_image);
        eventQR = findViewById(R.id.event_qr_code);

        // Change the text
        unregisterButton.setText("Unregister");
        eventNameTV.setText(selectedEvent.getEventName());
        String eventDetails = "Event Date: " + selectedEvent.getEventDate().toString() + "\nEvent Details: " + selectedEvent.getEventDetails();
        eventDetailsTV.setText(eventDetails);
        // Still a work in progress
        if (selectedEvent.getQRCode() != null) {
            eventQR.setImageBitmap(selectedEvent.getQRCode());
        }
        // Finish when event posters are uploadable
        // FirebaseDatabase.getInstance().getReference("events").child(...).get()

        // Initialize controllers to update User
        FireBaseController fireBaseController = new FireBaseController(this);

        // Remove Event from User waitlist
        unregisterButton.setOnClickListener( v -> {
            if (listType.equals("waitList")) {
                appUser.removeFromWaitlist(selectedEvent); // Technically this should be done via UserController; this can be amended later as in this cas it is a formality
                fireBaseController.deleteFromUserWaitList(appUser,selectedEvent);
            } else if (listType.equals("registeredEvents")) {
                appUser.removeRegisteredEvent(selectedEvent); // Technically this should be done via UserController; this can be amended later as in this cas it is a formality
                fireBaseController.deleteFromUserRegistered(appUser,selectedEvent);
            }
            Intent intent = new Intent(UserEventActivity.this, UserDashboardActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser",appUser);
            intent.putExtras(newArgs);
            startActivity(intent);

        });
    }
}
