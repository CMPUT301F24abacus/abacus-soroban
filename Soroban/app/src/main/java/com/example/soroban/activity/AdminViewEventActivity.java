package com.example.soroban.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import java.util.Objects;

public class AdminViewEventActivity extends AppCompatActivity {
    private Event selectedEvent;
    private User appUser;
    private TextView eventNameTV;
    private TextView eventDetailsTV;
    private Button deleteQRButton;
    private Button deleteEventButton;
    private ImageView eventPoster;
    private ImageView eventQR;
    private FireBaseController firebaseController;

    @Override
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
                appUser = (User) args.getSerializable("appUser");
                selectedEvent = (Event) args.getSerializable("selectedEvent");
            }

            if(appUser == null || selectedEvent == null){
                throw new IllegalArgumentException("Must pass object of type User and Event to initialize appUser.");
            }

        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        firebaseController = new FireBaseController(this);

        // Initialize buttons, etc.
        deleteQRButton = findViewById(R.id.btn_notify_me);
        deleteQRButton.setText("Delete QR Hash");
        deleteEventButton = findViewById(R.id.btn_register);
        deleteEventButton.setText("Delete Event");
        eventDetailsTV = findViewById(R.id.event_details);
        eventNameTV = findViewById(R.id.event_name);
        eventPoster = findViewById(R.id.event_image);
        eventQR = findViewById(R.id.event_qr_code);

        eventNameTV.setText(selectedEvent.getEventName());
        String eventDetails = "Event Date: " + selectedEvent.getEventDate().toString() + "\nEvent Details: " + selectedEvent.getEventDetails();
        eventDetailsTV.setText(eventDetails);
        // Still a work in progress
        if (selectedEvent.getQRCode() != null) {
            eventQR.setImageBitmap(selectedEvent.getQRCode());
        }

        deleteEventButton.setOnClickListener(v -> {
            new AlertDialog.Builder(AdminViewEventActivity.this)
                    .setTitle("Delete Image")
                    .setMessage("Would you like to delete \"" + selectedEvent.getEventName() + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        firebaseController.removeEventDoc(selectedEvent);
                        Intent intent;
                        intent = new Intent(AdminViewEventActivity.this, AdminBrowseEventActivity.class);
                        Bundle argsEvent = new Bundle();
                        argsEvent.putSerializable("appUser",appUser);
                        intent.putExtras(argsEvent);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });


    }


}
