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

public class UserArrayAdapter extends ArrayAdapter<User> {
    private UserList users;
    private Context context;


    public UserArrayAdapter(@NonNull Context context, @NonNull UserList objects) {
        super(context,0, objects);
        this.users = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.user_item, parent,false);
        }

        User user = users.get(position);

        TextView eventName = view.findViewById(R.id.tv_user_name);

        eventName.setText(user.getFirstName());

        return view;
    }
}
