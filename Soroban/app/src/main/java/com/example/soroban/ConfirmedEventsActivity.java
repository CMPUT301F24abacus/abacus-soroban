package com.example.soroban;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ConfirmedEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerConfirmedEvents;
    private ConfirmedEventsAdapter adapter;

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

    private List<String> getConfirmedEvents() {
        // Sample data; replace with actual data source.
        List<String> events = new ArrayList<>();
        events.add("Event Name, Organized by LMN");
        events.add("Event Name, Organized by OPQ");
        events.add("Event Name, Organized by RST");
        return events;
    }

    private class ConfirmedEventsAdapter extends RecyclerView.Adapter<ConfirmedEventsAdapter.ViewHolder> {
        private List<String> confirmedEvents;

        public ConfirmedEventsAdapter(List<String> confirmedEvents) {
            this.confirmedEvents = confirmedEvents;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_confirmed_event, parent, false);
            return new ViewHolder(view);
        }

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
