package com.example.soroban;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.model.Event;
import com.example.soroban.model.User;

public class EventDetailsActivity extends AppCompatActivity {

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
    private boolean isRegistered; // Store the registration status

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Bundle args = getIntent().getExtras();

        if (args != null) {
            selectedEvent = (Event) args.getSerializable("eventData");
            if (selectedEvent == null) {
                throw new IllegalArgumentException("Event data is missing.");
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
            FireBaseController firebaseController = new FireBaseController(this);
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
        registerButton.setOnClickListener(v -> handleRegister());
        unregisterButton.setOnClickListener(v -> handleUnregister());
    }

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

    private void handleRegister() {
        // Implement Registration
        Toast.makeText(this, "Registered for the event!", Toast.LENGTH_SHORT).show();
        isRegistered = true;
        updateRegistrationButtons();
    }

    private void handleUnregister() {
        // Implement Unregistration
        Toast.makeText(this, "Unregistered from the event!", Toast.LENGTH_SHORT).show();
        isRegistered = false;
        updateRegistrationButtons();
    }

    private void updateRegistrationButtons() {
        if (isRegistered) {
            registerButton.setVisibility(View.GONE);
            unregisterButton.setVisibility(View.VISIBLE);
        } else {
            registerButton.setVisibility(View.VISIBLE);
            unregisterButton.setVisibility(View.GONE);
        }
    }

}
