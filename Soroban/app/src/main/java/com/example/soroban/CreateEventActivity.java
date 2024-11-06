package com.example.soroban;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.soroban.activity.DatePickerListener;
import com.example.soroban.fragment.DatePickerFragment;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity implements DatePickerListener {

    private EditText eventNameEditText;
    private EditText sampleSizeEditText;
    private Button eventDateSelectButton;
    private Button drawDateSelectButton;
    private Button saveEventButton;
    private Date eventDate;
    private Date drawDate;
    private User appUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Get arguments passed from previous activity.
        // Reference: https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter
        Bundle args = getIntent().getExtras();

        // Initialize appUser for this activity.
        if(args != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
            }

            if(appUser == null ){
                throw new IllegalArgumentException("Must pass object of type User to initialize appUser.");
            }


        }else{
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

        // Set up Save button click listener
        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
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
        Facility userFacility =  appUser.getFacility();

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
        }else{
            eventSampleSize = Integer.parseInt(sampleSizeEditText.getText().toString());
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

        // Add the event to the list or database (you can adjust this part as needed)
        // For demonstration, we'll just pass the event data back to the previous activity

        Intent resultIntent = new Intent();
        resultIntent.putExtra("eventName", eventName);
        resultIntent.putExtra("eventDate", eventDate);
        setResult(RESULT_OK, resultIntent);
        finish(); // Close the activity and return to the previous screen
    }

    @Override
    public void setDate(Date targetDate,Date givenDate) {
        if(eventDate.equals(targetDate)){
            eventDate = givenDate;
        }else{
            drawDate = givenDate;
        }
    }
}
