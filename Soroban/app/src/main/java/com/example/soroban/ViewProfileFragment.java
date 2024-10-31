package com.example.soroban;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

            Dialog newDialog = super.onCreateDialog(savedInstanceState);
            newDialog.setContentView(view);

            // Reference: https://stackoverflow.com/questions/7189948/full-screen-dialogfragment-in-android
            newDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            Serializable user = args.getSerializable("appUser");


            // Check if the app user was passed as an argument
            if(user != null){
                User appUser= (User)user;

                // Update views to have correct info

                firstNameText.setText(appUser.getFirstName());
                lastNameText.setText(appUser.getLastName());
                emailText.setText(appUser.getEmail());
                phoneNumberText.setText(String.valueOf(appUser.getPhoneNumber()));

                // Set up listeners for buttons
                FloatingActionButton editButton = view.findViewById(R.id.floatingEditButton);
                FloatingActionButton backButton = view.findViewById(R.id.floatingBackButton);


                editButton.setOnClickListener(v -> {
                    ManageProfileFragment dialogFragment = ManageProfileFragment.newInstance(appUser);
                    newDialog.dismiss();
                    dialogFragment.show(getParentFragmentManager(), "Manage profile");
                });

                backButton.setOnClickListener(v -> {
                    newDialog.dismiss();
                });


            }else{
                throw new RuntimeException("Must pass User object to populate fields.");
            }
            return newDialog;

        }else{
            throw new RuntimeException("Instantiate ViewProfileFragment by using newInstance() method.");
        }
    }

}
