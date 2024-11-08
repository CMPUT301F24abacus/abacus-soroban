package com.example.soroban;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class UserEventViewDetailsActivity extends AppCompatActivity {
    private User appUser;
    private Event event;
    private FireBaseController firebaseController;
    private TextView eventName;
    private TextView eventDate;
    private ImageView eventPoster;
    private TextView eventDescription;
    private Button joinWaitingList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_event_view_details);

        Bundle args = getIntent().getExtras();

        // Initialize appUser and selected Event for this activity.
        if(args != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                event = args.getSerializable("selectedEvent", Event.class);
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
                event = (Event) args.getSerializable("selectedEvent");
            }

            if(appUser == null || event == null){
                throw new IllegalArgumentException("Must pass object of type User and Event to initialize appUser.");
            }

        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        // Initialize views
        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventPoster = findViewById(R.id.eventPoster);
        eventDescription = findViewById(R.id.eventDescription);
        joinWaitingList = findViewById(R.id.buttonJoinWaitingList);

        // Set event details
        eventName.setText(event.getEventName());
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(event.getEventDate());
        eventDate.setText(formattedDate);
        eventDescription.setText(event.getEventDetails());

        // Placeholder for event poster (assuming a method to set image exists)
        eventPoster.setImageDrawable(getResources().getDrawable(R.drawable.ic_event_image));

        // Handle Join Waiting List button click
        joinWaitingList.setOnClickListener(v -> {
            // If there is room for user to join event
            if(event.getMaxEntrants() > event.getWaitingEntrants().size()){
                appUser.addToWaitlist(event);
                firebaseController.updateUserWaitList(appUser,event);
                firebaseController.updateEventWaitList(event,appUser);
                Toast.makeText(this, "Added to " + event.getEventName() + " wait list.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Sorry, this event has reached its maximum number of entrants", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
