package com.example.soroban.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.controller.UserController;
import com.example.soroban.fragment.DatePickerFragment;
import com.example.soroban.fragment.DatePickerListener;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreateEventActivity extends AppCompatActivity implements DatePickerListener {

    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private EditText sampleSizeEditText;
    private Button eventDateSelectButton;
    private ImageButton eventPosterUploadButton;
    private Button drawDateSelectButton;
    private Button geoReqButton;
    private Button autoReplaceButton;
    private Button saveEventButton;
    private TextView selectedEventDateTextView;
    private TextView selectedDrawDateTextView;

    private Date eventDate;
    private Date drawDate;
    private User appUser;

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
        eventDescriptionEditText = findViewById(R.id.eventDescriptionEditText);
        sampleSizeEditText = findViewById(R.id.sampleSizeEditText);
        eventPosterUploadButton = findViewById(R.id.buttonUploadPoster);
        eventDateSelectButton = findViewById(R.id.eventDateSelectButton);
        drawDateSelectButton = findViewById(R.id.drawDateSelectButton);
        geoReqButton = findViewById(R.id.eventGeoReqSwitch);
        autoReplaceButton = findViewById(R.id.eventAutoReplaceSwitch);
        saveEventButton = findViewById(R.id.saveEventButton);
        selectedEventDateTextView = findViewById(R.id.event_date_view);
        selectedDrawDateTextView = findViewById(R.id.draw_date_view);

        eventPosterUploadButton.setOnClickListener(v -> {
            Toast.makeText(this, "WIP - Implement upload image prompt", Toast.LENGTH_SHORT).show();
        });

        // Set up Save button click listener
        saveEventButton.setOnClickListener(v -> saveEvent());

        // Set up event date select and draw date select button listeners
        DatePickerListener listener = this;

        eventDateSelectButton.setOnClickListener(view -> {
            DatePickerFragment dialogFragment = new DatePickerFragment();
            dialogFragment.setTargetDate(eventDate);
            dialogFragment.setListener(listener);
            dialogFragment.show(getSupportFragmentManager(), "Select event date");
        });

        drawDateSelectButton.setOnClickListener(view -> {
            DatePickerFragment dialogFragment = new DatePickerFragment();
            dialogFragment.setTargetDate(drawDate);
            dialogFragment.setListener(listener);
            dialogFragment.show(getSupportFragmentManager(), "Select draw date");
        });

        // Display initial dates
        updateDateTextViews();
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

        // Generate QR code hash
        String formattedEventDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(eventDate);
        String uniqueIdentifier = eventName + formattedEventDate + appUser.getDeviceId();
        String qrCodeHash = generateHash(uniqueIdentifier);

        // Create a new Event object and set QR code hash
        Event newOrganizerEvent = new Event(appUser, userFacility, eventName, eventDate, drawDate, eventSampleSize);
        newOrganizerEvent.setQrCodeHash(qrCodeHash);

        // Add the event to the list and database
        UserController userController = new UserController(appUser);
        FireBaseController fireBaseController = new FireBaseController(this);
        userController.addHostedEvent(newOrganizerEvent);
        fireBaseController.createEventDb(newOrganizerEvent);
        fireBaseController.updateUserHosted(appUser, newOrganizerEvent);

        Intent intent = new Intent(CreateEventActivity.this, OrganizerDashboardActivity.class);
        Bundle newArgs = new Bundle();
        newArgs.putSerializable("appUser", appUser);
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
        updateDateTextViews();
    }

    private void updateDateTextViews() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedEventDateTextView.setText(String.format("Event Date: %s", dateFormat.format(eventDate)));
        selectedDrawDateTextView.setText(String.format("Draw Date: %s", dateFormat.format(drawDate)));
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



