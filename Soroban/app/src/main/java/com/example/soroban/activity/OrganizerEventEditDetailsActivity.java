/**
 * Allows editing event details for organizers.
 * Such as event title, date, description, poster, sample size, and other event-related settings.
 *
 * Author: Aaryan Shetty
 * @see Event
 * @see User
 * @see FireBaseController
 */
package com.example.soroban.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.R;

public class OrganizerEventEditDetailsActivity extends AppCompatActivity {
    private EditText eventTitle;
    private EditText eventDate;
    private EditText eventDescription;
    private ImageView eventPoster;
    private ImageView eventPosterUpload;
    private ImageView eventPosterDelete;
    private EditText eventSampleSize;
    private EditText eventDrawDate;
    private Button geoReq;
    private Button autoReplace;

    private Button buttonConfirm;
    private Button buttonCancel;

    /**
     * Called when this activity is first created. Initializes UI components and sets up listeners.
     * @param savedInstanceState the saved instance state of the activity.
     * Author: Aaryan Shetty
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_edit_event_details);

        // Assign button variables to views
        eventTitle = findViewById(R.id.eventEditName);
        eventDate = findViewById(R.id.eventEditDate);
        eventDescription = findViewById(R.id.eventEditDescription);
        eventPoster = findViewById(R.id.eventPosterImage);
        eventPosterUpload = findViewById(R.id.eventPosterUpload);
        eventPosterDelete = findViewById(R.id.eventPosterDelete);
        eventSampleSize = findViewById(R.id.eventEditSampleSize);
        geoReq = findViewById(R.id.eventGeoReqSwitch);
        autoReplace = findViewById(R.id.eventAutoReplaceSwitch);

        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Set up listeners for applicable buttons
        eventPosterUpload.setOnClickListener(v -> {
            Toast.makeText(this, "WIP - Implement upload image prompt", Toast.LENGTH_SHORT).show();
        });

        eventPosterDelete.setOnClickListener(v -> {
            Toast.makeText(this, "WIP - Implement confirm delete image prompt", Toast.LENGTH_SHORT).show();
        });
    }
}
