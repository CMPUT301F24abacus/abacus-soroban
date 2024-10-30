package com.example.soroban;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.Serializable;

public class ViewProfileFragment extends DialogFragment{

    public static ViewProfileFragment newInstance(@Nullable User user){
        Bundle args = new Bundle();
        args.putSerializable("appUser", user);

        ViewProfileFragment newFragment = new ViewProfileFragment();
        newFragment.setArguments(args);
        return newFragment;
    }

    @SuppressLint({"DialogFragmentCallbacksDetector", "SetTextI18n"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = getLayoutInflater().inflate(R.layout.user_profile_view, null);

        // Get references to all views that will display a profile's information.
        TextView firstNameText = view.findViewById(R.id.user_FirstNameField);
        TextView lastNameText = view.findViewById(R.id.user_LastNameField);
        TextView emailText = view.findViewById(R.id.user_emailAddressField);
        TextView phoneNumberText = view.findViewById(R.id.user_phoneNumberField);

        Bundle args = getArguments();
        if(args != null){

            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(true);

            // Reference: https://stackoverflow.com/questions/7189948/full-screen-dialogfragment-in-android
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);

            Serializable user = args.getSerializable("appUser");

            // Check if the app user was passed as an argument
            if(user != null){
                User appUser= (User)user;

                // Update views to have correct info

                firstNameText.setText(appUser.getName());
                lastNameText.setText(appUser.getName());
                emailText.setText(appUser.getEmail());
                phoneNumberText.setText(String.valueOf(appUser.getPhoneNumber()));

            }else{
                throw new RuntimeException("Must pass User object to populate fields.");
            }

            return dialog;

        }else{
            throw new RuntimeException("Instantiate ViewProfileFragment by using newInstance() method.");
        }
    }

}
