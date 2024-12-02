package com.example.soroban.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soroban.activity.EventDetailsActivity;
import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
/**
 * Allows the admin to browse events from Firebase, search for events using a search bar,
 * and view detailed information about a selected event.
 * It retrieves data from Firebase Firestore and displays
 * the list of events in a RecyclerView.
 * @author Kevin Li
 * @see AdminViewEventActivity
 * @see FireBaseController
 * @see Event
 * @see User
 * @see Facility
 */
public class AdminBrowseEventActivity extends AppCompatActivity {
    private User appUser;
    private RecyclerView eventRecycler;
    private SearchView eventSearch;
    private BrowseEventsAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Event> browseEventsList;
    private FireBaseController firebaseController;

    /**
     * Called when the activity is created. It
     * initializes the activity, retrieves user data
     * sets up Firestore data listener, and
     * configures the event list and search functionality
     *
     * @param savedInstanceState a Bundle containing saved instance data
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_events);

        // Get arguments passed from previous activity.
        // Reference: https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter
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

        CollectionReference eventRf = db.collection("events");
        firebaseController = new FireBaseController(this);
        eventSearch = findViewById(R.id.event_search_bar);
        eventRecycler = findViewById(R.id.event_admin_recycler);
        eventRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Customize SearchView text and hint color programmatically
        // Reference: Customize SearchView EditText color programmatically
        int searchEditTextId = eventSearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = eventSearch.findViewById(searchEditTextId);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.BLACK); // Set text color to black
            searchEditText.setHintTextColor(Color.GRAY); // Set hint color to gray
        }

        // Customize SearchView background
        eventSearch.setBackgroundResource(R.drawable.search_view_background);


        browseEventsList = new ArrayList<>();

        // Add snapshot listener for Firestore
        eventRf.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    browseEventsList.clear();
                    for (QueryDocumentSnapshot document : querySnapshots) {
                        Log.d("Firestore", "Found Events!");
                        Map<String, Object> eventData = document.getData();
                        String eventName = (String) eventData.get("eventName");
                        Date eventDate = document.getDate("eventDate");
                        Date drawDate = document.getDate("drawDate");
                        Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                        User owner = new User(((DocumentReference) document.get("owner")).getPath().replace("users/", ""));
                        firebaseController.fetchUserDoc(owner);
                        owner.createFacility();
                        DocumentReference facilityRef = (DocumentReference) document.get("facility");
                        Facility facility = owner.getFacility();
                        firebaseController.fetchFacilityDoc(owner, facilityRef);
                        Event event = new Event(owner, facility, eventName, eventDate, drawDate, sampleSize);
                        if (eventData.get("geoLocation") != null) {
                            event.setRequiresGeolocation((Boolean) eventData.get("geoLocation"));
                        }
                        if (eventData.get("maxEntrants") != null) {
                            Integer maxEntrants = ((Long) eventData.get("maxEntrants")).intValue();
                            event.setMaxEntrants(maxEntrants);
                        }
                        if (eventData.get("eventDetails") != null) {
                            event.setEventDetails((String) eventData.get("eventDetails"));
                        }
                        if (eventData.get("posterUrl") != null) {
                            event.setPosterUrl((String) eventData.get("posterUrl"));
                        }
                        if (eventData.get("geoLocation") != null) {
                            // do later
                        }
                        browseEventsList.add(event);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        adapter = new BrowseEventsAdapter(browseEventsList);
        eventRecycler.setAdapter(adapter);

        // On Searching with Search View
        // Reference: https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
        eventSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(AdminBrowseEventActivity.this, AdminDashboardActivity.class);
                Bundle newArgs = new Bundle();
                newArgs.putSerializable("appUser",appUser);
                intent.putExtras(newArgs);
                startActivity(intent);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * This is a sample adapter to display the events.
     */
    private class BrowseEventsAdapter extends RecyclerView.Adapter<BrowseEventsAdapter.ViewHolder> {
        private ArrayList<Event> browseEventsList;

        public BrowseEventsAdapter(ArrayList<Event> browseEventsList) {
            this.browseEventsList = browseEventsList;
        }

        /**
         * This method applies the filtered list from the SearchView into the adapter.
         * @param newList: new array list for search results
         */
        public void filterList(ArrayList<Event> newList) {
            browseEventsList = newList;
            notifyDataSetChanged();
        }

        /**
         * This method is called when a new ViewHolder is created.
         * @param parent: The ViewGroup into which the new View will be added after it is bound to
         *               an adapter position.
         * @param viewType: The view type of the new View.
         * @return the newly created ViewHolder
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            return new ViewHolder(view);
        }

        /**
         * This method is called by RecyclerView to display the data at the specified position.
         * @param holder The ViewHolder which should be updated to represent the contents of the
         *        item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(BrowseEventsAdapter.ViewHolder holder, int position) {
            Event selectedEvent = browseEventsList.get(position);
            String eventName = selectedEvent.getEventName();
            String eventDate = "No Date"; // Temporary, for events without dates
            if (selectedEvent.getEventDate() != null) {
                eventDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedEvent.getEventDate());
            }

            holder.eventNameTV.setText(eventName);
            holder.eventDateTV.setText(eventDate);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(AdminBrowseEventActivity.this, AdminViewEventActivity.class);
                Bundle newArgs = new Bundle();
                newArgs.putSerializable("selectedEvent", selectedEvent);
                newArgs.putSerializable("appUser", appUser);
                intent.putExtras(newArgs);
                startActivity(intent);
            });
        }

        /**
         * This method returns the total number of items in the data set held by the adapter.
         * @return the size of the event list
         */
        @Override
        public int getItemCount() {
            return browseEventsList.size();
        }

        /**
         * ViewHolder class to hold references to UI elements for each event item.
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView eventNameTV;
            TextView eventDateTV;

            ViewHolder(View itemView) {
                super(itemView);
                eventNameTV = itemView.findViewById(R.id.eventNameTextView);
                eventDateTV = itemView.findViewById(R.id.eventDateTextView);
            }
        }
    }

    /**
     * This method filters data based on the query.
     * Reference: https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
     * @param enteredText: text searched by query
     */
    private void filter(String enteredText) {
        ArrayList<Event> filteredList = new ArrayList<>();
        for (Event filteredEvent : browseEventsList) {
            if (filteredEvent.getEventName().toLowerCase().contains(enteredText.toLowerCase())) {
                filteredList.add(filteredEvent);
            }
        }

        adapter.filterList(filteredList);
    }
}
