package com.example.soroban.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.soroban.FireBaseController;
import com.example.soroban.R;
import com.example.soroban.model.Event;
import com.example.soroban.model.Notification;
import com.example.soroban.model.User;

import java.util.ArrayList;
import java.util.Calendar;

public class AcceptInviteFragment extends DialogFragment {
    private Event selectedEvent;
    private User appUser;
    private FireBaseController fireBaseController;
    private DialogFragmentListener listener;

    public static AcceptInviteFragment newInstance(Event selectedEvent, User appUser) {

        Bundle args = new Bundle();
        args.putSerializable("selectedEvent", selectedEvent);
        args.putSerializable("appUser", appUser);

        AcceptInviteFragment fragment = new AcceptInviteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof DialogFragmentListener){
            listener = (DialogFragmentListener) context;
        }else{
            throw new RuntimeException(context + "must implement DialogFragmentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = getLayoutInflater().inflate(R.layout.user_accept_invitation, null);

        fireBaseController = new FireBaseController(getContext());

        Bundle args = getArguments();

        // Initialize appUser and selected Event for this fragment.
        if(args != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                selectedEvent = args.getSerializable("selectedEvent", Event.class);
                appUser = args.getSerializable("appUser", User.class);
            }else{
                appUser = (User) args.getSerializable("appUser");
                selectedEvent = (Event) args.getSerializable("selectedEvent");
            }

            if(appUser == null || selectedEvent == null){
                throw new IllegalArgumentException("Must pass object of type User and Event to initialize appUser.");
            }else{
                selectedEvent = appUser.getInvitedEvents().find(selectedEvent);
            }

        }else{
            throw new IllegalArgumentException("Must pass arguments to initialize this activity.");
        }

        ImageButton cancel = view.findViewById(R.id.closeInvitation);

        cancel.setOnClickListener(v -> {
            this.dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Accept Invitation?");
        builder.setView(view);
        builder.setNegativeButton("Reject", (dialog, which) -> {
            //User rejects invitation

            // Perform local changes
            selectedEvent.removeFromInvited(selectedEvent.getInvitedEntrants().find(appUser));
            listener.update();

            // Perform Firebase changes
            fireBaseController.updateThoseNotGoing(selectedEvent, appUser);
            fireBaseController.removeInvitedDoc(selectedEvent, appUser);

            // Re-sample a new user
            ArrayList<Integer> reSampledIndices = selectedEvent.sampleEntrants(1); // Re-sample one user

            // If a User was re-sampled
            if(reSampledIndices.size() == 1){
                User user = selectedEvent.getInvitedEntrants().get(0);
                fireBaseController.updateInvited(selectedEvent, user);
                fireBaseController.updateUserInvited(user, selectedEvent);
                fireBaseController.removeFromWaitListDoc(selectedEvent, user);

                // Update model class
                selectedEvent.addInvited(user);
                selectedEvent.removeFromWaitingEntrants(user);

                // Notify invited entrant that they have been re-sampled
                Notification newNotif = new Notification("You have be re-sampled!", "", Calendar.getInstance().getTime(), selectedEvent, selectedEvent.getNumberOfNotifications());
                fireBaseController.updateUserNotifications(user, newNotif);
            }
            // Else there are no other waiting entrants
        });
        builder.setPositiveButton("Accept", (dialog, which) -> {
            //User accepts invitation

            // Perform local changes
            selectedEvent.removeFromInvited(selectedEvent.getInvitedEntrants().find(appUser));
            selectedEvent.addAttendee(selectedEvent.getInvitedEntrants().find(appUser));
            listener.update();

            // Perform Firebase changes
            fireBaseController.updateUserRegistered(appUser, selectedEvent);
            fireBaseController.updateAttendees(selectedEvent, appUser);
            fireBaseController.removeInvitedDoc(selectedEvent, appUser);
            listener.update();
        });

        return builder.create();
    }

}
