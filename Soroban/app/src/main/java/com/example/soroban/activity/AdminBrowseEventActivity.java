package com.example.soroban.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AdminBrowseEventActivity extends AppCompatActivity {

    private RecyclerView eventRecycler;
    private BrowseEventsAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Event> browseEventList;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_browse_events);

        CollectionReference eventRf = db.collection("events");
        eventRecycler = findViewById(R.id.event_admin_recycler);
        eventRecycler.setLayoutManager(new LinearLayoutManager(this));


        browseEventList = new ArrayList<>();

        eventRf.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    browseEventList.clear();
                    for (QueryDocumentSnapshot document: querySnapshots) {
                        Log.d("Firestore", "Found Events!");
                        Map<String, Object> eventData = document.getData();
                        String eventName = (String) eventData.get("eventName");
                        Date eventDate = document.getDate("eventDate");
                        Date drawDate = document.getDate("drawDate");
                        Integer sampleSize = ((Long) eventData.get("sampleSize")).intValue();
                        User owner = new User("temp"); // temporary for now
                        owner.createFacility();
                        //fetchUserDoc(owner);
                        Facility facility = owner.getFacility();
                        Event event = new Event(owner, facility, eventName, eventDate, drawDate,sampleSize);
                        if (eventData.get("maxEntrants") != null) {
                            Integer maxEntrants = ((Long) eventData.get("maxEntrants")).intValue();
                            event.setMaxEntrants(maxEntrants);
                        }
                        browseEventList.add(event);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


        adapter = new BrowseEventsAdapter(browseEventList);
        eventRecycler.setAdapter(adapter);

    }


    private class BrowseEventsAdapter extends RecyclerView.Adapter<BrowseEventsAdapter.ViewHolder> {
        private ArrayList<Event> browseEventsList;

        public BrowseEventsAdapter(ArrayList<Event> browseEventsList) {
            this.browseEventsList = browseEventsList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BrowseEventsAdapter.ViewHolder holder, int position) {
            String eventName = browseEventsList.get(position).getEventName();
            String eventDate = browseEventsList.get(position).getEventDate().toString();
            holder.eventNameTV.setText(eventName);
            holder.eventDateTV.setText(eventDate);
            holder.itemView.setOnClickListener(v -> {
                //Intent intent = new Intent(AdminBrowseEventActivity.this, EventRegistrationActivity.class);
                //intent.putExtra("isRegistered", true);
                //startActivity(intent);
            });
        }

        /**
         * This method returns the total number of items in the data set held by the adapter.
         * @return
         */
        @Override
        public int getItemCount() {
            return browseEventsList.size();
        }

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

}
