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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.soroban.FireBaseController;
import com.example.soroban.QRCodeGenerator;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.Notification;
import com.example.soroban.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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
    private Switch geoSwitch;
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
        setContentView(R.layout.admin_view_event);

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
        Button deleteQRButton = findViewById(R.id.delete_QR_btn);
        deleteQRButton.setText("Delete QR Hash");
        Button deleteEventButton = findViewById(R.id.delete_event_btn);
        deleteEventButton.setText("Delete Event");
        TextView eventDetailsTV = findViewById(R.id.eventDescriptionAdmin);
        TextView eventOwnerTV = findViewById(R.id.eventOwnerTextAdmin);
        TextView eventNameTV = findViewById(R.id.eventTitleAdmin);
        TextView eventDateTV = findViewById(R.id.eventDateAdmin);
        TextView drawDateTV = findViewById(R.id.eventDrawDateTextAdmin);
        TextView sampleSizeTV = findViewById(R.id.eventSampleSizeTextAdmin);
        TextView entrantLimitTV = findViewById(R.id.eventEntrantLimitTextAdmin);
        ImageView eventPoster = findViewById(R.id.eventPosterImageAdmin);
        eventQR = findViewById(R.id.admin_qr_code);
        geoSwitch = findViewById(R.id.eventGeoReqSwitchAdmin);

        eventNameTV.setText(selectedEvent.getEventName());
        eventOwnerTV.setText(selectedEvent.getOwner().getDeviceId());
        String eventDetails = selectedEvent.getEventDetails();
        eventDetailsTV.setText((eventDetails != null) ? eventDetails : "No Details Set");
        eventDateTV.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedEvent.getEventDate()));
        drawDateTV.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedEvent.getDrawDate()));
        sampleSizeTV.setText(selectedEvent.getSampleSize().toString());
        Integer entrantLimit = selectedEvent.getMaxEntrants();
        if (entrantLimit != null) {
            entrantLimitTV.setText(entrantLimit.toString());
        }
        geoSwitch.setChecked(selectedEvent.requiresGeolocation());


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
                        firebaseController.updateUserNotifications(selectedEvent.getOwner(), new Notification("Deleted Event", "Deleted " + selectedEvent.getEventName(), Calendar.getInstance().getTime(), selectedEvent, selectedEvent.getNumberOfNotifications()));
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
                                firebaseController.updateUserNotifications(selectedEvent.getOwner(), new Notification("Deleted Event's QR Hash", "Deleted " + selectedEvent.getEventName() + "'s QR Hash.", Calendar.getInstance().getTime(), selectedEvent, selectedEvent.getNumberOfNotifications()));
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
