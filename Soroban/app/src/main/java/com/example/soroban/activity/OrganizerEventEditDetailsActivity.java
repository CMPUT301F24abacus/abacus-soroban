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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
//import com.example.soroban.activity.CreateEventActivity;

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
    private ActivityResultLauncher<Intent> posterPickerLauncher;
    private String posterUrl = null;
    private Event selectedEvent;
    private boolean deletedFlag = false;
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedEvent = (Event) extras.getSerializable("selectedEvent");
            if (selectedEvent == null) {
                throw new IllegalArgumentException("Event details not passed to OrganizerEventEditDetailsActivity");
            }
        }
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


        posterPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri posterUri = result.getData().getData();
                        handlePosterUpload(posterUri); // Pass the URI to handle upload
                    } else {
                        Log.e("EditEventActivity", "Poster selection canceled or failed.");
                        Toast.makeText(this, "Failed to select a poster.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        eventPosterUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            posterPickerLauncher.launch(intent);
        });

        eventPosterDelete.setOnClickListener(v -> {
            deletedFlag = true;
            selectedEvent.setPosterUrl(null);
            eventPoster.setImageResource(R.drawable.ic_event_image);
        });

        buttonConfirm.setOnClickListener(v -> {
            confirmChanges();
        });


    }

    /**
     * Handles poster uploads by interacting with Firebase and updating the event object.
     *
     * @param posterUri the URI of the selected poster.
     */
    private void handlePosterUpload(Uri posterUri) {
        if (posterUri != null) {
            String eventId = selectedEvent.getEventName(); // Use unique identifier for the event

            FireBaseController fireBaseController = new FireBaseController(this);

            // Upload the poster to Firebase
            fireBaseController.uploadEventPoster(posterUri, eventId, uri -> {
                posterUrl = uri.toString(); // Update the posterUrl with the uploaded URL
                //selectedEvent.setPosterUrl(posterUrl); // Update the event object
                //fireBaseController.updateEventPoster(selectedEvent); // Update the event in Firebase

                // Update UI with the new poster
                Glide.with(this)
                        .load(posterUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(eventPoster);

                Toast.makeText(this, "Poster uploaded successfully!", Toast.LENGTH_SHORT).show();
            }, e -> {
                Log.e("EditEventActivity", "Failed to upload poster", e);
                Toast.makeText(this, "Failed to upload poster.", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("EditEventActivity", "Poster URI is null.");
            Toast.makeText(this, "Failed to get image URI.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the changes to the database and to the event.
     *
     */
    private void confirmChanges() {
        FireBaseController fireBaseController = new FireBaseController(this);
        Intent resultIntent = new Intent();
        if (posterUrl != null | deletedFlag) {
            fireBaseController.updateEventPoster(selectedEvent);
            selectedEvent.setPosterUrl(posterUrl);
            resultIntent.putExtra("updatedUrl", posterUrl);
        }
        setResult(RESULT_OK, resultIntent);
        //Intent intent = new Intent(OrganizerEventEditDetailsActivity.this, OrganizerEventViewDetailsActivity.class);

        // Pass the updated event back to the target activity
        //Bundle newArgs = new Bundle();
        //newArgs.putSerializable("selectedEvent", selectedEvent); // Pass the updated event object
        //intent.putExtras(newArgs);

        // Start the target activity
        Toast.makeText(this, "Event changes confirmed!", Toast.LENGTH_SHORT).show();
        Log.d("POSTER URL:", posterUrl);
        finish();

        // Optionally, display a confirmation message

    }

}
