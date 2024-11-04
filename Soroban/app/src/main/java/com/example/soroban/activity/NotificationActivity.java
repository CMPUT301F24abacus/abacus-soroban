/**
 * Author: Ayan Chaudhry
 * References: ChatGPT, Stack Overflow, Geeks for Geeks
 * this class contains all the functionalities related to Notifications for the User
 */

package com.example.soroban.activity;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soroban.R;
import com.example.soroban.adapter.NotificationAdapter;
import com.example.soroban.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private Button muteAllButton;
    private Button clearAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recyclerView = findViewById(R.id.notifications_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        muteAllButton = findViewById(R.id.btn_mute_all);
        clearAllButton = findViewById(R.id.btn_clear_all);
        /**
         * Initialize the notification list with some sample notifications
         */
        // Initialize the notification list with some sample notifications
        notificationList = new ArrayList<>();
        notificationList.add(new Notification("Not selected in the waitlist", "09:00 AM", "Event A", Notification.NotificationType.BAD_NEWS));
        notificationList.add(new Notification("Spot confirmed", "10:30 AM", "Event B", Notification.NotificationType.SPOT_CONFIRMED));
        notificationList.add(new Notification("Registration open for Event C", "12:00 PM", "Event C", Notification.NotificationType.REGISTRATION_OPEN));
        /**
         * Set up the adapter and assign it to the RecyclerView
         */
        // Set up the adapter and assign it to the RecyclerView
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);
        /**
         * Set up click listeners for the mute all and clear all buttons
         */
        // Mute All button functionality
        muteAllButton.setOnClickListener(v -> {
            adapter.muteAllNotifications();
        });

        // Clear All button functionality
        clearAllButton.setOnClickListener(v -> {
            adapter.clearAllNotifications();
        });
    }
}
