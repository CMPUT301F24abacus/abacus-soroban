package com.example.soroban;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.soroban.FireBaseController;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.activity.EventEntrantsListActivity;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class OrganizerEventViewDetailsActivity extends AppCompatActivity {
    private Event selectedEvent;
    private User appUser;
    private ImageView viewQRcode;
    private ImageView eventGeolocation;
    private TextView eventTitle;
    private TextView eventDate;
    private TextView eventDescription;
    private ImageView eventPoster;
    private TextView eventSampleSize;
    private TextView eventEntrantLimit;
    private Button geoReq;
    private Button autoReplace;

    private Button viewEntrants;
    private Button editEvent;

    /**
     * Called when this activity is first created.
     * @param savedInstanceState
     * Author: Aaryan Shetty
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_view_event_details);

        Bundle args = getIntent().getExtras();

        // Initialize appUser and selected Event for this activity.
        if(args != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                selectedEvent = args.getSerializable("selectedEvent", Event.class);
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
                selectedEvent = (Event) args.getSerializable("selectedEvent");
            }

            if(appUser == null || selectedEvent == null){
                throw new IllegalArgumentException("Must pass object of type User and Event to initialize appUser.");
            }else{
                selectedEvent = appUser.getHostedEvents().find(selectedEvent);
            }

        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }


        // Assign button variables to views
        viewQRcode = findViewById(R.id.buttonScanQRCode);
        eventGeolocation = findViewById(R.id.buttonEventGeolocation);
        eventTitle = findViewById(R.id.eventNameTitle);
        eventDate = findViewById(R.id.eventDateTitle);
        eventDescription = findViewById(R.id.eventDescriptionText);
        eventPoster = findViewById(R.id.eventPosterImage);
        eventSampleSize = findViewById(R.id.eventSampleSizeText);
        geoReq = findViewById(R.id.eventGeoReqSwitch);
        autoReplace = findViewById(R.id.eventAutoReplaceSwitch);

        viewEntrants = findViewById(R.id.buttonViewEntrants);
        editEvent = findViewById(R.id.buttonEditEventDetails);


        // Populate Event Details
        eventTitle.setText(selectedEvent.getEventName());
        String formattedEventDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedEvent.getEventDate());
        eventDate.setText(formattedEventDate);
        eventSampleSize.setText(selectedEvent.getSampleSize().toString());

        // Set up listeners for applicable buttons
        viewQRcode.setOnClickListener(v -> {
            FireBaseController firebaseController = new FireBaseController(this);

            firebaseController.fetchQRCodeHash(selectedEvent.getEventName(), qrCodeHash -> {
                if (qrCodeHash != null) {
                    // Generate the QR code bitmap using the hash from firebase
                    Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(qrCodeHash);
                    if (qrCodeBitmap != null) {
                        // Inflate the custom dialog layout
                        View dialogView = getLayoutInflater().inflate(R.layout.activity_view_qr_code, null);
                        ImageView qrCodeImageView = dialogView.findViewById(R.id.qrCodeImageView);
                        qrCodeImageView.setImageBitmap(qrCodeBitmap); // Set the QR code bitmap
                        // Pop-up the QRCode Image
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setView(dialogView);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        // Dismiss QRCode Image when touching outside or pressing the back button
                        dialog.setCanceledOnTouchOutside(true);
                    } else {
                        Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "QR code not available", Toast.LENGTH_SHORT).show();
                }
            });
        });


        eventGeolocation.setOnClickListener(v -> {
            Toast.makeText(this, "WIP - Implement viewing where entrants are located", Toast.LENGTH_SHORT).show();
        });

        viewEntrants.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerEventViewDetailsActivity.this, EventEntrantsListActivity.class);
            Bundle newArgs = new Bundle();
            newArgs.putSerializable("selectedEvent", selectedEvent);
            newArgs.putSerializable("appUser", appUser);
            intent.putExtras(newArgs);
            startActivity(intent);
        });

        editEvent.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerEventViewDetailsActivity.this, OrganizerEventEditDetailsActivity.class);
            startActivity(intent);
        });
    }
}
