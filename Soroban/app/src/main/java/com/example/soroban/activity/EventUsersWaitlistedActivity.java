package com.example.soroban.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.adapter.UserArrayAdapter;
import com.example.soroban.fragment.SendMessageFragment;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.User;
import com.example.soroban.model.UserList;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Manages the list of users on the waitlist for a specific event.
 * Event organizers can view the waitlist and send messages to waitlisted users.
 *
 * @author Aaryan Shetty
 * @author Matthieu Larochelle
 * @see Event
 * @see User
 * @see FireBaseController
 * @see UserArrayAdapter
 * @see SendMessageFragment
 */
public class EventUsersWaitlistedActivity extends AppCompatActivity {
    private User appUser;
    private Event selectedEvent;
    private FireBaseController fireBaseController;
    private FirebaseFirestore db;
    private ListView listView;
    private UserList listData;
    private UserArrayAdapter listAdapter;
    private Button sendMessage;
    private Button rejectUsers;
    private Button inviteUsers;

    /**
     * Called when this activity is first created.
     *
     * @param savedInstanceState the saved state of the activity
     * @throws IllegalArgumentException if required arguments (e.g., appUser or selectedEvent) are missing.
     * @author Aaryan Shetty
     * @author Matthieu Larochelle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_entrants_waitlist);

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

        fireBaseController = new FireBaseController(this);
        db = FirebaseFirestore.getInstance();

        // Grab event data from appUser
        listData = selectedEvent.getWaitingEntrants();

        // Reference to list view
        listView = findViewById(R.id.usersWaitlistView);

        // Setup adapter for list view
        listAdapter = new UserArrayAdapter(this,listData);
        listView.setAdapter(listAdapter);

        // Assign button variables to views
        sendMessage = findViewById(R.id.buttonSendMessageToUsers);

        // Set up reactions for when the buttons are clicked
        sendMessage.setOnClickListener(v -> {
            SendMessageFragment fragment = SendMessageFragment.newInstance(selectedEvent, appUser, listData, "waitList");
            fragment.show(getSupportFragmentManager(), "Send Message");
        });


        // Add snapshot listener for Firestore
        db.collection("events").document(selectedEvent.getEventName() + ", " + appUser.getDeviceId()).collection("waitList").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    listData.clear();
                    for (QueryDocumentSnapshot document : querySnapshots) {
                        Log.d("Firestore", "Found WaitList Users!");
                        Map<String, Object> userData = document.getData();
                        String deviceId = (String) userData.get("deviceId");
                        User newUser = new User(deviceId);
                        newUser.setFirstName((String) userData.get("firstName"));
                        newUser.setLastName((String) userData.get("lastName"));

                        listData.addUser(newUser);
                    }
                    listAdapter.notifyDataSetChanged();
                }
            }
        });


    }
}
