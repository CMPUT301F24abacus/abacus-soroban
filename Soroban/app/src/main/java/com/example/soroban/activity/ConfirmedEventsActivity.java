/**
 * This screen will display the events in which the User has already registered and secured their spot
 * Author: Ayan Imran Chaudhry
 * References: ChatGPT, Stack Overflow, Android Documentation
 */

package com.example.soroban.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soroban.R;

import java.util.ArrayList;
import java.util.List;

public class ConfirmedEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerConfirmedEvents;
    private ConfirmedEventsAdapter adapter;

    /**
     * This method is called when the activity is created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_events);

        recyclerConfirmedEvents = findViewById(R.id.recycler_confirmed_events);
        recyclerConfirmedEvents.setLayoutManager(new LinearLayoutManager(this));

        List<String> confirmedEventsList = getConfirmedEvents();
        adapter = new ConfirmedEventsAdapter(confirmedEventsList);
        recyclerConfirmedEvents.setAdapter(adapter);
    }

    /**
     * This is a sample method to get the confirmed events.
     */
    private List<String> getConfirmedEvents() {
        // Sample data; replace with actual data source.
        List<String> events = new ArrayList<>();
        events.add("Event Name, Organized by LMN");
        events.add("Event Name, Organized by OPQ");
        events.add("Event Name, Organized by RST");
        return events;
    }

    /**
     * This is a sample adapter to display the confirmed events.
     */
    private class ConfirmedEventsAdapter extends RecyclerView.Adapter<ConfirmedEventsAdapter.ViewHolder> {
        private List<String> confirmedEvents;

        public ConfirmedEventsAdapter(List<String> confirmedEvents) {
            this.confirmedEvents = confirmedEvents;
        }

        /**
         * This method is called when a new ViewHolder is created.
         * @param parent The ViewGroup into which the new View will be added after it is bound to
         *               an adapter position.
         * @param viewType The view type of the new View.
         *
         * @return
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_confirmed_event, parent, false);
            return new ViewHolder(view);
        }

        /**
         * This method is called by RecyclerView to display the data at the specified position.
         * @param holder The ViewHolder which should be updated to represent the contents of the
         *        item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String eventName = confirmedEvents.get(position);
            holder.tvEventName.setText(eventName);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ConfirmedEventsActivity.this, EventRegistrationActivity.class);
                intent.putExtra("isRegistered", true); // Pass true if the user is registered for the event
                startActivity(intent);
            });
        }

        /**
         * This method returns the total number of items in the data set held by the adapter.
         * @return
         */
        @Override
        public int getItemCount() {
            return confirmedEvents.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvEventName;

            ViewHolder(View itemView) {
                super(itemView);
                tvEventName = itemView.findViewById(R.id.tv_event_name);
            }
        }
    }
}
