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
import com.example.soroban.model.User;
import com.example.soroban.model.UserList;

/**
 * Custom ArrayAdapter for displaying a list of {@link User} objects.
 * This adapter inflates a custom layout to display user information.
 *
 * @author
 * @see User
 * @see UserList
 */
public class UserArrayAdapter extends ArrayAdapter<User> {
    private UserList users;
    private Context context;

    /**
     * Constructs a new {@code UserArrayAdapter}.
     *
     * @param context the context in which the adapter is being used.
     * @param objects the list of users to display.
     */
    public UserArrayAdapter(@NonNull Context context, @NonNull UserList objects) {
        super(context,0, objects);
        this.users = objects;
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
            view = LayoutInflater.from(context).inflate(R.layout.organizer_event_entrant_item, parent,false);
        }

        User user = users.get(position);

        TextView eventName = view.findViewById(R.id.userUsername);

        eventName.setText(user.getFirstName());

        return view;
    }
}
