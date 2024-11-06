package com.example.soroban.activity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Bundle args = getIntent().getExtras();

        // Initialize appUser and selected Event for this activity.
        if(args != null){
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

        // Change the text
        unregisterButton.setText("Unregister");
        eventNameTV.setText(selectedEvent.getEventName());
        //Date eventDate = (Date) selectedEvent.getEventDate();
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //String eventDateS = dateFormat.format(eventDate);
        String eventDetails = "Event Date: \nEvent Details: " + selectedEvent.getEventDetails();
        eventDetailsTV.setText(eventDetails);
    }
}
