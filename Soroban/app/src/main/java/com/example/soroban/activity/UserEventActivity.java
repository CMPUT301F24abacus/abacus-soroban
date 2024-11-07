package com.example.soroban.activity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserEventActivity extends AppCompatActivity {
    private Event selectedEvent;
    private User appUser;
    private TextView eventNameTV;
    private TextView eventDetailsTV;
    private Button notifyButton;
    private Button unregisterButton;
    private ImageView eventPoster;
    private ImageView eventQR;
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
                selectedEvent = (Event) args.getSerializable("selectedEvent");
                appUser = (User) args.getSerializable("appUser");
            }

            if(appUser == null || selectedEvent == null){
                throw new IllegalArgumentException("Must pass object of type User and Event to initialize appUser.");
            }

        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

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

        // Button currently does not remove the event
        unregisterButton.setOnClickListener( v -> {
            if (listType.equals("waitList")) {
                appUser.removeFromWaitlist(selectedEvent);
                selectedEvent.addToNotGoing(appUser);
                finish();
            } else if (listType.equals("registeredEvents")) {
                appUser.removeRegisteredEvent(selectedEvent);
                selectedEvent.addToNotGoing(appUser);
                finish();
            }

        });
    }
}
