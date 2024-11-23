package com.example.soroban.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;

import java.text.SimpleDateFormat;
import java.util.Locale;

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
            view = LayoutInflater.from(context).inflate(R.layout.item_confirmed_event, parent,false);
        }

        Event event = events.get(position);

        TextView eventName = view.findViewById(R.id.tv_event_name);
        TextView eventDate = view.findViewById(R.id.tv_event_date);

        eventName.setText(event.getEventName());
        String formattedEventDrawDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(event.getDrawDate());
        eventDate.setText(formattedEventDrawDate);

        return view;
    }
}
