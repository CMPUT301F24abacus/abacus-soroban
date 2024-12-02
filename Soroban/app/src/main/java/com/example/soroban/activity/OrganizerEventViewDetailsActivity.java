package com.example.soroban.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.example.soroban.FireBaseController;
import com.example.soroban.QRCodeGenerator;
import com.example.soroban.R;
import com.example.soroban.activity.EventEntrantsGeolocationActivity;
import com.example.soroban.activity.EventEntrantsListActivity;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Allows organizers to view details of a specific event.
 * Provides functionality to view event details, entrants, and QR codes.
 *
 * Author: Aaryan Shetty
 * @see Event
 * @see User
 * @see FireBaseController
 * @see EventEntrantsListActivity
 * @see OrganizerEventEditDetailsActivity
 * @see AlertDialog
 * @see QRCodeGenerator
 * @see Glide
 */
public class OrganizerEventViewDetailsActivity extends AppCompatActivity {
    private static final int EDIT_EVENT_REQUEST_CODE = 1;
    private Event selectedEvent;
    private User appUser;
    private ImageView viewQRcode;
    private ImageView eventGeolocation;
    private TextView eventTitle;
    private TextView eventDate;
    private TextView eventDescription;
    private ImageView eventPoster;
    private TextView eventSampleSize;
    private TextView eventDrawDate;
    private Button geoReq;
    private Button autoReplace;

    private Button viewEntrants;
    private Button editEvent;

    /**
     * Called when this activity is first created. Initializes UI components and populates event details.
     * @param savedInstanceState the saved instance state of the activity.
     * Author: Aaryan Shetty
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_view_event_details);

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
        // Assign button variables to views
        viewQRcode = findViewById(R.id.buttonScanQRCode);
        eventGeolocation = findViewById(R.id.buttonEventGeolocation);
        eventTitle = findViewById(R.id.eventNameTitle);
        eventDate = findViewById(R.id.eventDateText);
        eventDescription = findViewById(R.id.eventDescriptionText);
        eventPoster = findViewById(R.id.eventPosterImage);
        eventSampleSize = findViewById(R.id.eventSampleSizeText);
        eventDrawDate = findViewById(R.id.eventDrawDateText);
        geoReq = findViewById(R.id.eventGeoReqSwitch);
        autoReplace = findViewById(R.id.eventAutoReplaceSwitch);

        viewEntrants = findViewById(R.id.buttonViewEntrants);
        editEvent = findViewById(R.id.buttonEditEventDetails);


        // Populate Event Details
        eventTitle.setText(selectedEvent.getEventName());
        eventDescription.setText(selectedEvent.getEventDetails());
        String formattedEventDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedEvent.getEventDate());
        eventDate.setText(formattedEventDate);
        eventSampleSize.setText(selectedEvent.getSampleSize().toString());
        String formattedEventDrawDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedEvent.getDrawDate());
        eventDrawDate.setText(formattedEventDrawDate);
        // geoReq.setActivated();
        // autoReplace.setActivated();


        //Log.d("EventPosterURL", "Poster URL: " + selectedEvent.getPosterUrl());
        FireBaseController fireBaseController = new FireBaseController(this);
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


        // Set up listeners for applicable buttons
        viewQRcode.setOnClickListener(v -> {
            FireBaseController firebaseController = new FireBaseController(this);

            firebaseController.fetchQRCodeHash(selectedEvent.getEventName(), qrCodeHash -> {
                if (qrCodeHash != null) {
                    // Generate the QR code bitmap using the hash from firebase
                    Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(qrCodeHash);
                    if (qrCodeBitmap != null) {
                        // Inflate the custom dialog layout
                        View dialogView = getLayoutInflater().inflate(R.layout.activity_view_qr_code, null);
                        ImageView qrCodeImageView = dialogView.findViewById(R.id.qrCodeImageView);
                        qrCodeImageView.setImageBitmap(qrCodeBitmap); // Set the QR code bitmap
                        // Pop-up the QRCode Image
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setView(dialogView);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        // Dismiss QRCode Image when touching outside or pressing the back button
                        dialog.setCanceledOnTouchOutside(true);
                    } else {
                        Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "QR code not available", Toast.LENGTH_SHORT).show();
                }
            });
        });


        eventGeolocation.setOnClickListener(v -> {
            if(selectedEvent.requiresGeolocation()){
                Intent intent = new Intent(OrganizerEventViewDetailsActivity.this, EventEntrantsGeolocationActivity.class);
                Bundle newArgs = new Bundle();
                newArgs.putSerializable("selectedEvent", selectedEvent);
                newArgs.putSerializable("appUser", appUser);
                intent.putExtras(newArgs);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Your event does not have geolocation enabled", Toast.LENGTH_SHORT).show();
            }
        });

        viewEntrants.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerEventViewDetailsActivity.this, EventEntrantsListActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("selectedEvent", selectedEvent);
            newArgs.putSerializable("appUser", appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
        });

        editEvent.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerEventViewDetailsActivity.this, OrganizerEventEditDetailsActivity.class);
            intent.putExtra("selectedEvent", selectedEvent);
            intent.putExtra("appUser", appUser);
            startActivity(intent);
            finish();
        });
    }




}