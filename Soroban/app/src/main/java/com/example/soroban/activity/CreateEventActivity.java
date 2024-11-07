package com.example.soroban.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.QRCodeGenerator;
import com.example.soroban.R;
import com.example.soroban.controller.UserController;
import com.example.soroban.fragment.DatePickerFragment;
import com.example.soroban.fragment.DatePickerListener;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreateEventActivity extends AppCompatActivity implements DatePickerListener {

    private EditText eventNameEditText;
    private EditText sampleSizeEditText;
    private Button eventDateSelectButton;
    private Button drawDateSelectButton;
    private Button saveEventButton;
    private Date eventDate;
    private Date drawDate;
    private User appUser;

    // QRCode
    private Button generateQrCodeButton;
    private TextView qrCodeLabel;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Get arguments passed from previous activity.
        Bundle args = getIntent().getExtras();

        // Initialize appUser for this activity.
        if (args != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appUser = args.getSerializable("appUser", User.class);
            } else {
                appUser = (User) args.getSerializable("appUser");
            }

            if (appUser == null) {
                throw new IllegalArgumentException("Must pass object of type User to initialize appUser.");
            }
        } else {
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        // Initialize dates to be current day
        eventDate = Calendar.getInstance().getTime();
        drawDate = Calendar.getInstance().getTime();

        // Initialize views
        eventNameEditText = findViewById(R.id.eventNameEditText);
        sampleSizeEditText = findViewById(R.id.sampleSizeEditText);
        eventDateSelectButton = findViewById(R.id.eventDateSelectButton);
        drawDateSelectButton = findViewById(R.id.drawDateSelectButton);
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

        // Set up event date select and draw date select button listeners
        DatePickerListener listener = this;

        eventDateSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment dialogFragment = new DatePickerFragment();
                dialogFragment.setTargetDate(eventDate);
                dialogFragment.setListener(listener);
                dialogFragment.show(getSupportFragmentManager(), "Select event date");
            }
        });

        drawDateSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment dialogFragment = new DatePickerFragment();
                dialogFragment.setTargetDate(drawDate);
                dialogFragment.setListener(listener);
                dialogFragment.show(getSupportFragmentManager(), "Select draw date");
            }
        });
    }

    private void saveEvent() {
        // Get the entered data
        String eventName = eventNameEditText.getText().toString().trim();
        int eventSampleSize;
        Facility userFacility = appUser.getFacility();

        // Validate input
        if (eventName.isEmpty()) {
            Toast.makeText(this, "Please enter a name for your event.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventDate.compareTo(drawDate) <= 0) {
            Toast.makeText(this, "Please enter an event date that is after the draw date.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventDate.compareTo(Calendar.getInstance().getTime()) <= 0) {
            Toast.makeText(this, "Please enter an event date that is after the current date.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (drawDate.compareTo(Calendar.getInstance().getTime()) <= 0) {
            Toast.makeText(this, "Please enter a draw date that is after the current date.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sampleSizeEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a sample size for your event.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            eventSampleSize = Integer.parseInt(sampleSizeEditText.getText().toString().trim());
        }

        if (eventSampleSize <= 0) {
            Toast.makeText(this, "Please enter a valid sample size for your event.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure user has facility
        if (userFacility == null) {
            Toast.makeText(this, "You must have a facility to create events.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Event object
        Event newOrganizerEvent = new Event(appUser, userFacility, eventName, eventDate, drawDate, eventSampleSize);

        // Add the event to the list and database
        UserController userController = new UserController(appUser);
        FireBaseController fireBaseController = new FireBaseController(this);
        userController.addHostedEvent(newOrganizerEvent);
        fireBaseController.createEventDb(newOrganizerEvent);
        fireBaseController.updateUserHosted(appUser, newOrganizerEvent);

        Intent intent = new Intent(CreateEventActivity.this, OrganizerDashboardActivity.class);
        Bundle newArgs = new Bundle();
        newArgs.putSerializable("appUser",appUser);
        intent.putExtras(newArgs);
        startActivity(intent);
    }

    @Override
    public void setDate(Date targetDate, Date givenDate) {
        if (eventDate.equals(targetDate)) {
            eventDate = givenDate;
        } else {
            drawDate = givenDate;
        }
    }

    // QRCode
    private void generateAndDisplayQRCode() {
        String eventName = eventNameEditText.getText().toString().trim();
        String formattedEventDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(eventDate);

        if (eventName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields before generating QR code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique hash for the QR code content
        String qrCodeHash = generateHash(eventName + formattedEventDate);

        // Generate the QR code using the hash as content
        Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(qrCodeHash);
        DateFormat dateFormat = new SimpleDateFormat();

        // Generate QR Code using QRCodeGenerator utility class

        if (qrCodeBitmap != null) {
            // Set the QR code bitmap to the ImageView and make it visible
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
            qrCodeImageView.setVisibility(View.VISIBLE);
            qrCodeLabel.setVisibility(View.VISIBLE);

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
