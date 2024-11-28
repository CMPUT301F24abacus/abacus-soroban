package com.example.soroban;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.example.soroban.fragment.ConfirmGiveLocationFragment;
import com.example.soroban.fragment.ConfirmRequireLocationFragment;
import com.example.soroban.fragment.DatePickerListener;
import com.example.soroban.fragment.DialogFragmentListener;
import com.example.soroban.fragment.GeolocationListener;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import java.util.Calendar;

/**
 * Handles the display and management of event details.
 * Allows the user to view details, register/unregister for an event, and access the event's QR code.
 * @author Edwin Manalastas
 * @author Aaryan Shetty
 * @author Matthieu Larochelle
 * @see Event
 * @see User
 * @see FireBaseController
 * @see QRCodeGenerator
 * @see AlertDialog
 */
public class EventDetailsActivity extends AppCompatActivity implements GeolocationListener {
    private FireBaseController firebaseController;
    private Event selectedEvent;
    private User appUser;
    private ImageView eventImage;
    private TextView eventName;
    private TextView eventDetails;
    private ImageView eventQRCode;
    private TextView eventDrawDate;
    private TextView eventDate;
    private Button notifyMeButton;
    private Button registerButton;
    private Button unregisterButton;
    private boolean allowLocation;
    private boolean isRegistered; // Store the registration status

    /**
     * Called when the activity is created. Initializes the UI and sets up event data.
     *
     * @param savedInstanceState the saved state of the activity.
     * @throws IllegalArgumentException if required arguments (User or Event) are missing.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Bundle args = getIntent().getExtras();
        firebaseController = new FireBaseController(this);

        if (args != null) {
            selectedEvent = (Event) args.getSerializable("eventData");
            appUser = (User) args.getSerializable("appUser");

            if (selectedEvent == null || appUser == null) {
                throw new IllegalArgumentException("Event data or App User is missing.");
            }else{
                firebaseController.fetchEventWaitlistDoc(selectedEvent);
            }
        } else {
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        // Assign button variables to views
        eventImage = findViewById(R.id.event_image);
        eventName = findViewById(R.id.event_name);
        eventDate = findViewById(R.id.event_date);
        eventDrawDate = findViewById(R.id.event_draw_date);
        eventDetails = findViewById(R.id.event_details);
        eventQRCode = findViewById(R.id.event_qr_code);
        notifyMeButton = findViewById(R.id.btn_notify_me);
        registerButton = findViewById(R.id.btn_register);
        unregisterButton = findViewById(R.id.btn_unregister);

        // Populate Event Details
        eventName.setText(selectedEvent.getEventName());
        eventDetails.setText(selectedEvent.getEventDetails());
        eventDate.setText(selectedEvent.getEventDate().toString());
        eventDrawDate.setText(selectedEvent.getDrawDate().toString());

        // Set up QR code image
        eventQRCode.setOnClickListener(v -> {
            firebaseController.fetchQRCodeHash(selectedEvent.getEventName(), qrCodeHash -> {
                if (qrCodeHash != null) {
                    Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(qrCodeHash);
                    if (qrCodeBitmap != null) {
                        showQRCodeDialog(qrCodeBitmap);
                    } else {
                        Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "QR code not available", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Set button click listeners
        notifyMeButton.setOnClickListener(v -> {
            Toast.makeText(this, "Notification feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Set up registration status
        isRegistered = getIntent().getBooleanExtra("isRegistered", false);
        updateRegistrationButtons();

        // Set button click listeners
        registerButton.setOnClickListener(v ->{
            // Check if Event has geolocation requirement
            if(selectedEvent.requiresGeolocation()){
                ConfirmGiveLocationFragment fragment = new ConfirmGiveLocationFragment();
                fragment.show(getSupportFragmentManager(), "Confirm geolocation");
            }else{
                handleRegister();
            }
        });
        unregisterButton.setOnClickListener(v -> handleUnregister());
    }

    /**
     * Displays the event QR code in a dialog.
     *
     * @param qrCodeBitmap the bitmap of the QR code to display.
     */
    private void showQRCodeDialog(Bitmap qrCodeBitmap) {
        View dialogView = getLayoutInflater().inflate(R.layout.activity_view_qr_code, null);
        ImageView qrCodeImageView = dialogView.findViewById(R.id.qrCodeImageView);
        qrCodeImageView.setImageBitmap(qrCodeBitmap);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }

    /**
     * Handles user registration for the event.
     */
    private void handleRegister() {

        // Check if current date is passed drawDate
        if(!(Calendar.getInstance().getTimeInMillis() > selectedEvent.getDrawDate().getTime())){
            // Add user to event waitlist
            boolean joinResult = selectedEvent.addToWaitingEntrants(appUser);

            if(joinResult){
                // Add the user to the users-waitlist and events-waitingEntrants
                FireBaseController firebaseController = new FireBaseController(this);
                firebaseController.updateEventWaitList(selectedEvent, appUser);
                firebaseController.updateUserWaitList(appUser, selectedEvent);

                Log.d("EventDetailsActivity", "User registration requested.");
                Toast.makeText(this, "Registered for the event!", Toast.LENGTH_SHORT).show();


                // Update UI
                isRegistered = true;
                updateRegistrationButtons();
            }else{
                // User was unable to join the waitlist
                // Could be they are already registered, or max entrants was met
                Toast.makeText(this, "Sorry, you cannot register for this event at this time!", Toast.LENGTH_SHORT).show();
            }
        }else{
            // It is past the drawDate
            Toast.makeText(this, "Sorry, the draw date for this event has already passed!", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Handles user unregistration from the event.
     */
    private void handleUnregister() {
        // Unregistration
        appUser.removeFromWaitlist(selectedEvent); // Technically this should be done via UserController; this can be amended later as in this cas it is a formality
        firebaseController.removeFromWaitListDoc(selectedEvent,appUser);
        Toast.makeText(this, "Unregistered from the event!", Toast.LENGTH_SHORT).show();
        isRegistered = false;
        updateRegistrationButtons();
    }

    /**
     * Updates the visibility of the registration buttons based on registration status.
     */
    private void updateRegistrationButtons() {
        if (isRegistered) {
            registerButton.setVisibility(View.GONE);
            unregisterButton.setVisibility(View.VISIBLE);
        } else {
            registerButton.setVisibility(View.VISIBLE);
            unregisterButton.setVisibility(View.GONE);
        }
    }


    @Override
    public void returnResult(boolean result) {
        if(result){
            // Allow user to register
            handleRegister();
        }
    }

    @Override
    public void setLocation(double latitude, double longitude) {
        if(appUser != null) {
            appUser.setLocation(latitude, longitude);
            firebaseController.userUpdate(appUser);
        }
    }
}
