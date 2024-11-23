package com.example.soroban.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.controller.UserController;
import com.example.soroban.fragment.DatePickerFragment;
import com.example.soroban.fragment.DatePickerListener;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity implements DatePickerListener {

    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private EditText sampleSizeEditText;
    private Button eventDateSelectButton;
    private ImageButton eventPosterUploadButton;
    private Button drawDateSelectButton;
    private Button saveEventButton;
    private TextView selectedEventDateTextView;
    private TextView selectedDrawDateTextView;
    private ActivityResultLauncher<Intent> posterPickerLauncher;

    private static final int PICK_POSTER_REQUEST = 1001;

    private Event newOrganizerEvent;
    private Date eventDate;
    private Date drawDate;
    private User appUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize the launcher for picking an image
        posterPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri posterUri = result.getData().getData();
                        handlePosterUpload(posterUri);
                    } else {
                        Log.e("CreateEventActivity", "Poster selection canceled or failed.");
                        Toast.makeText(this, "Failed to select a poster.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Get arguments passed from the previous activity
        Bundle args = getIntent().getExtras();
        if (args != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appUser = args.getSerializable("appUser", User.class);
            } else {
                appUser = (User) args.getSerializable("appUser");
            }

            if (appUser == null) {
                throw new IllegalArgumentException("Must pass a User object to initialize appUser.");
            }
        } else {
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        // Initialize event dates
        eventDate = Calendar.getInstance().getTime();
        drawDate = Calendar.getInstance().getTime();

        // Initialize views
        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventDescriptionEditText = findViewById(R.id.eventDescriptionEditText);
        sampleSizeEditText = findViewById(R.id.sampleSizeEditText);
        eventPosterUploadButton = findViewById(R.id.buttonUploadPoster);
        eventDateSelectButton = findViewById(R.id.eventDateSelectButton);
        drawDateSelectButton = findViewById(R.id.drawDateSelectButton);
        saveEventButton = findViewById(R.id.saveEventButton);
        selectedEventDateTextView = findViewById(R.id.event_date_view);
        selectedDrawDateTextView = findViewById(R.id.draw_date_view);


        // Set up date pickers
        eventDateSelectButton.setOnClickListener(view -> {
            DatePickerFragment dialogFragment = new DatePickerFragment();
            dialogFragment.setTargetDate(eventDate);
            dialogFragment.setListener(this);
            dialogFragment.show(getSupportFragmentManager(), "Select event date");
        });

        drawDateSelectButton.setOnClickListener(view -> {
            DatePickerFragment dialogFragment = new DatePickerFragment();
            dialogFragment.setTargetDate(drawDate);
            dialogFragment.setListener(this);
            dialogFragment.show(getSupportFragmentManager(), "Select draw date");
        });

        // Enable poster upload button only after event is saved
        saveEventButton.setOnClickListener(v -> {
            saveEvent();
        });

        eventPosterUploadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            posterPickerLauncher.launch(intent);
        });



        // Initialize the launcher for picking an image
        posterPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri posterUri = result.getData().getData();
                        handlePosterUpload(posterUri);
                    } else {
                        Log.e("CreateEventActivity", "Poster selection canceled or failed.");
                        Toast.makeText(this, "Failed to select a poster.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Update the date views with initial values
        updateDateTextViews();
    }

    private void saveEvent() {
        // Get input data
        String eventName = eventNameEditText.getText().toString().trim();
        String eventDetails = eventDescriptionEditText.getText().toString().trim();
        int eventSampleSize;

        // Validate input
        if (eventName.isEmpty()) {
            Toast.makeText(this, "Please enter a name for your event.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventDate.compareTo(drawDate) <= 0) {
            Toast.makeText(this, "The event date must be after the draw date.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sampleSizeEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a sample size for your event.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            eventSampleSize = Integer.parseInt(sampleSizeEditText.getText().toString().trim());
        }

        if (eventSampleSize <= 0) {
            Toast.makeText(this, "Sample size must be greater than zero.", Toast.LENGTH_SHORT).show();
            return;
        }

        Facility userFacility = appUser.getFacility();
        if (userFacility == null) {
            Toast.makeText(this, "You must have a facility to create events.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the event
        newOrganizerEvent = new Event(appUser, userFacility, eventName, eventDate, drawDate, eventSampleSize);
        newOrganizerEvent.setEventDetails(eventDetails);
        newOrganizerEvent.setQrCodeHash(generateHash());

        // Save the event to the user's hosted events and Firebase
        UserController userController = new UserController(appUser);
        FireBaseController fireBaseController = new FireBaseController(this);
        userController.addHostedEvent(newOrganizerEvent);
        fireBaseController.createEventDb(newOrganizerEvent);

        Intent intent = new Intent(CreateEventActivity.this, OrganizerDashboardActivity.class);
        Bundle newArgs = new Bundle();
        newArgs.putSerializable("appUser", appUser);
        intent.putExtras(newArgs);
        startActivity(intent);

        Toast.makeText(this, "Event saved successfully.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_POSTER_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri posterUri = data.getData();

            if (posterUri != null) {
                FireBaseController fireBaseController = new FireBaseController(this);
                fireBaseController.uploadEventPoster(posterUri, newOrganizerEvent.getEventName(), uri -> {
                    String posterUrl = uri.toString();
                    newOrganizerEvent.setPosterUrl(posterUrl);
                    fireBaseController.updateEventPoster(newOrganizerEvent);

                    Toast.makeText(this, "Poster uploaded successfully!", Toast.LENGTH_SHORT).show();
                }, e -> {
                    Log.e("CreateEventActivity", "Failed to upload poster", e);
                    Toast.makeText(this, "Failed to upload poster.", Toast.LENGTH_SHORT).show();
                });
            } else {
                Log.e("CreateEventActivity", "Poster URI is null.");
                Toast.makeText(this, "Failed to get image URI.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateDateTextViews() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedEventDateTextView.setText(dateFormat.format(eventDate));
        selectedDrawDateTextView.setText(dateFormat.format(drawDate));
    }

    private String generateHash() {
        return java.util.UUID.randomUUID().toString();
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

    // Handle the poster upload
    private void handlePosterUpload(Uri posterUri) {
        if (posterUri != null) {
            String tempEventName = eventNameEditText.getText().toString().trim();

            // Ensure that the event name is provided
            if (tempEventName.isEmpty()) {
                Toast.makeText(this, "Please enter an event name before uploading a poster.", Toast.LENGTH_SHORT).show();
                return;
            }

            FireBaseController fireBaseController = new FireBaseController(this);

            // Upload the poster to Firebase
            fireBaseController.uploadEventPoster(posterUri, tempEventName, uri -> {
                String posterUrl = uri.toString();
                if (newOrganizerEvent != null) {
                    newOrganizerEvent.setPosterUrl(posterUrl);
                    fireBaseController.updateEventPoster(newOrganizerEvent);
                }
                Toast.makeText(this, "Poster uploaded successfully!", Toast.LENGTH_SHORT).show();
            }, e -> {
                Log.e("CreateEventActivity", "Failed to upload poster", e);
                Toast.makeText(this, "Failed to upload poster.", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("CreateEventActivity", "Poster URI is null.");
            Toast.makeText(this, "Failed to get image URI.", Toast.LENGTH_SHORT).show();
        }
    }

}
