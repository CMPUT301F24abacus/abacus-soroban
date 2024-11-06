package com.example.soroban;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventNameEditText;
    private EditText eventDateEditText;
    private Button saveEventButton;

    //QRCode
    private Button generateQrCodeButton;
    private TextView qrCodeLabel;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize views
        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        saveEventButton = findViewById(R.id.saveEventButton);

        // QRCode
        generateQrCodeButton = findViewById(R.id.generateQrCodeButton);
        qrCodeLabel = findViewById(R.id.qrCodeLabel);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        // Set up Save button click listener
        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }

        });

        // Set up Generate QR Code button click listener
        generateQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateAndDisplayQRCode();
            }
        });
    }

    private void saveEvent() {
        // Get the entered data
        String eventName = eventNameEditText.getText().toString().trim();
        String eventDate = eventDateEditText.getText().toString().trim();

        // Validate input
        if (eventName.isEmpty() || eventDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Event object (assuming you have an Event model class)
        OrganizerEvent newOrganizerEvent = new OrganizerEvent(eventName, eventDate);


        // Add the event to the list or database (you can adjust this part as needed)
        // For demonstration, we'll just pass the event data back to the previous activity

        Intent resultIntent = new Intent();
        resultIntent.putExtra("eventName", eventName);
        resultIntent.putExtra("eventDate", eventDate);
        setResult(RESULT_OK, resultIntent);
        finish(); // Close the activity and return to the previous screen
    }

    //QRCode
    private void generateAndDisplayQRCode() {
        String eventName = eventNameEditText.getText().toString().trim();
        String eventDate = eventDateEditText.getText().toString().trim();

        if (eventName.isEmpty() || eventDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields before generating QR code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique hash for the QR code content (using event details)
        String qrCodeHash = generateHash(eventName + eventDate);

        // Generate the QR code using the hash as content
        Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(qrCodeHash);

        if (qrCodeBitmap != null) {
            // Set the QR code bitmap to the ImageView and make it visible
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
            qrCodeImageView.setVisibility(View.VISIBLE);
            qrCodeLabel.setVisibility(View.VISIBLE);

            // Create and save the event with QR code hash
            OrganizerEvent newOrganizerEvent = new OrganizerEvent(eventName, eventDate);
            FireBaseController dbController = new FireBaseController();
            dbController.addEventWithQRCodeHash(qrCodeHash, newOrganizerEvent);

        } else {
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }
    private String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
