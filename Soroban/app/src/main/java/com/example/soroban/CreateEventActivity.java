package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


public class CreateEventActivity extends AppCompatActivity {

    private EditText eventNameEditText;
    private EditText eventDateEditText;
    private Button saveEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize views
        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        saveEventButton = findViewById(R.id.saveEventButton);

        // Set up Save button click listener
        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
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
}
