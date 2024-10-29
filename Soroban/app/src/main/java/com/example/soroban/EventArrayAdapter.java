package com.example.soroban;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EventArrayAdapter extends ArrayAdapter<Event> {
    private EventList events;
    private Context context;


    public EventArrayAdapter(@NonNull Context context, @NonNull EventList objects) {
        super(context,0, objects);
        this.events = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.eventlist_content, parent,false);
        }

        Event event = events.get(position);

        TextView eventName = view.findViewById(R.id.event_list_name);

        eventName.setText(event.getEventName());

        return view;
    }
}
