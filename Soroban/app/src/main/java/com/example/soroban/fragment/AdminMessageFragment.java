package com.example.soroban.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.activity.AdminBrowseFacilityActivity;
import com.example.soroban.activity.AdminViewFacilityActivity;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.Notification;
import com.example.soroban.model.User;
import com.example.soroban.model.UserList;

import java.util.Calendar;
import java.util.Objects;

/**
 * Provides a dialog for sending administrative messages to facility owners
 * regarding violations or other important notifications.
 *
 * @see FireBaseController
 * @see Facility
 * @see Notification
 * @see AdminBrowseFacilityActivity
 */
public class AdminMessageFragment extends DialogFragment {
    private Facility selectedFacility;
    private User appUser;
    private FireBaseController firebaseController;
    private DialogFragmentListener listener;

    public static AdminMessageFragment newInstance(Facility selectedFacility, User appUser) {

        Bundle args = new Bundle();
        args.putSerializable("selectedFacility", selectedFacility);
        args.putSerializable("appUser", appUser);

        AdminMessageFragment fragment = new AdminMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.organizer_event_send_message, null);z

        Bundle args = getArguments();

        // Initialize appUser and selected Facility for this fragment.
        if(args != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                selectedFacility = args.getSerializable("selectedFacility", Facility.class);
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
                selectedFacility = (Facility) args.getSerializable("selectedFacility");
            }

            if(appUser == null || selectedFacility == null){
                throw new IllegalArgumentException("Must pass object of type User and Event to initialize appUser.");
            }

        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        firebaseController = new FireBaseController(getContext());
        TextView titleText = view.findViewById(R.id.setMessageTitle);
        EditText messageEdit = view.findViewById(R.id.organizerMessageEdit);
        titleText.setVisibility(View.INVISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        builder.setTitle("What Violation has the Facility Violated? ");
        builder.setView(view);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Send", (dialog, which) -> {
            // Send message to selected users
            String message = "Violation: " + messageEdit.getText().toString();
            // Check if the message is empty
            if(message.isEmpty()){
                Toast.makeText(getContext(), "You must first write a message!", Toast.LENGTH_SHORT).show();
            }else{
                firebaseController.updateUserNotificationsAdmin(selectedFacility.getOwner(), new Notification("Your Facility Has Been Deleted", message, Calendar.getInstance().getTime(), selectedFacility), "facilityDelete");
                firebaseController.removeFacilityDoc(selectedFacility);
                Intent intent;
                intent = new Intent(getContext(), AdminBrowseFacilityActivity.class);
                Bundle argsEvent = new Bundle();
                argsEvent.putSerializable("appUser", appUser);
                intent.putExtras(argsEvent);
                startActivity(intent);
            }

        });




        return builder.create();
    }

}
