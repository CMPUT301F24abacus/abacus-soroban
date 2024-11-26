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

/**
 * Custom ArrayAdapter for displaying a list of {@link Event} objects.
 * This adapter is responsible for inflating a custom layout for each event in the list.
 *
 * @author
 * @see Event
 * @see EventList
 */

/**
 * Constructs a new EventArrayAdapter
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private EventList events;
    private Context context;


    public EventArrayAdapter(@NonNull Context context, @NonNull EventList objects) {
        super(context,0, objects);
        this.events = objects;
        this.context = context;
    }

    /**
     * Returns a view for a specific item in the list.
     *
     * @param position the position of the item in the list.
     * @param convertView the old view to reuse, if possible.
     * @param parent the parent view to attach this view to.
     * @return a view corresponding to the specified position in the list.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_confirmed_event, parent,false);
        }

        Event event = events.get(position);

        TextView eventName = view.findViewById(R.id.tv_event_name);

        eventName.setText(event.getEventName());

        return view;
    }
}
