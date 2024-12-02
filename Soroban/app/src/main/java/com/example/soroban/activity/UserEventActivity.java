package com.example.soroban.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.soroban.FireBaseController;
import com.example.soroban.QRCodeGenerator;
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
    private TextView eventDateTV;
    private TextView drawDateTV;
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
        if (args != null) {
            Log.d("UserEventActivity", "Extras: " + args.toString());
        } else {
            Log.e("UserEventActivity", "No extras passed!");
        }

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



        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        fireBaseController = new FireBaseController(this);

        // Initialize buttons, etc.
        // notifyButton = findViewById(R.id.btn_notify_me);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        unregisterButton = findViewById(R.id.btn_register);
        eventDetailsTV = findViewById(R.id.event_details);
        eventNameTV = findViewById(R.id.event_name);
        eventDateTV = findViewById(R.id.event_date);
        drawDateTV = findViewById(R.id.event_draw_date);
        eventPoster = findViewById(R.id.event_image);
        eventQR = findViewById(R.id.event_qr_code);

        // Change the text
        if(Objects.equals(listType, "waitList")){
            unregisterButton.setText("Leave Waitlist");
            unregisterButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.notification_negative_background));
        }else if(Objects.equals(listType, "registeredEvents")){
            unregisterButton.setText("Unregister");
            unregisterButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.notification_negative_background));
        }
        eventNameTV.setText(selectedEvent.getEventName());
        String eventDetails = "Event Details: " + ((selectedEvent.getEventDetails() != null) ? selectedEvent.getEventDetails() : "No Event Details");
        eventDetailsTV.setText(eventDetails);
        eventDateTV.setText("Event Date: " + dateFormat.format(selectedEvent.getEventDate()));
        drawDateTV.setText("Draw Date: " + dateFormat.format(selectedEvent.getDrawDate()));
        // QR Code
        fireBaseController.fetchQRCodeHash(selectedEvent.getEventName(), qrCodeHash -> {
            if (qrCodeHash != null) {
                // Generate the QR code bitmap using the hash from firebase
                Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(qrCodeHash);
                if (qrCodeBitmap != null) {
                    eventQR.setImageBitmap(qrCodeBitmap); // Set the QR code bitmap
                }
            }
        });
        // Posters
        if (selectedEvent.getPosterUrl() != null && !selectedEvent.getPosterUrl().isEmpty()) {
            Log.d("EventPosterURL", "Poster URL: " + selectedEvent.getPosterUrl());
            Glide.with(this)
                    .load(selectedEvent.getPosterUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(eventPoster);
        } else {
            eventPoster.setImageResource(R.drawable.ic_event_image); // Set a default image if no poster is available
        }

        fireBaseController.fetchEventPosterUrl(selectedEvent,
                posterUrl -> {
                    // Check if the fetched posterUrl is null or empty
                    if ("no poster".equals(posterUrl)) {
                        // Set default image if no poster URL is available
                        eventPoster.setImageResource(R.drawable.ic_event_image);
                    } else {
                        // Success: Update the poster URL in the UI
                        selectedEvent.setPosterUrl(posterUrl);
                        // Load the poster image with Glide
                        Glide.with(this)
                                .load(selectedEvent.getPosterUrl())
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable Glide's disk cache
                                .skipMemoryCache(true) // Disable in-memory cache
                                .into(eventPoster);
                    }
                },
                error -> {
                    // Handle errors and set a default image
                    Log.e("OrganizerEventView", "Failed to fetch posterUrl", error);
                    Toast.makeText(this, "Failed to load event poster.", Toast.LENGTH_SHORT).show();
                    eventPoster.setImageResource(R.drawable.ic_event_image);
                });
        // Initialize controllers to update User
        FireBaseController fireBaseController = new FireBaseController(this);

        // Remove Event from User waitlist
        unregisterButton.setOnClickListener( v -> {
            if (listType.equals("waitList")) {
                appUser.removeFromWaitlist(selectedEvent); // Technically this should be done via UserController; this can be amended later as in this cas it is a formality
                fireBaseController.removeFromWaitListDoc(selectedEvent,appUser);
                Toast.makeText(this, "You have left the waitlist.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "You have unregistered from the event.", Toast.LENGTH_SHORT).show();
                // Else there are no other waiting entrants
            }
            Intent intent = new Intent(UserEventActivity.this, UserDashboardActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("appUser",appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
            finish();

        });
    }
}
