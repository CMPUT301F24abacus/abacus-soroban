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

/**
 * Provides a dialog for organizers to confirm whether an event should require location access.
 *
 * @author Matthieu Larochelle
 * @see DialogFragmentListener
 */
public class ConfirmRequireLocationFragment extends DialogFragment {
    private DialogFragmentListener listener;

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
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.organizer_require_location, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setNegativeButton("No", (dialog, which) -> {
            listener.returnResult(false);
        });
        builder.setPositiveButton("Yes", (dialog, which) -> {
            listener.returnResult(true);
        });

        return builder.create();
    }
}