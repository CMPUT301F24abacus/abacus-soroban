package com.example.soroban.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soroban.R;
import com.example.soroban.model.Notification;

import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of {@link Notification} objects.
 * This adapter binds each notification to a custom layout and handles user interactions.
 *
 * @author
 * @see Notification
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;

    /**
     * Constructs a new {@code NotificationAdapter}.
     *
     * @param notificationList the list of notifications to display.
     */
    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    /**
     * Creates a new ViewHolder for the RecyclerView.
     *
     * @param parent the parent view group.
     * @param viewType the view type of the new View.
     * @return a new {@code NotificationViewHolder}.
     */
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    /**
     * Binds data from a {@link Notification} to a ViewHolder.
     *
     * @param holder the ViewHolder to bind data to.
     * @param position the position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.titleTextView.setText(notification.getTitle());
        /*holder.timeTextView.setText(notification.getTime());
        holder.eventTextView.setText(notification.getEvent());

        // Set background color based on notification type
        switch (notification.getType()) {
            case BAD_NEWS:
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_bad_news));
                break;
            case SPOT_CONFIRMED:
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_spot_confirmed));
                break;
            case REGISTRATION_OPEN:
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_registration_open));
                break;
        }

         Set the icon based on the muted state
        if (notification.isMuted()) {
            holder.muteIcon.setImageResource(R.drawable.ic_mute);
        } else {
            holder.muteIcon.setImageResource(R.drawable.ic_sound);
        }

        // Handle mute/unmute toggle
        holder.muteIcon.setOnClickListener(v -> {
            notification.setMuted(!notification.isMuted());
            holder.muteIcon.setImageResource(notification.isMuted() ? R.drawable.ic_mute : R.drawable.ic_sound);
            notifyItemChanged(position);
        });

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            // Remove the notification and refresh the list
            notificationList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notificationList.size()); // Notify range change to update the list
        });*/
    }

    /**
     * Returns the total number of items in the list.
     *
     * @return the size of the notification list.
     */
    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    // Method to mute all notifications
    public void muteAllNotifications() {
        for (Notification notification : notificationList) {
            //notification.setMuted(true);
        }
        notifyDataSetChanged();
    }

    // Method to clear all notifications
    public void clearAllNotifications() {
        notificationList.clear();
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for displaying a single {@link Notification}.
     */
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, timeTextView, eventTextView;
        ImageView muteIcon;
        Button deleteButton;

        /**
         * Constructs a new {@code NotificationViewHolder}.
         *
         * @param itemView the view representing a single notification item.
         */
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.notification_title);
            timeTextView = itemView.findViewById(R.id.notification_time);
            eventTextView = itemView.findViewById(R.id.notification_event);
            muteIcon = itemView.findViewById(R.id.mute_icon);
            deleteButton = itemView.findViewById(R.id.btn_delete); // Initialize the delete button
        }
    }
}
