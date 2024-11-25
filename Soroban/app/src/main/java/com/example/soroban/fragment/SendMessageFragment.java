package com.example.soroban.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.Notification;
import com.example.soroban.model.User;
import com.example.soroban.model.UserList;

import java.util.Calendar;
import java.util.Objects;


public class SendMessageFragment extends DialogFragment {
    private Event selectedEvent;
    private User appUser;
    private UserList currentList;
    private FireBaseController firebaseController;
    private DialogFragmentListener listener;
    String listType;

    public static SendMessageFragment newInstance(Event selectedEvent, User appUser, String listType) {

        Bundle args = new Bundle();
        args.putSerializable("selectedEvent", selectedEvent);
        args.putSerializable("appUser", appUser);
        args.putSerializable("listType", listType);

        SendMessageFragment fragment = new SendMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = getLayoutInflater().inflate(R.layout.organizer_event_send_message, null);

        Bundle args = getArguments();

        // Initialize appUser and selected Event for this activity.
        if(args != null){
            listType = args.getString("listType");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                selectedEvent = args.getSerializable("selectedEvent", Event.class);
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
                selectedEvent = (Event) args.getSerializable("selectedEvent");
            }

            if(appUser == null || selectedEvent == null){
                throw new IllegalArgumentException("Must pass object of type User and Event to initialize appUser.");
            }

            if(Objects.equals(listType, "waitList")){
                currentList = selectedEvent.getWaitingEntrants();
            }else if(Objects.equals(listType, "attendees")){
                currentList = selectedEvent.getAttendees();
            }else if(Objects.equals(listType, "cancelled")){
                currentList = selectedEvent.getNotGoing();
            }else if(Objects.equals(listType, "invited")){
                currentList = selectedEvent.getInvitedEntrants();
            }

        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        firebaseController = new FireBaseController(getContext());

        // Reference to input text
        EditText messageEdit = view.findViewById(R.id.organizerMessageEdit);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Send message to selected entrants?");
        builder.setView(view);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Send", (dialog, which) -> {
           // Send message to selected users

            String message = messageEdit.getText().toString();
            // Check if the message is empty
            if(message.isEmpty()){
                Toast.makeText(getContext(), "You must first write a message!", Toast.LENGTH_SHORT).show();
            }else{
                for(int i = 0; i < currentList.size(); i++){
                    firebaseController.updateUserNotifications(currentList.get(i),new Notification("Message from " + appUser.getFirstName() , message, Calendar.getInstance().getTime(), selectedEvent, selectedEvent.getNumberOfNotifications()));
                }
            }
        });

        return builder.create();
    }
}
