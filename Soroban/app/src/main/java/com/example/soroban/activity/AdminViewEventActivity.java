package com.example.soroban.activity;

import android.app.AlertDialog;
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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.soroban.FireBaseController;
import com.example.soroban.QRCodeGenerator;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import java.util.Objects;

/**
 * Responsible for displaying the details of a selected event
 * and providing options to delete the event or its associated QR code.
 * @author Kevin Li
 * @see AdminBrowseEventActivity
 * @see Event
 * @see FireBaseController
 */
public class AdminViewEventActivity extends AppCompatActivity {
    private Event selectedEvent;
    private User appUser;
    private TextView eventNameTV;
    private TextView eventDetailsTV;
    private TextView eventDateTV;
    private TextView drawDateTV;
    private Button deleteQRButton;
    private Button deleteEventButton;
    private ImageView eventPoster;
    private ImageView eventQR;
    private FireBaseController firebaseController;

    /**
     * Called when the activity is first created.
     * Initializes the user interface and passes the appUser data to subsequent activities.
     *
     * @param savedInstanceState the saved state of the activity.
     * @throws IllegalArgumentException if required arguments are missing or incorrect.
     */
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
        eventDateTV = findViewById(R.id.event_date);
        drawDateTV = findViewById(R.id.event_draw_date);
        eventPoster = findViewById(R.id.event_image);
        eventQR = findViewById(R.id.event_qr_code);

        eventNameTV.setText(selectedEvent.getEventName());
        String eventDetails = selectedEvent.getEventDetails();
        eventDetailsTV.setText((eventDetails != null) ? ("Event Details: " + eventDetails) : "No Details Set");
        eventDateTV.setText("Event Date: " + selectedEvent.getEventDate().toString());
        drawDateTV.setText("Event's Draw Date: " + selectedEvent.getDrawDate().toString());

        firebaseController.fetchQRCodeHash(selectedEvent.getEventName(), qrCodeHash -> {
            if (qrCodeHash != null) {
                // Generate the QR code bitmap using the hash from firebase
                Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(qrCodeHash);
                if (qrCodeBitmap != null) {
                    eventQR.setImageBitmap(qrCodeBitmap); // Set the QR code bitmap
                }
            }
        });

        if (selectedEvent.getPosterUrl() != null && !selectedEvent.getPosterUrl().isEmpty()) {
            Log.d("EventPosterURL", "Poster URL: " + selectedEvent.getPosterUrl());
            Glide.with(this)
                    .load(selectedEvent.getPosterUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(eventPoster);
        } else {
            eventPoster.setImageResource(R.drawable.ic_event_image); // Set a default image if no poster is available
        }

        deleteEventButton.setOnClickListener(v -> {
            new AlertDialog.Builder(AdminViewEventActivity.this)
                    .setTitle("Delete Event")
                    .setMessage("Would you like to delete \"" + selectedEvent.getEventName() + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        firebaseController.removeEventDoc(selectedEvent);
                        Intent intent;
                        intent = new Intent(AdminViewEventActivity.this, AdminBrowseEventActivity.class);
                        Bundle argsEvent = new Bundle();
                        argsEvent.putSerializable("appUser",appUser);
                        intent.putExtras(argsEvent);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        deleteQRButton.setOnClickListener(v -> {
            firebaseController.fetchQRCodeHash(selectedEvent.getEventName(), qrCodeHash -> {
                if (qrCodeHash != null) {
                    new AlertDialog.Builder(AdminViewEventActivity.this)
                            .setTitle("Delete QR Hash")
                            .setMessage("Would you like to delete \"" + selectedEvent.getEventName() + "\"'s QR Hash?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                firebaseController.deleteQRCodeHash(selectedEvent);
                                eventQR.setImageResource(R.drawable.ic_qr_code);
                            })
                            .setNegativeButton("No", null)
                            .show();

                } else {
                    Toast.makeText(this, "QR code not available", Toast.LENGTH_SHORT).show();
                }
            });

        });


    }


}
